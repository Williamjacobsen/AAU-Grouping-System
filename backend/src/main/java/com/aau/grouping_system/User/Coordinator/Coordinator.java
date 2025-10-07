package com.aau.grouping_system.User.Coordinator;

import com.aau.grouping_system.EnhancedMap.EnhancedMap;
import com.aau.grouping_system.EnhancedMap.EnhancedMapItem;
import com.aau.grouping_system.EnhancedMap.EnhancedMapReference;
import com.aau.grouping_system.Sessions.Session;
import com.aau.grouping_system.User.User;

public class Coordinator extends User {

	public EnhancedMapReference<Session> sessions = new EnhancedMapReference<>(this);

	// constructors

	public Coordinator(EnhancedMap<EnhancedMapItem> parentMap, String email, String password, String name) {
		super(parentMap, email, password, name);
	}

	// abstract method overrides

	@Override
	public Role getRole() {
		return Role.COORDINATOR;
	}

}
