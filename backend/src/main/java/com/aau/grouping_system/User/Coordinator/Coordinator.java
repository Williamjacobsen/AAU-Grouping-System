package com.aau.grouping_system.User.Coordinator;

import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.DatabaseItemChildGroup;
import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.User.User;

public class Coordinator extends User {

	private DatabaseItemChildGroup sessions;

	public Coordinator(Database db, DatabaseItemChildGroup parentItemChildIdList,
			String email, String passwordHash, String name) {
		super(db, parentItemChildIdList, email, passwordHash, name);
		this.sessions = new DatabaseItemChildGroup(db.getSessions(), this);
	}

	@Override
	protected DatabaseMap<? extends DatabaseItem> getDatabaseMap(Database db) {
		return db.getCoordinators();
	}

	@Override
	public Role getRole() {
		return Role.Coordinator;
	}

	// @formatter:off
	public DatabaseItemChildGroup getSessions() { return sessions; }
	public void setSessions(DatabaseItemChildGroup sessions) { this.sessions = sessions; }
}
