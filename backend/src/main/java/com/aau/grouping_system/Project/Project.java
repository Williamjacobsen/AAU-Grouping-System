package com.aau.grouping_system.Project;

import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseItemChildGroup;

public class Project extends DatabaseItem {

	private String name;
	private String description;

	// constructors

	public Project(Database db, DatabaseItemChildGroup parentItemChildIdList,
			String name, String description) {
		super(db, parentItemChildIdList);
		this.name = name;
		this.description = description;
	}

	@Override
	protected DatabaseMap<? extends DatabaseItem> getDatabaseMap(Database db) {
		return db.getProjects();
	}

	// getters & setters

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}