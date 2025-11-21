package com.aau.grouping_system.User.SessionMember.Supervisor;

import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.SessionMember.SessionMember;

public class Supervisor extends SessionMember {

	private Integer maxGroups = 1; // Default to 1 group

	public Supervisor(String email, String name, Session session) {
		super(email, name, session);
	}

	@Override
	public Role getRole() {
		return Role.Supervisor;
	}

	// @formatter:off
	public Integer getMaxGroups() { return maxGroups; }
	public void setMaxGroups(Integer maxGroups) { this.maxGroups = maxGroups; }
}
