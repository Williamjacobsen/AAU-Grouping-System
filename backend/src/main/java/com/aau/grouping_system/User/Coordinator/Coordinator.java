package com.aau.grouping_system.User.Coordinator;

import com.aau.grouping_system.EnhancedMap.EnhancedMap;
import com.aau.grouping_system.Sessions.Session;
import com.aau.grouping_system.User.User;

public class Coordinator extends User {

	public EnhancedMap<Session> sessions = new EnhancedMap<>();

	// constructors

	public Coordinator(String email, String password, String name) {
		super(email, password, name);
	}

	// abstract method overrides

	@Override
	public Role getRole() {
		return Role.COORDINATOR;
	}

}
