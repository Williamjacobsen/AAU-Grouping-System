package com.aau.grouping_system.User.Student;

import com.aau.grouping_system.User.User;

public class Student extends User {

	// constructors

	public Student(String email, String passwordHash, String name) {
		super(email, passwordHash, name);
	}

	// abstract method overrides

	@Override
	public Role getRole() {
		return Role.STUDENT;
	}

}
