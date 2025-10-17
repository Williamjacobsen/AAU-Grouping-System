package com.aau.grouping_system.User.Coordinator;

import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.DatabaseIdList;
import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.User.User;

public class Coordinator extends User {

	public DatabaseIdList sessions;

	// constructors

	public Coordinator(Database db, DatabaseIdList parentItemChildIdList,
			String email, String passwordHash, String name) {
		super(db, parentItemChildIdList, email, passwordHash, name);
		this.sessions = new DatabaseIdList(db.getSessions(), this);
	}

	// abstract method overrides

	@Override
	protected DatabaseMap<? extends DatabaseItem> getDatabaseMap(Database db) {
		return db.getCoordinators();
	}

	@Override
	public Role getRole() {
		return Role.COORDINATOR;
	}

}
