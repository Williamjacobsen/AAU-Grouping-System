package com.aau.grouping_system.EnhancedMap.Project;

import com.aau.grouping_system.EnhancedMap.EnhancedMap;
import com.aau.grouping_system.EnhancedMap.EnhancedMapItem;

public class Project extends EnhancedMapItem {
	// todo: Fjern "project" fra variabelnavne.
	private String projectName;
	private String description;
	private int projectId;
	// todo: Tilføj user

	public Project(EnhancedMap<? extends EnhancedMapItem> parentMap, String projectName, String description,
			int projectId) {
		super(parentMap);
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