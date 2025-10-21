package com.aau.grouping_system.User.Coordinator;

import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.DatabaseItemChildList;
import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.User.User;

public class Coordinator extends User {

	public DatabaseItemChildList sessions;

	// constructors

	public Coordinator(Database db, DatabaseItemChildList parentItemChildIdList,
			String email, String passwordHash, String name) {
		super(db, parentItemChildIdList, email, passwordHash, name);
		this.sessions = new DatabaseItemChildList(db.getSessions(), this);
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
