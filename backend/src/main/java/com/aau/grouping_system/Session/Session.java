package com.aau.grouping_system.Session;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.DatabaseIdList;
import com.aau.grouping_system.User.Coordinator.Coordinator;

public class Session extends DatabaseItem {

	private Coordinator coordinator;
	public DatabaseIdList supervisors;
	public DatabaseIdList students;
	public DatabaseIdList projects;
	public DatabaseIdList groups;

	// Constructor

	public Session(DatabaseMap<? extends DatabaseItem> parentMap,
			DatabaseIdList parentReferences, Database db,
			Coordinator coordinator) {
		super(parentMap, parentReferences);
		this.coordinator = coordinator;
		this.supervisors = new DatabaseIdList(db.getSupervisors(), this);
		this.students = new DatabaseIdList(db.getStudents(), this);
		this.projects = new DatabaseIdList(db.getProjects(), this);
		this.groups = new DatabaseIdList(db.getGroups(), this);
	}

	// Getters og setters

	public Coordinator getCoordinator() {
		return coordinator;
	}

	public void setCoordinator(Coordinator coordinator) {
		this.coordinator = coordinator;
	}

	public DatabaseIdList getSupervisors() {
		return supervisors;
	}

	public void setSupervisors(DatabaseIdList supervisors) {
		this.supervisors = supervisors;
	}

	public DatabaseIdList getStudents() {
		return students;
	}

	public void setStudents(DatabaseIdList students) {
		this.students = students;
	}

	public DatabaseIdList getProjects() {
		return projects;
	}

	public void setProjects(DatabaseIdList projects) {
		this.projects = projects;
	}

	public DatabaseIdList getGroups() {
		return groups;
	}

	public void setGroups(DatabaseIdList groups) {
		this.groups = groups;
	}

}