package com.aau.grouping_system.User.Coordinator;

import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.item.DatabaseItem;
import com.aau.grouping_system.Database.item.ItemReferenceList;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.User;

public class Coordinator extends User {

	public ItemReferenceList<Session> sessions;

	// constructors

	public Coordinator(DatabaseMap<? extends DatabaseItem> parentDatabaseMap,
			ItemReferenceList<? extends DatabaseItem> parentReferenceList,
			String email, String password, String name) {
		super(parentDatabaseMap, parentReferenceList, email, password, name);
		this.sessions = new ItemReferenceList<>(this);
	}

	// abstract method overrides

	@Override
	public Role getRole() {
		return Role.COORDINATOR;
	}

}
