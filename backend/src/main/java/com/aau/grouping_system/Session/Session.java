package com.aau.grouping_system.Session;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.DatabaseItemChildGroup;
import com.aau.grouping_system.User.Coordinator.Coordinator;

public class Session extends DatabaseItem {

	public DatabaseItemChildGroup supervisors;
	public DatabaseItemChildGroup students;
	public DatabaseItemChildGroup projects;
	public DatabaseItemChildGroup groups;

	public String coordinatorId;
	public String name;

	// constructor

	public Session(Database db, DatabaseItemChildGroup parentItemChildIdList,
			Coordinator coordinator, String name) {
		super(db, parentItemChildIdList);
		this.coordinatorId = coordinator.getId();
		this.supervisors = new DatabaseItemChildGroup(db.getSupervisors(), this);
		this.students = new DatabaseItemChildGroup(db.getStudents(), this);
		this.projects = new DatabaseItemChildGroup(db.getProjects(), this);
		this.groups = new DatabaseItemChildGroup(db.getGroups(), this);
		this.name = name;
	}

	// abstract method overrides

	@Override
	protected DatabaseMap<? extends DatabaseItem> getDatabaseMap(Database db) {
		return db.getSessions();
	}

	// getters & setters

	public String getCoordinatorId() {
		return coordinatorId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DatabaseItemChildGroup getSupervisors() {
		return supervisors;
	}

	public void setSupervisors(DatabaseItemChildGroup supervisors) {
		this.supervisors = supervisors;
	}

	public DatabaseItemChildGroup getStudents() {
		return students;
	}

	public void setStudents(DatabaseItemChildGroup students) {
		this.students = students;
	}

	public DatabaseItemChildGroup getProjects() {
		return projects;
	}

	public void setProjects(DatabaseItemChildGroup projects) {
		this.projects = projects;
	}

	public DatabaseItemChildGroup getGroups() {
		return groups;
	}

	public void setGroups(DatabaseItemChildGroup groups) {
		this.groups = groups;
	}

}