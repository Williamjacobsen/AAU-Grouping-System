package com.aau.grouping_system.Session;

import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;
import com.aau.grouping_system.EnhancedMap.EnhancedMap;
import com.aau.grouping_system.EnhancedMap.EnhancedMapItem;
import com.aau.grouping_system.EnhancedMap.EnhancedMapItemReferenceList;

public class Session extends EnhancedMapItem {

	private Coordinator coordinator;
	public EnhancedMapItemReferenceList<Supervisor> supervisors;
	public EnhancedMapItemReferenceList<Student> students;
	public EnhancedMapItemReferenceList<Project> projects;
	public EnhancedMapItemReferenceList<Group> groups;

	// Constructor

	public Session(EnhancedMap<? extends EnhancedMapItem> parentMap, Coordinator coordinator) {
		super(parentMap);
		this.coordinator = coordinator;
		this.supervisors = new EnhancedMapItemReferenceList<>(this);
		this.students = new EnhancedMapItemReferenceList<>(this);
		this.projects = new EnhancedMapItemReferenceList<>(this);
		this.groups = new EnhancedMapItemReferenceList<>(this);
	}

	// Getters og setters

	public Coordinator getCoordinator() {
		return coordinator;
	}

	public void setCoordinator(Coordinator coordinator) {
		this.coordinator = coordinator;
	}

	public EnhancedMapItemReferenceList<Supervisor> getSupervisors() {
		return supervisors;
	}

	public void setSupervisors(EnhancedMapItemReferenceList<Supervisor> supervisors) {
		this.supervisors = supervisors;
	}

	public EnhancedMapItemReferenceList<Student> getStudents() {
		return students;
	}

	public void setStudents(EnhancedMapItemReferenceList<Student> students) {
		this.students = students;
	}

	public EnhancedMapItemReferenceList<Project> getProjects() {
		return projects;
	}

	public void setProjects(EnhancedMapItemReferenceList<Project> projects) {
		this.projects = projects;
	}

	public EnhancedMapItemReferenceList<Group> getGroups() {
		return groups;
	}

	public void setGroups(EnhancedMapItemReferenceList<Group> groups) {
		this.groups = groups;
	}

}