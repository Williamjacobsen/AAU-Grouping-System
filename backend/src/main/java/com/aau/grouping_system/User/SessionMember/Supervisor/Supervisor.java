package com.aau.grouping_system.User.SessionMember.Supervisor;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.SessionMember.SessionMember;

public class Supervisor extends SessionMember {

	private Integer maxGroups = 1; // Default to 1 group

	public Supervisor(String email, String name, Session session) {
		super(email, name, session);
	}

	@Override
	protected void onCascadeRemove(Database db) {
		// Disconnect from linked groups
		Session session = db.getSessions().getItem(this.getSessionId());
		for (Group group : db.getGroups().getItems(session.getGroups().getIds())) {
			if (group.getSupervisorId().equals(this.getId())) {
				group.setSupervisorId(null);
			}
		}
	}

	@Override
	public Role getRole() {
		return Role.Supervisor;
	}

	// @formatter:off
	public Integer getMaxGroups() { return maxGroups; }
	public void setMaxGroups(Integer maxGroups) { this.maxGroups = maxGroups; }
}
