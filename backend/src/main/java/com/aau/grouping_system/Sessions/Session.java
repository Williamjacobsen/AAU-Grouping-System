package com.aau.grouping_system.Sessions;

import java.util.List;

import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;

public class Session {

	// todo: brug CopyOnWriteArrayList i stedet for lister, fordi det er
	// thread-safe.
	private Coordinator coordinator;
	private List<Supervisor> supervisors;
	private List<Student> students;
	private List<Project> projects;
	private List<Group> groups;

	// Constructor
	public Session(Coordinator coordinator, List<Supervisor> supervisors, List<Student> students,
			List<Project> projects, List<Group> groups) {
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

	public List<Supervisor> getSupervisors() {
		return supervisors;
	}

	public void setSupervisors(List<Supervisor> supervisors) {
		this.supervisors = supervisors;
	}

	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

}