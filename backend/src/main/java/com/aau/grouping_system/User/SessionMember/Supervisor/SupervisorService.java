package com.aau.grouping_system.User.SessionMember.Supervisor;

import org.springframework.stereotype.Service;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Session.Session;

@Service
public class SupervisorService {

	private final Database db;

	public SupervisorService(
			Database db) {
		this.db = db;
	}

	public Supervisor addSupervisor(Session session, String email, String password, String name) {
		Supervisor newSupervisor = db.getSupervisors().addItem(
				session.getSupervisors(),
				new Supervisor(email, name, session));
		return newSupervisor;
	}
}
