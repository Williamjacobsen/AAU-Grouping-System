package com.aau.grouping_system.SessionPage;

import org.springframework.stereotype.Service;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.EnhancedMap.EnhancedMap;
import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.Sessions.Session;
import com.aau.grouping_system.Sessions.SessionFactory;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;

@Service
public class SessionPageService {

	private final Database db;

	public SessionPageService(Database db) {
		this.db = db;
	}

	public Session createSession(String sessionName, Coordinator coordinator) {
		EnhancedMap<Supervisor> supervisors = new EnhancedMap<>();
		EnhancedMap<Student> students = new EnhancedMap<>();
		EnhancedMap<Project> projects = new EnhancedMap<>();
		EnhancedMap<Group> groups = new EnhancedMap<>();

		Session newSession = SessionFactory.create(coordinator, supervisors, students, projects, groups);
		
		coordinator.sessions.put(newSession);
		
		return newSession;
	}

	public Session getSession(Integer sessionId) {
		for (Coordinator coordinator : db.getCoordinators().getAllEntries().values()) {
			Session session = coordinator.sessions.getEntry(sessionId);
			if (session != null) {
				return session;
			}
		}
		return null;
	}

	public EnhancedMap<Session> getSessionsByCoordinator(Coordinator coordinator) {
		return coordinator.sessions;
	}

	public boolean deleteSession(Integer sessionId, Coordinator coordinator) {
		Session session = coordinator.sessions.getEntry(sessionId);
		if (session != null && session.getCoordinator().equals(coordinator)) {
			coordinator.sessions.remove(session);
			return true;
		}
		return false;
	}

	public boolean hasPermission(Integer sessionId, Coordinator coordinator) {
		Session session = coordinator.sessions.getEntry(sessionId);
		return session != null && session.getCoordinator().equals(coordinator);
	}
}