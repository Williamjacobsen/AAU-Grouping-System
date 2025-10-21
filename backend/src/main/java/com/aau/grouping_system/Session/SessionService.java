package com.aau.grouping_system.Session;

import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.User.Coordinator.Coordinator;

@Service
public class SessionService {

	private final Database db;

	public SessionService(Database db) {
		this.db = db;
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
		if (!hasPermission(sessionId, coordinator)) {
			return false;
		}

		db.getSessions().remove(db, sessionId);
		return true;
	}

	public boolean hasPermission(String sessionId, Coordinator coordinator) {
		Session session = db.getSessions().getItem(sessionId);
		return session != null && session.getCoordinatorId().equals(coordinator.getId());
	}

	public boolean isAuthorized(Coordinator coordinator, String sessionId) {
		return coordinator != null && hasPermission(sessionId, coordinator);
	}
}