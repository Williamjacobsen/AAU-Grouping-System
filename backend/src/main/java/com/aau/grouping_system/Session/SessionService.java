package com.aau.grouping_system.Session;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.Authentication.AuthService;

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

	public void applySetup(Session session, String name, String description, String studentEmails, 
													String supervisorEmails, String coordinatorName, String questionnaireDeadline, String initialProjects, 
													String optionalQuestionnaire, int groupSize) {

		List<String> studentEmailList = new CopyOnWriteArrayList<>(
			Arrays.asList(studentEmails.split("\\r?\\n")));

		List<String> supervisorEmailList = new CopyOnWriteArrayList<>(
			Arrays.asList(supervisorEmails.split("\\r?\\n")));

		for (String email : studentEmailList) {
			if (!email.trim().isEmpty()) {
				Student student = new Student(db, session, email.trim());
				session.addStudent(student);
			}

		for (String email : supervisorEmailList) {
			if (!email.trim().isEmpty()) {
				Supervisor supervisor = new Supervisor(db, session, email.trim());
				session.addSupervisor(supervisor);
			}
		}
		session.setName(name);
    session.setDescription(description);
    session.setCoordinatorName(coordinatorName);
    session.setQuestionnaireDeadline(questionnaireDeadline);
    session.setInitialProjects(initialProjects);
    session.setOptionalQuestionnaire(optionalQuestionnaire);
		session.setGroupSize(groupSize);
		sessionRepository.save(session);
	}
}
}