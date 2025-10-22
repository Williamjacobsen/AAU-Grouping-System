package com.aau.grouping_system.User;

import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseItemChildGroup;

public abstract class User extends DatabaseItem {

	private String email;
	private String passwordHash;
	private String name;

	public enum Role {
		COORDINATOR,
		SUPERVISOR,
		STUDENT;
	}

	// abstract methods

	public abstract Role getRole();

	// constructors

	public User(Database db, DatabaseItemChildGroup parentItemChildIdList,
			String email, String passwordHash, String name) {
		super(db, parentItemChildIdList);
		this.email = email;
		this.passwordHash = passwordHash;
		this.name = name;
	}

	// getters & setters

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
