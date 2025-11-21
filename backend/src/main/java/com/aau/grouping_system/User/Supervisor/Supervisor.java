package com.aau.grouping_system.User.Supervisor;

import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.User;

public class Supervisor extends User {

	private String sessionId;
	private Integer maxGroups = 1; // Default to 1 group

	public Supervisor(String email, String passwordHash, String name, Session session) {
		super(email, passwordHash, name);
		this.sessionId = session.getId();
	}

	@Override
	public Role getRole() {
		return Role.Supervisor;
	}

	// @formatter:off
	public String getSessionId() { return sessionId; }
	public Integer getMaxGroups() { return maxGroups; }
	public void setMaxGroups(Integer maxGroups) { this.maxGroups = maxGroups; }
}
