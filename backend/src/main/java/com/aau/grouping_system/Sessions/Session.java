package com.aau.grouping_system.Sessions;

import java.util.concurrent.CopyOnWriteArrayList;

import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;
import com.aau.grouping_system.EnhancedMap.EnhancedMap;
import com.aau.grouping_system.EnhancedMap.EnhancedMapItem;
import com.aau.grouping_system.EnhancedMap.EnhancedMapReference;

public class Session extends EnhancedMapItem {

	private Coordinator coordinator;
	public EnhancedMapReference<Supervisor> supervisors = new EnhancedMapReference<>(this);
	public EnhancedMapReference<Student> students = new EnhancedMapReference<>(this);
	public EnhancedMapReference<Project> projects = new EnhancedMapReference<>(this);
	public EnhancedMapReference<Group> groups = new EnhancedMapReference<>(this);

	// Constructor

	public Session(EnhancedMap<EnhancedMapItem> parentMap,
			Coordinator coordinator,
			EnhancedMapReference<Supervisor> supervisors,
			EnhancedMapReference<Student> students,
			EnhancedMapReference<Project> projects,
			EnhancedMapReference<Group> groups) {
		super(parentMap);
		this.coordinator = coordinator;
		this.supervisors = supervisors;
		this.students = students;
		this.projects = projects;
		this.groups = groups;
	}

	// Getters og setters

	public Coordinator getCoordinator() {
		return coordinator;
	}

	public void setCoordinator(Coordinator coordinator) {
		this.coordinator = coordinator;
	}

	public EnhancedMapReference<Supervisor> getSupervisors() {
		return supervisors;
	}

	public void setSupervisors(EnhancedMapReference<Supervisor> supervisors) {
		this.supervisors = supervisors;
	}

	public EnhancedMapReference<Student> getStudents() {
		return students;
	}

	public void setStudents(EnhancedMapReference<Student> students) {
		this.students = students;
	}

	public EnhancedMapReference<Project> getProjects() {
		return projects;
	}

	public void setProjects(EnhancedMapReference<Project> projects) {
		this.projects = projects;
	}

	public EnhancedMapReference<Group> getGroups() {
		return groups;
	}

	public void setGroups(EnhancedMapReference<Group> groups) {
		this.groups = groups;
	}

}