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

/// A container for saveable data.
@SuppressWarnings("unchecked") // Type-safety violations aren't true here.
public class DatabaseData implements Serializable {

	private final ConcurrentHashMap<Integer, DatabaseMap<? extends DatabaseItem>> maps = new ConcurrentHashMap<>();
	private final AtomicInteger idGenerator = new AtomicInteger();

	DatabaseMap<Coordinator> coordinators = (DatabaseMap<Coordinator>) addMap();
	DatabaseMap<Session> sessions = (DatabaseMap<Session>) addMap();
	DatabaseMap<Supervisor> supervisors = (DatabaseMap<Supervisor>) addMap();
	DatabaseMap<Student> students = (DatabaseMap<Student>) addMap();
	DatabaseMap<Project> projects = (DatabaseMap<Project>) addMap();
	DatabaseMap<Group> groups = (DatabaseMap<Group>) addMap();

	public DatabaseMap<? extends DatabaseItem> addMap() {
		Integer newId = idGenerator.incrementAndGet();
		DatabaseMap<? extends DatabaseItem> newMap = new DatabaseMap<>(newId);
		maps.put(newId, newMap);
		return newMap;
	}

	// @formatter:off
	DatabaseMap<? extends DatabaseItem> getMap(Integer id) { return maps.get(id); }
	DatabaseMap<Coordinator> getCoordinators() { return coordinators; }
	DatabaseMap<Session> getSessions() { return sessions; }
	DatabaseMap<Supervisor> getSupervisors() { return supervisors; }
	DatabaseMap<Student> getStudents() { return students; }
	DatabaseMap<Project> getProjects() { return projects; }
	DatabaseMap<Group> getGroups() { return groups; }
}
