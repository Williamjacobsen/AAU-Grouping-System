package com.aau.grouping_system.EnhancedMap.User.Supervisor;

import com.aau.grouping_system.EnhancedMap.EnhancedMap;
import com.aau.grouping_system.EnhancedMap.EnhancedMapItem;
import com.aau.grouping_system.EnhancedMap.Session.Session;
import com.aau.grouping_system.EnhancedMap.User.User;

public class Supervisor extends User {

	Session session;

	// constructors

	public Supervisor(EnhancedMap<? extends EnhancedMapItem> parentMap, String email, String passwordHash, String name,
			Session session) {
		super(parentMap, email, passwordHash, name);
		this.session = session;
	}

	// abstract method overrides

	@Override
	public Role getRole() {
		return Role.SUPERVISOR;
	}

}
