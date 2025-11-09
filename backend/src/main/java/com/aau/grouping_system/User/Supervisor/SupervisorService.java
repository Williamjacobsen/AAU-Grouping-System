package com.aau.grouping_system.User.Supervisor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Session.Session;

@Service
public class SupervisorService {

	private final Database db;
	private final PasswordEncoder passwordEncoder;

	public SupervisorService(Database db, PasswordEncoder passwordEncoder) {
		this.db = db;
		this.passwordEncoder = passwordEncoder;
	}

	public Supervisor addSupervisor(Session session, String email, String password, String name) {
		String passwordHash = passwordEncoder.encode(password);
		return new Supervisor(db, session.getSupervisors(), email, passwordHash, name, session);
	}
}
