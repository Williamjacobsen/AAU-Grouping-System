package com.aau.grouping_system.User.Supervisor;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Database.DatabaseItemChildGroup;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.User;

public class Supervisor extends User {

	private String sessionId;
	private Integer maxGroups = 1; // Default to 1 group

	public Supervisor(Database db, DatabaseItemChildGroup parentItemChildIdList,
			String email, String passwordHash, String name, Session session) {
		super(db, parentItemChildIdList, email, passwordHash, name);
		this.sessionId = session.getId();
	}

	@Override
	protected DatabaseMap<? extends DatabaseItem> getDatabaseMap(Database db) {
		return db.getSupervisors();
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
