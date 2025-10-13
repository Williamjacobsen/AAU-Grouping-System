package com.aau.grouping_system.Project;

import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.item.DatabaseItem;
import com.aau.grouping_system.Database.item.ItemReferenceList;

public class Project extends DatabaseItem {
	private String name;
	private String description;
	private int id;
	private int user;

	public Project(DatabaseMap<? extends DatabaseItem> parentDatabaseMap,
			ItemReferenceList<? extends DatabaseItem> parentReferenceList, String projectName,
			String description,
			int projectId) {
		super(parentDatabaseMap, parentReferenceList);
		this.name = projectName;
		this.description = description;
		this.id = projectId;
	}

	// Getters and setters
	public String getProjectName() {
		return name;
	}

	public void setProjectName(String projectName) {
		this.name = projectName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getProjectId() {
		return id;
	}

	public void setProjectId(int projectId) {
		this.id = projectId;
	}

	public int getUserId() {
		return user;
	}

	public void setUserId(int projectUser) {
		this.id = user;
	}
}