package com.aau.grouping_system.Session;

import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.User.Coordinator.Coordinator;
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
		return SessionFactory.create(db, coordinator.sessions, coordinator, sessionName);
	}

	public Session getSession(String sessionId) {
		return db.getSessions().getItem(sessionId);
	}

	@SuppressWarnings("unchecked") // Suppress in-editor warnings about type safety violations because it isn't
																	// true here because Java's invariance of generics.
	public CopyOnWriteArrayList<Session> getSessionsByCoordinator(Coordinator coordinator) {
		return (CopyOnWriteArrayList<Session>) coordinator.sessions.getItems(db);
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

	// 1) Tilføj selv parametre til den data, som du har ekstraheret i
	// StudentController.java.
	public void applySetup(Session session) {

		// 2) Lige nu er din variabel "studentEmails" bare en lang String.
		// Hver email er separeret af en "\n" (AKA new line character).
		// Du skal nu finde en måde at opdele hver email i deres eget String-objekt,
		// som du så indsætter i en liste (brug CopyOnWriteArrayList<String>).

		// 3) Gør det samme med supervisorEmails.

		// 4) Lav et enhanced for-loop, som cycler igennem hver email i din liste af
		// student emails.
		// For hver email skal du bare lave et nyt Student-objekt (så "new
		// Student(.......)").
		// Dette tilføjer automatisk disse students til din session.
		// Hint: Tjek "fillDatabaseWithExampleData"-funktionen i
		// "DatabaseSerializer.java". Her er nemlig et eksempel på, at jeg laver nye
		// Student-objekter.

		// 5) Gør det samme med din liste af supervisor emails.

		// 6) Sæt resten af den nye data ind i din session.
		// Hint: Er meget simpelt. Bare gør, fx "session.setName(newName)"

	}

}