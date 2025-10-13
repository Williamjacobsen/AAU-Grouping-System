package com.aau.grouping_system.User.Coordinator;

import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.DatabaseReferences;
import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.User.User;

public class Coordinator extends User {

	public DatabaseReferences sessions;

	// constructors

	public Coordinator(DatabaseMap<? extends DatabaseItem> parentMap,
			DatabaseReferences parentReferences, Database db,
			String email, String passwordHash, String name) {
		super(parentMap, parentReferences, email, passwordHash, name);
		this.sessions = new DatabaseReferences(db.getSessions(), this);
	}

	// abstract method overrides

	@Override
	public Role getRole() {
		return Role.COORDINATOR;
	}

}
