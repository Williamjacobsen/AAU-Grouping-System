package com.aau.grouping_system.Project;

import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseIdList;

public class Project extends DatabaseItem {
	// todo: Fjern "project" fra variabelnavne.
	private String projectName;
	private String description;
	private int projectId;
	// todo: Tilføj user

	public Project(Database db, DatabaseIdList parentItemChildIdList,
			String projectName, String description, int projectId) {
		super(db, parentItemChildIdList);
		this.projectName = projectName;
		this.description = description;
		this.projectId = projectId;
	}

	@Override
	protected DatabaseMap<? extends DatabaseItem> getDatabaseMap(Database db) {
		return db.getProjects();
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