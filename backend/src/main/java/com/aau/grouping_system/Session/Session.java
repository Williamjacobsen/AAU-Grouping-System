package com.aau.grouping_system.Session;

import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.item.DatabaseItem;
import com.aau.grouping_system.Database.item.ItemReferenceList;
import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;

public class Session extends DatabaseItem {

	private Coordinator coordinator;
	public ItemReferenceList<Supervisor> supervisors;
	public ItemReferenceList<Student> students;
	public ItemReferenceList<Project> projects;
	public ItemReferenceList<Group> groups;

	// Constructor

	public Session(DatabaseMap<? extends DatabaseItem> parentDatabaseMap,
			ItemReferenceList<? extends DatabaseItem> parentReferenceList,
			Coordinator coordinator) {
		super(parentDatabaseMap, parentReferenceList);
		this.coordinator = coordinator;
		this.supervisors = new ItemReferenceList<>(this);
		this.students = new ItemReferenceList<>(this);
		this.projects = new ItemReferenceList<>(this);
		this.groups = new ItemReferenceList<>(this);
	}

	// Getters og setters

	public Coordinator getCoordinator() {
		return coordinator;
	}

	public void setCoordinator(Coordinator coordinator) {
		this.coordinator = coordinator;
	}

	public ItemReferenceList<Supervisor> getSupervisors() {
		return supervisors;
	}

	public void setSupervisors(ItemReferenceList<Supervisor> supervisors) {
		this.supervisors = supervisors;
	}

	public ItemReferenceList<Student> getStudents() {
		return students;
	}

	public void setStudents(ItemReferenceList<Student> students) {
		this.students = students;
	}

	public ItemReferenceList<Project> getProjects() {
		return projects;
	}

	public void setProjects(ItemReferenceList<Project> projects) {
		this.projects = projects;
	}

	public ItemReferenceList<Group> getGroups() {
		return groups;
	}

	public void setGroups(ItemReferenceList<Group> groups) {
		this.groups = groups;
	}

}