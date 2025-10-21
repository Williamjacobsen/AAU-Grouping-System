package com.aau.grouping_system.Session;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.DatabaseItemChildList;
import com.aau.grouping_system.User.Coordinator.Coordinator;

public class Session extends DatabaseItem {

	private String coordinatorId;
	public DatabaseItemChildList supervisors;
	public DatabaseItemChildList students;
	public DatabaseItemChildList projects;
	public DatabaseItemChildList groups;

	// constructor

	public Session(Database db, DatabaseItemChildList parentItemChildIdList,
			Coordinator coordinator) {
		super(db, parentItemChildIdList);
		this.coordinatorId = coordinator.getId();
		this.supervisors = new DatabaseItemChildList(db.getSupervisors(), this);
		this.students = new DatabaseItemChildList(db.getStudents(), this);
		this.projects = new DatabaseItemChildList(db.getProjects(), this);
		this.groups = new DatabaseItemChildList(db.getGroups(), this);
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

	public void setCoordinator(String coordinatorId) {
		this.coordinatorId = coordinatorId;
	}

	public DatabaseItemChildList getSupervisors() {
		return supervisors;
	}

	public void setSupervisors(DatabaseItemChildList supervisors) {
		this.supervisors = supervisors;
	}

	public DatabaseItemChildList getStudents() {
		return students;
	}

	public void setStudents(DatabaseItemChildList students) {
		this.students = students;
	}

	public DatabaseItemChildList getProjects() {
		return projects;
	}

	public void setProjects(DatabaseItemChildList projects) {
		this.projects = projects;
	}

	public DatabaseItemChildList getGroups() {
		return groups;
	}

	public void setGroups(DatabaseItemChildList groups) {
		this.groups = groups;
	}

}