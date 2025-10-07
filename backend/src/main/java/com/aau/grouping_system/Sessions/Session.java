package com.aau.grouping_system.Sessions;

import java.util.concurrent.CopyOnWriteArrayList;

import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;
import com.aau.grouping_system.EnhancedMap.EnhancedMapItem;

public class Session extends EnhancedMapItem {

	private Coordinator coordinator;
	public CopyOnWriteArrayList<Supervisor> supervisors = new CopyOnWriteArrayList<>();
	public CopyOnWriteArrayList<Student> students = new CopyOnWriteArrayList<>();
	public CopyOnWriteArrayList<Project> projects = new CopyOnWriteArrayList<>();
	public CopyOnWriteArrayList<Group> groups = new CopyOnWriteArrayList<>();

	@Override
	protected void initializeChildMapReferences() {
		childMapReferences.add(supervisors);
		childMapReferences.add(students);
		childMapReferences.add(projects);
		childMapReferences.add(groups);
	}

	// Constructor

	public Session(Coordinator coordinator,
			CopyOnWriteArrayList<Supervisor> supervisors,
			CopyOnWriteArrayList<Student> students,
			CopyOnWriteArrayList<Project> projects,
			CopyOnWriteArrayList<Group> groups) {
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

	public CopyOnWriteArrayList<Supervisor> getSupervisors() {
		return supervisors;
	}

	public void setSupervisors(CopyOnWriteArrayList<Supervisor> supervisors) {
		this.supervisors = supervisors;
	}

	public CopyOnWriteArrayList<Student> getStudents() {
		return students;
	}

	public void setStudents(CopyOnWriteArrayList<Student> students) {
		this.students = students;
	}

	public CopyOnWriteArrayList<Project> getProjects() {
		return projects;
	}

	public void setProjects(CopyOnWriteArrayList<Project> projects) {
		this.projects = projects;
	}

	public CopyOnWriteArrayList<Group> getGroups() {
		return groups;
	}

	public void setGroups(CopyOnWriteArrayList<Group> groups) {
		this.groups = groups;
	}

}