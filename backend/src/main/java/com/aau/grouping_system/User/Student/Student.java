package com.aau.grouping_system.User.Student;

import com.aau.grouping_system.EnhancedMap.EnhancedMap;
import com.aau.grouping_system.EnhancedMap.EnhancedMapItem;
import com.aau.grouping_system.EnhancedMap.EnhancedMapItemReferenceList;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.Session.Session;

public class Student extends User {

	Session session;

	// constructors

	public Student(EnhancedMap<? extends EnhancedMapItem> parentDatabaseMap,
			EnhancedMapItemReferenceList<? extends EnhancedMapItem> parentReferenceList,
			String email, String passwordHash, String name, Session session) {
		super(parentDatabaseMap, parentReferenceList, email, passwordHash, name);
		this.session = session;
	}

	// abstract method overrides

	@Override
	public Role getRole() {
		return Role.STUDENT;
	}

}
