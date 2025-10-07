package com.aau.grouping_system.User.Student;

import com.aau.grouping_system.EnhancedMap.EnhancedMap;
import com.aau.grouping_system.EnhancedMap.EnhancedMapItem;
import com.aau.grouping_system.User.User;

public class Student extends User {

	// constructors

	public Student(EnhancedMap<EnhancedMapItem> parentMap, String email, String passwordHash, String name) {
		super(parentMap, email, passwordHash, name);
	}

	// abstract method overrides

	@Override
	public Role getRole() {
		return Role.STUDENT;
	}

}
