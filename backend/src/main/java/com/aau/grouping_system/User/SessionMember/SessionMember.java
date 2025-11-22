package com.aau.grouping_system.User.SessionMember;

import java.util.UUID;

import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.User;

public abstract class SessionMember extends User {

	private String sessionId;

	/// Upon construction, the SessionMembers password is randomized to prevent
	/// logins until the user has been emailed a new password.
	public SessionMember(String email, String name, Session session) {
		super(
				email,
				UUID.randomUUID().toString(), // Random password
				name);
		this.sessionId = session.getId();
	}

	// @formatter:off
	public String getSessionId() { return sessionId; }
}