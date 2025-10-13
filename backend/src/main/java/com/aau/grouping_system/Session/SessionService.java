package com.aau.grouping_system.Session;

import org.springframework.stereotype.Service;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.item.ItemReferenceList;
import com.aau.grouping_system.User.Coordinator.Coordinator;

@Service
public class SessionService {

	private final Database db;

	public SessionService(Database db) {
		this.db = db;
	}

	public Session createSession(String sessionName, Coordinator coordinator) {
		return SessionFactory.create(db.getSessions(), coordinator.sessions, coordinator);
	}

	public Session getSession(Integer sessionId) {
		return db.getSessions().getItem(sessionId);
	}

	public ItemReferenceList<Session> getSessionsByCoordinator(Coordinator coordinator) {
		return coordinator.sessions;
	}

	public boolean deleteSession(Integer sessionId, Coordinator coordinator) {
		if (!hasPermission(sessionId, coordinator)) {
			return false;
		}

		db.getSessions().remove(sessionId);
		return true;
	}

	public boolean hasPermission(Integer sessionId, Coordinator coordinator) {
		Session session = db.getSessions().getItem(sessionId);
		return session != null && session.getCoordinator().equals(coordinator);
	}
}