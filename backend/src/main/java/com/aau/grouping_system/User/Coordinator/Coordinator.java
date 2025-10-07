package com.aau.grouping_system.User.Coordinator;

import java.util.concurrent.CopyOnWriteArrayList;

import com.aau.grouping_system.Sessions.Session;
import com.aau.grouping_system.User.User;

public class Coordinator extends User {

	public CopyOnWriteArrayList<Session> sessions;

	@Override
	protected void initializeChildMapReferences() {
		childMapReferences.add(sessions);
	}

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
