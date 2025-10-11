package com.aau.grouping_system.Session;

import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.DatabaseMapItem;
import com.aau.grouping_system.Database.DatabaseMapItemReferenceList;
import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;

public class Session extends DatabaseMapItem {

	private Coordinator coordinator;
	public DatabaseMapItemReferenceList<Supervisor> supervisors;
	public DatabaseMapItemReferenceList<Student> students;
	public DatabaseMapItemReferenceList<Project> projects;
	public DatabaseMapItemReferenceList<Group> groups;

	// Constructor

	public Session(DatabaseMap<? extends DatabaseMapItem> parentDatabaseMap,
			DatabaseMapItemReferenceList<? extends DatabaseMapItem> parentReferenceList,
			Coordinator coordinator) {
		super(parentDatabaseMap, parentReferenceList);
		this.coordinator = coordinator;
		this.supervisors = new DatabaseMapItemReferenceList<>(this);
		this.students = new DatabaseMapItemReferenceList<>(this);
		this.projects = new DatabaseMapItemReferenceList<>(this);
		this.groups = new DatabaseMapItemReferenceList<>(this);
	}

	// Getters og setters

	public Coordinator getCoordinator() {
		return coordinator;
	}

	public void setCoordinator(Coordinator coordinator) {
		this.coordinator = coordinator;
	}

	public DatabaseMapItemReferenceList<Supervisor> getSupervisors() {
		return supervisors;
	}

	public void setSupervisors(DatabaseMapItemReferenceList<Supervisor> supervisors) {
		this.supervisors = supervisors;
	}

	public DatabaseMapItemReferenceList<Student> getStudents() {
		return students;
	}

	public void setStudents(DatabaseMapItemReferenceList<Student> students) {
		this.students = students;
	}

	public DatabaseMapItemReferenceList<Project> getProjects() {
		return projects;
	}

	public void setProjects(DatabaseMapItemReferenceList<Project> projects) {
		this.projects = projects;
	}

	public DatabaseMapItemReferenceList<Group> getGroups() {
		return groups;
	}

	public void setGroups(DatabaseMapItemReferenceList<Group> groups) {
		this.groups = groups;
	}

}