package com.aau.grouping_system.User.Student;

import com.aau.grouping_system.User.User;
import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseItemChildList;
import com.aau.grouping_system.Session.Session;

public class Student extends User {

	private String sessionId;

	// constructors

	public Student(Database db, DatabaseItemChildList parentItemChildIdList,
			String email, String passwordHash, String name, Session session) {
		super(db, parentItemChildIdList, email, passwordHash, name);
		this.sessionId = session.getId();
	}

	// abstract method overrides

	@Override
	protected DatabaseMap<? extends DatabaseItem> getDatabaseMap(Database db) {
		return db.getStudents();
	}

	@Override
	public Role getRole() {
		return Role.STUDENT;
	}

}
