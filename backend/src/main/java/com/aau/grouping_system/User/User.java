package com.aau.grouping_system.User;

import com.aau.grouping_system.EnhancedMap.EnhancedMap;
import com.aau.grouping_system.EnhancedMap.EnhancedMapItem;
import com.aau.grouping_system.EnhancedMap.EnhancedMapItemReferenceList;

public abstract class User extends EnhancedMapItem {

	private String email;
	private String passwordHash;
	private String name;

	public enum Role {
		UNDEFINED,
		COORDINATOR,
		SUPERVISOR,
		STUDENT
	}

	// abstract methods

	public abstract Role getRole();

	// constructors

	public User(EnhancedMap<? extends EnhancedMapItem> parentDatabaseMap,
			EnhancedMapItemReferenceList<? extends EnhancedMapItem> parentReferenceList,
			String email, String passwordHash, String name) {
		super(parentDatabaseMap, parentReferenceList);
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
