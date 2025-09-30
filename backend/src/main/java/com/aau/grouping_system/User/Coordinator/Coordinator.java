package com.aau.grouping_system.User.Coordinator;

import com.aau.grouping_system.User.User;

public class Coordinator extends User {

	private Integer databaseID = null;

	// getters & setters

	public Integer getDatabaseID() { return this.databaseID; }
	public void setDatabaseID(Integer id) { this.databaseID = id; }

	// constructors

	public Coordinator(String email, String password, String name) {
		super(email, password, name);
	}
}
