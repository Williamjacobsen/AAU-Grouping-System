package com.aau.grouping_system.Session;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;

import com.aau.grouping_system.Authentication.AuthService;
import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;
import com.aau.grouping_system.User.User;

@Service
public class SessionService {

	private final Database db;
	private final AuthService authService;

	public SessionService(Database db, AuthService authService) {
		this.db = db;
		this.authService = authService;
	}

	public Session createSession(String sessionName, Coordinator coordinator) {
		return SessionFactory.create(db, coordinator.getSessions(), coordinator, sessionName);
	}

	public Session getSession(String sessionId) {
		return db.getSessions().getItem(sessionId);
	}

	@SuppressWarnings("unchecked") // Suppress in-editor warnings about type safety violations because it isn't
																	// true here because Java's invariance of generics.
	public CopyOnWriteArrayList<Session> getSessionsByCoordinator(Coordinator coordinator) {
		return (CopyOnWriteArrayList<Session>) coordinator.getSessions().getItems(db);
	}

	public boolean deleteSession(String sessionId, Coordinator coordinator) {
		if (!isUserAuthorizedSession(sessionId, coordinator)) {
			return false;
		}

		db.getSessions().cascadeRemove(db, sessionId);
		return true;
	}

	@SuppressWarnings("unchecked")
  public CopyOnWriteArrayList<Student> getStudentsBySessionId(String sessionId) {
        Session s = getSession(sessionId);
        if (s == null) return new CopyOnWriteArrayList<>();
        return (CopyOnWriteArrayList<Student>) s.getStudents().getItems(db);
    }

  @SuppressWarnings("unchecked")
    public CopyOnWriteArrayList<Supervisor> getSupervisorsBySessionId(String sessionId) {
        Session s = getSession(sessionId);
        if (s == null) return new CopyOnWriteArrayList<>();
        return (CopyOnWriteArrayList<Supervisor>) s.getSupervisors().getItems(db);
    }
	public boolean isQuestionnaireDeadlineExceeded(Session session) {
        if (session == null) return false;
        String raw = session.getQuestionnaireDeadline();
        if (raw == null || raw.isBlank()) return false; // no deadline set => not exceeded
        LocalDateTime deadline = parseDeadline(raw);
        if (deadline == null) return false;             // unparsable => treat as not exceeded
        return LocalDateTime.now().isAfter(deadline);
    }

    // ADD: robust parser that accepts several common formats
    private LocalDateTime parseDeadline(String s) {
        try { return LocalDateTime.parse(s); } catch (DateTimeParseException ignored) {}
        try { return LocalDate.parse(s).atTime(23, 59, 59); } catch (DateTimeParseException ignored) {}
        try { return OffsetDateTime.parse(s).toLocalDateTime(); } catch (DateTimeParseException ignored) {}
        try { return Instant.parse(s).atZone(ZoneId.systemDefault()).toLocalDateTime(); } catch (DateTimeParseException ignored) {}
        return null;
    }

	private Boolean isUserAuthorizedSession(String sessionId, User user, User.Role[] authorizedRoles) {

		if (user == null || !authService.hasAuthorizedRole(user, authorizedRoles)) {
			return false;
		}

		switch (user.getRole()) {
			case User.Role.Coordinator:
				return db.getSessions().getItem(sessionId).getCoordinatorId().equals(user.getId());
			case User.Role.Supervisor:
				return db.getSupervisors().getItem(user.getId()).getSessionId().equals(sessionId);
			case User.Role.Student:
				return db.getStudents().getItem(user.getId()).getSessionId().equals(sessionId);
			default:
				return false;
		}
	}

	public Boolean isUserAuthorizedSession(String sessionId, User user) {
		User.Role[] authorizedRoles = { User.Role.Coordinator, User.Role.Supervisor, User.Role.Student };
		return isUserAuthorizedSession(sessionId, user, authorizedRoles);
	}

	public Boolean isUserAuthorizedSession(String sessionId, Coordinator coordinator) {
		User.Role[] authorizedRoles = { User.Role.Coordinator };
		return isUserAuthorizedSession(sessionId, coordinator, authorizedRoles);
	}

	public void applySetup(
            Session session,
            String name,
            String description,
            String studentEmails,
            String supervisorEmails,
            String coordinatorName,
            String questionnaireDeadline,
            String initialProjects,
            String optionalQuestionnaire,
            int groupSize) {

        if (session == null) return;

        List<String> studentEmailList = Arrays.stream(
                studentEmails == null ? new String[0] : studentEmails.split("\\r?\\n"))
                .map(String::trim).filter(s -> !s.isEmpty()).toList();

        List<String> supervisorEmailList = Arrays.stream(
                supervisorEmails == null ? new String[0] : supervisorEmails.split("\\r?\\n"))
                .map(String::trim).filter(s -> !s.isEmpty()).toList();

        for (String email : studentEmailList) {
            new Student(db, session.getStudents(), email, "", "Student", session);
        }

        for (String email : supervisorEmailList) {
            new Supervisor(db, session.getSupervisors(), email, "", "Supervisor", session);
        }

        session.setName(name);
        session.setDescription(description);
        session.setCoordinatorName(coordinatorName);
        session.setQuestionnaireDeadline(questionnaireDeadline);
        session.setInitialProjects(initialProjects);
        session.setOptionalQuestionnaire(optionalQuestionnaire);
        session.setGroupSize(groupSize);
    }
}
