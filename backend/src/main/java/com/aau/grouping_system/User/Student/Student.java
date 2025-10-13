package com.aau.grouping_system.User.Student;

import com.aau.grouping_system.User.User;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.item.DatabaseItem;
import com.aau.grouping_system.Database.item.ItemReferenceList;
import com.aau.grouping_system.Session.Session;

public class Student extends User {

	Session session;

	// constructors

	public Student(DatabaseMap<? extends DatabaseItem> parentDatabaseMap,
			ItemReferenceList<? extends DatabaseItem> parentReferenceList,
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
