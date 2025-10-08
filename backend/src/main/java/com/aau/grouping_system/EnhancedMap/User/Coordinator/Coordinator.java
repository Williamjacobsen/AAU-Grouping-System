package com.aau.grouping_system.EnhancedMap.User.Coordinator;

import com.aau.grouping_system.EnhancedMap.EnhancedMap;
import com.aau.grouping_system.EnhancedMap.EnhancedMapItem;
import com.aau.grouping_system.EnhancedMap.EnhancedMapItemReferenceList;
import com.aau.grouping_system.EnhancedMap.Session.Session;
import com.aau.grouping_system.EnhancedMap.User.User;

public class Coordinator extends User {

	public EnhancedMapItemReferenceList<Session> sessions;

	// constructors

	public Coordinator(EnhancedMap<? extends EnhancedMapItem> parentMap, String email, String password, String name) {
		super(parentMap, email, password, name);
		this.sessions = new EnhancedMapItemReferenceList<>(this);
	}

	// abstract method overrides

	@Override
	public Role getRole() {
		return Role.COORDINATOR;
	}

}
