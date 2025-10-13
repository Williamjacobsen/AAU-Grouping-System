package com.aau.grouping_system.User.Student;

import com.aau.grouping_system.User.User;
import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.DatabaseReferences;
import com.aau.grouping_system.Session.Session;

public class Student extends User {

	Session session;

	// constructors

	public Student(DatabaseMap<? extends DatabaseItem> parentMap, DatabaseReferences parentReferences,
			String email, String passwordHash, String name, Session session) {
		super(parentMap, parentReferences, email, passwordHash, name);
		this.session = session;
	}

	// abstract method overrides

	@Override
	public Role getRole() {
		return Role.STUDENT;
	}

}
