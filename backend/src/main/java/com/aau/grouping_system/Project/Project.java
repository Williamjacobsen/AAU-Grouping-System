package com.aau.grouping_system.Project;

import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.DatabaseIdList;
// new class Project, inherits from DatabaseItem
public class Project extends DatabaseItem {
// attributes
	private String name;
	private String description;

	// constructor (initial values)
	public Project(DatabaseMap<? extends DatabaseItem> parentMap,
			DatabaseIdList parentReferences, String name,
			String description) {
		super(parentMap, parentReferences); // parent class
		this.name = name;
		this.description = description;
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