package com.aau.grouping_system.Project;

import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.User.User;

public class Project extends DatabaseItem {

	private String name;
	private String description;
	private String creatorUserId;

	// constuctor
	public Project(String name, String description, User creatorUser) {
		this.name = name;
		this.description = description;
		this.creatorUserId = creatorUser.getId();
	}

	// @formatter:off
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public String getCreatorUserId() { return creatorUserId; }
	public void setCreatorUserId(String creatorUserId) { this.creatorUserId = creatorUserId; }
}