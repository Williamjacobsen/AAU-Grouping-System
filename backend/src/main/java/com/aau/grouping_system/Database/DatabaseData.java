package com.aau.grouping_system.Database;

import java.io.Serializable;

import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;

public class DatabaseData implements Serializable {
	
	private final DatabaseMap<Coordinator> coordinators = new DatabaseMap<>();
	private final DatabaseMap<Session> sessions = new DatabaseMap<>();
	private final DatabaseMap<Supervisor> supervisors = new DatabaseMap<>();
	private final DatabaseMap<Student> students = new DatabaseMap<>();
	private final DatabaseMap<Project> projects = new DatabaseMap<>();
	private final DatabaseMap<Group> groups = new DatabaseMap<>();

	// getters & setters

	public DatabaseMap<Coordinator> getCoordinators() {
		return coordinators;
	}

	public DatabaseMap<Session> getSessions() {
		return sessions;
	}

	public DatabaseMap<Supervisor> getSupervisors() {
		return supervisors;
	}

	public DatabaseMap<Student> getStudents() {
		return students;
	}

	public DatabaseMap<Project> getProjects() {
		return projects;
	}

	public DatabaseMap<Group> getGroups() {
		return groups;
	}
}
