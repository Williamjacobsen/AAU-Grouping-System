package com.aau.grouping_system.Database;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;

public class DatabaseData implements Serializable {

	private final ConcurrentHashMap<Integer, DatabaseMap<? extends DatabaseItem>> maps = new ConcurrentHashMap<>();
	private final AtomicInteger idGenerator = new AtomicInteger();

	private DatabaseMap<Coordinator> coordinators = new DatabaseMap<>(maps, idGenerator);
	private DatabaseMap<Session> sessions = new DatabaseMap<>(maps, idGenerator);
	private DatabaseMap<Supervisor> supervisors = new DatabaseMap<>(maps, idGenerator);
	private DatabaseMap<Student> students = new DatabaseMap<>(maps, idGenerator);
	private DatabaseMap<Project> projects = new DatabaseMap<>(maps, idGenerator);
	private DatabaseMap<Group> groups = new DatabaseMap<>(maps, idGenerator);

	// getters & setters

	DatabaseMap<? extends DatabaseItem> getMap(Integer id) {
		return maps.get(id);
	}

	DatabaseMap<Coordinator> getCoordinators() {
		return coordinators;
	}

	DatabaseMap<Session> getSessions() {
		return sessions;
	}

	DatabaseMap<Supervisor> getSupervisors() {
		return supervisors;
	}

	DatabaseMap<Student> getStudents() {
		return students;
	}

	DatabaseMap<Project> getProjects() {
		return projects;
	}

	DatabaseMap<Group> getGroups() {
		return groups;
	}
}
