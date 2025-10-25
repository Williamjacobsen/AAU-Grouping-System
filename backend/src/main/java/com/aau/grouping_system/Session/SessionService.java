package com.aau.grouping_system.Session;

import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.User;

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
		if (!isCoordinatorAuthorized(sessionId, coordinator)) {
			return false;
		}

		db.getSessions().cascadeRemove(db, sessionId);
		return true;
	}

	public Boolean isCoordinatorAuthorized(String sessionId, Coordinator coordinator) {
		if (coordinator == null || !db.getSessions().getItem(sessionId).coordinatorId.equals(coordinator.getId())) {
			return false;
		} else {
			return true;
		}
	}

	public Boolean isUserAuthorized(String sessionId, User user) {

		if (user == null) {
			return false;
		}

		switch (user.getRole()) {
			case User.Role.Coordinator:
				if (!db.getSessions().getItem(sessionId).coordinatorId.equals(user.getId())) {
					return false;
				}
				break;
			case User.Role.Supervisor:
				if (!db.getSupervisors().getItem(user.getId()).getSessionId().equals(sessionId)) {
					return false;
				}
				break;
			case User.Role.Student:
				if (!db.getStudents().getItem(user.getId()).getSessionId().equals(sessionId)) {
					System.out.println("------------- STUDENT DENIED");
					System.out.println("Incoming session id: " + sessionId);
					System.out.println("Stored   session id: " + db.getStudents().getItem(user.getId()).getSessionId());
					return false;
				}
				break;
			default:
				return false;
		}
		return true;
	}

}