package com.aau.grouping_system.Database;

import org.springframework.stereotype.Component;

import com.aau.grouping_system.EnhancedMap.EnhancedMap;
import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;

@Component // so we can do dependency injection
public class Database {

	private final EnhancedMap<Coordinator> coordinators = new EnhancedMap<>();
	private final EnhancedMap<Session> sessions = new EnhancedMap<>();
	private final EnhancedMap<Supervisor> supervisors = new EnhancedMap<>();
	private final EnhancedMap<Student> students = new EnhancedMap<>();
	private final EnhancedMap<Project> projects = new EnhancedMap<>();
	private final EnhancedMap<Group> groups = new EnhancedMap<>();

	// getters & setters

	public EnhancedMap<Coordinator> getCoordinators() {
		return coordinators;
	}

	public EnhancedMap<Session> getSessions() {
		return sessions;
	}

	public EnhancedMap<Supervisor> getSupervisors() {
		return supervisors;
	}

	public EnhancedMap<Student> getStudents() {
		return students;
	}

	public EnhancedMap<Project> getProjects() {
		return projects;
	}

	public EnhancedMap<Group> getGroups() {
		return groups;
	}

}
