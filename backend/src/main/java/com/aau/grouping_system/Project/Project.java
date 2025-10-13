package com.aau.grouping_system.Project;

import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.databaseMapItem.DatabaseMapItem;
import com.aau.grouping_system.Database.databaseMapItem.DatabaseMapItemReferenceList;

public class Project extends DatabaseMapItem {
	// todo: Fjern "project" fra variabelnavne.
	private String projectName;
	private String description;
	private int projectId;
	// todo: Tilføj user

	public Project(DatabaseMap<? extends DatabaseMapItem> parentDatabaseMap,
			DatabaseMapItemReferenceList<? extends DatabaseMapItem> parentReferenceList, String projectName,
			String description,
			int projectId) {
		super(parentDatabaseMap, parentReferenceList);
		this.projectName = projectName;
		this.description = description;
		this.projectId = projectId;
	}

	// Getters and setters
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
}