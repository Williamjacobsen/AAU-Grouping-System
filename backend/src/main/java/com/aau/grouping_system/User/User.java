package com.aau.grouping_system.User;

import com.aau.grouping_system.Database.DatabaseItem;

public abstract class User extends DatabaseItem {

	private String email;
	private String passwordHash;
	private String name;
	/// While the login code is not null, it is the same as the user's password.
	private String loginCode = null;

	/// Despite getRole being an abstract method and this therefore never being
	/// assigned or set, this field is still needed to send the role in a JSON object
	/// of User. This is because - despite being private - fields with public getters
	/// are inserted into the JSON object when creating one.
	private Role role;

	public enum Role {
		Coordinator,
		Supervisor,
		Student
	}

	public abstract Role getRole();

	public User(String email, String passwordHash, String name) {
		this.email = email;
		this.passwordHash = passwordHash;
		this.name = name;
		this.role = getRole();
	}

	// @formatter:off
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getPasswordHash() { return passwordHash; }
	void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getLoginCode() { return loginCode;}
	void setLoginCode(String loginCode) { this.loginCode = loginCode;}
}
