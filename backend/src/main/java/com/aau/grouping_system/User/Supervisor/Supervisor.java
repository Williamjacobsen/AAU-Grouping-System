package com.aau.grouping_system.User.Supervisor;

import com.aau.grouping_system.EnhancedMap.EnhancedMap;
import com.aau.grouping_system.EnhancedMap.EnhancedMapItem;
import com.aau.grouping_system.User.User;

public class Supervisor extends User {

	// constructors

	public Supervisor(EnhancedMap<EnhancedMapItem> parentMap, String email, String passwordHash, String name) {
		super(parentMap, email, passwordHash, name);
	}

	// abstract method overrides

	@Override
	public Role getRole() {
		return Role.SUPERVISOR;
	}

}
