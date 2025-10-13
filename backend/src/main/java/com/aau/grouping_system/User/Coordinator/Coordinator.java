package com.aau.grouping_system.User.Coordinator;

import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.databaseMapItem.DatabaseMapItem;
import com.aau.grouping_system.Database.databaseMapItem.DatabaseMapItemReferenceList;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.User;

public class Coordinator extends User {

	public DatabaseMapItemReferenceList<Session> sessions;

	// constructors

	public Coordinator(DatabaseMap<? extends DatabaseMapItem> parentDatabaseMap,
			DatabaseMapItemReferenceList<? extends DatabaseMapItem> parentReferenceList,
			String email, String password, String name) {
		super(parentDatabaseMap, parentReferenceList, email, password, name);
		this.sessions = new DatabaseMapItemReferenceList<>(this);
	}

	// abstract method overrides

	@Override
	public Role getRole() {
		return Role.COORDINATOR;
	}

}
