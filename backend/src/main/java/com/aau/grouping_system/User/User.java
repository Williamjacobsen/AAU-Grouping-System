package com.aau.grouping_system.User;

public class User {

	private String email;
	private String passwordHash;
	private String name;

	// getters & setters

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public String getPasswordHash() { return passwordHash; }
	public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	// constructors

	public User(String email, String passwordHash, String name) {
		this.email = email;
		this.passwordHash = passwordHash;
		this.name = name;
	}

}
