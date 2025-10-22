package com.aau.grouping_system.Session;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.DatabaseItemChildSubgroup;
import com.aau.grouping_system.User.Coordinator.Coordinator;

public class Session extends DatabaseItem {

	public DatabaseItemChildSubgroup supervisors;
	public DatabaseItemChildSubgroup students;
	public DatabaseItemChildSubgroup projects;
	public DatabaseItemChildSubgroup groups;

	public String coordinatorId;
	public String name;

	// constructor

	public Session(Database db, DatabaseItemChildSubgroup parentItemChildIdList,
			Coordinator coordinator, String name) {
		super(db, parentItemChildIdList);
		this.coordinatorId = coordinator.getId();
		this.supervisors = new DatabaseItemChildSubgroup(db.getSupervisors(), this);
		this.students = new DatabaseItemChildSubgroup(db.getStudents(), this);
		this.projects = new DatabaseItemChildSubgroup(db.getProjects(), this);
		this.groups = new DatabaseItemChildSubgroup(db.getGroups(), this);
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

	public DatabaseItemChildSubgroup getSupervisors() {
		return supervisors;
	}

	public void setSupervisors(DatabaseItemChildSubgroup supervisors) {
		this.supervisors = supervisors;
	}

	public DatabaseItemChildSubgroup getStudents() {
		return students;
	}

	public void setStudents(DatabaseItemChildSubgroup students) {
		this.students = students;
	}

	public DatabaseItemChildSubgroup getProjects() {
		return projects;
	}

	public void setProjects(DatabaseItemChildSubgroup projects) {
		this.projects = projects;
	}

	public DatabaseItemChildSubgroup getGroups() {
		return groups;
	}

	public void setGroups(DatabaseItemChildSubgroup groups) {
		this.groups = groups;
	}

}