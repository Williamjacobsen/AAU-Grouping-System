package com.aau.grouping_system.Session;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.DatabaseReferences;
import com.aau.grouping_system.User.Coordinator.Coordinator;

public class Session extends DatabaseItem {

	private Coordinator coordinator;
	public DatabaseReferences supervisors;
	public DatabaseReferences students;
	public DatabaseReferences projects;
	public DatabaseReferences groups;

	// Constructor

	public Session(DatabaseMap<? extends DatabaseItem> parentMap,
			DatabaseReferences parentReferences, Database db,
			Coordinator coordinator) {
		super(parentMap, parentReferences);
		this.coordinator = coordinator;
		this.supervisors = new DatabaseReferences(db.getSupervisors(), this);
		this.students = new DatabaseReferences(db.getStudents(), this);
		this.projects = new DatabaseReferences(db.getProjects(), this);
		this.groups = new DatabaseReferences(db.getGroups(), this);
	}

	// Getters og setters

	public Coordinator getCoordinator() {
		return coordinator;
	}

	public void setCoordinator(Coordinator coordinator) {
		this.coordinator = coordinator;
	}

	public DatabaseReferences getSupervisors() {
		return supervisors;
	}

	public void setSupervisors(DatabaseReferences supervisors) {
		this.supervisors = supervisors;
	}

	public DatabaseReferences getStudents() {
		return students;
	}

	public void setStudents(DatabaseReferences students) {
		this.students = students;
	}

	public DatabaseReferences getProjects() {
		return projects;
	}

	public void setProjects(DatabaseReferences projects) {
		this.projects = projects;
	}

	public DatabaseReferences getGroups() {
		return groups;
	}

	public void setGroups(DatabaseReferences groups) {
		this.groups = groups;
	}

}