package com.aau.grouping_system.Sessions;

import com.aau.grouping_system.EnhancedMap.EnhancedMap;
import com.aau.grouping_system.EnhancedMap.EnhancedMappable;
import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;

public class Session extends EnhancedMappable {

	// todo: brug CopyOnWriteArrayList i stedet for lister, fordi det er
	// thread-safe.
	private Coordinator coordinator;
	public EnhancedMap<Supervisor> supervisors = new EnhancedMap<>();
	public EnhancedMap<Student> students = new EnhancedMap<>();
	public EnhancedMap<Project> projects = new EnhancedMap<>();
	public EnhancedMap<Group> groups = new EnhancedMap<>();

	// Constructor
	public Session(Coordinator coordinator,
			EnhancedMap<Supervisor> supervisors,
			EnhancedMap<Student> students,
			EnhancedMap<Project> projects,
			EnhancedMap<Group> groups) {
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

	public EnhancedMap<Supervisor> getSupervisors() {
		return supervisors;
	}

	public void setSupervisors(EnhancedMap<Supervisor> supervisors) {
		this.supervisors = supervisors;
	}

	public EnhancedMap<Student> getStudents() {
		return students;
	}

	public void setStudents(EnhancedMap<Student> students) {
		this.students = students;
	}

	public EnhancedMap<Project> getProjects() {
		return projects;
	}

	public void setProjects(EnhancedMap<Project> projects) {
		this.projects = projects;
	}

	public EnhancedMap<Group> getGroups() {
		return groups;
	}

	public void setGroups(EnhancedMap<Group> groups) {
		this.groups = groups;
	}

}