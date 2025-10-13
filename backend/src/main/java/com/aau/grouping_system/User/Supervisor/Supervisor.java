package com.aau.grouping_system.User.Supervisor;

import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.databaseMapItem.DatabaseMapItem;
import com.aau.grouping_system.Database.databaseMapItem.DatabaseMapItemReferenceList;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.User;

public class Supervisor extends User {

	Session session;

	// constructors

	public Supervisor(DatabaseMap<? extends DatabaseMapItem> parentDatabaseMap,
			DatabaseMapItemReferenceList<? extends DatabaseMapItem> parentReferenceList,
			String email, String passwordHash, String name, Session session) {
		super(parentDatabaseMap, parentReferenceList, email, passwordHash, name);
		this.session = session;
	}

	// abstract method overrides

	@Override
	public Role getRole() {
		return Role.SUPERVISOR;
	}

}
