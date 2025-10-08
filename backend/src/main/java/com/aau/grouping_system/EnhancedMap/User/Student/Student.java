package com.aau.grouping_system.EnhancedMap.User.Student;

import com.aau.grouping_system.EnhancedMap.EnhancedMap;
import com.aau.grouping_system.EnhancedMap.EnhancedMapItem;
import com.aau.grouping_system.EnhancedMap.User.User;
import com.aau.grouping_system.EnhancedMap.Session.Session;

public class Student extends User {

	Session session;

	// constructors

	public Student(EnhancedMap<? extends EnhancedMapItem> parentMap, String email, String passwordHash, String name,
			Session session) {
		super(parentMap, email, passwordHash, name);
		this.session = session;
	}

	// abstract method overrides

	@Override
	public Role getRole() {
		return Role.STUDENT;
	}

}
