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
import com.aau.grouping_system.User.SessionMember.Student.Student;
import com.aau.grouping_system.User.SessionMember.Supervisor.Supervisor;
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
		Session newSession = db.getSessions().addItem(
				db,
				coordinator.getSessions(),
				new Session(db, coordinator, sessionName));
		return newSession;
	}

	public Session getSession(String sessionId) {
		return db.getSessions().getItem(sessionId);
	}

	@SuppressWarnings("unchecked") // Type-safety violations aren't true here.
	public CopyOnWriteArrayList<Session> getSessionsByCoordinator(Coordinator coordinator) {
		return (CopyOnWriteArrayList<Session>) coordinator.getSessions().getItems(db);
	}

	public boolean deleteSession(String sessionId, Coordinator coordinator) {
		if (!isUserAuthorizedSession(sessionId, coordinator)) {
			return false;
		}

		db.getSessions().cascadeRemoveItem(db, sessionId);
		return true;
	}

	@SuppressWarnings("unchecked") // Type-safety violations aren't true here.
	public CopyOnWriteArrayList<Student> getStudentsBySessionId(String sessionId) {
		Session s = getSession(sessionId);
		if (s == null)
			return new CopyOnWriteArrayList<>();
		return (CopyOnWriteArrayList<Student>) s.getStudents().getItems(db);
	}

	@SuppressWarnings("unchecked") // Type-safety violations aren't true here.
	public CopyOnWriteArrayList<Supervisor> getSupervisorsBySessionId(String sessionId) {
		Session s = getSession(sessionId);
		if (s == null)
			return new CopyOnWriteArrayList<>();
		return (CopyOnWriteArrayList<Supervisor>) s.getSupervisors().getItems(db);
	}

	public boolean isQuestionnaireDeadlineExceeded(Session session) {
		if (session == null)
			return false;
		LocalDateTime deadline = session.getQuestionnaireDeadline();
		if (deadline == null)
			return false; // unparsable => treat as not exceeded
		return LocalDateTime.now().isAfter(deadline);
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
}
