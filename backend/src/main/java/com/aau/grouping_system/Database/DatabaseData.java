package com.aau.grouping_system.Database;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.aau.grouping_system.ChatSystem.ChatRoom;
import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.SessionMember.Student.Student;
import com.aau.grouping_system.User.SessionMember.Supervisor.Supervisor;

/// A container for saveable data.
// We use @SuppressWarnings("unchecked") because the type-safety violation
// warnings aren't true here because of Java's invariance of generics (see:
// https://medium.com/@barbieri.santiago/understanding-invariance-in-java-generics-048cb891569e).
@SuppressWarnings("unchecked")
public class DatabaseData implements Serializable {

	private final ConcurrentHashMap<Integer, DatabaseMap<? extends DatabaseItem>> maps = new ConcurrentHashMap<>();
	private final AtomicInteger idGenerator = new AtomicInteger();

	DatabaseMap<Coordinator> coordinators = (DatabaseMap<Coordinator>) addMap();
	DatabaseMap<Session> sessions = (DatabaseMap<Session>) addMap();
	DatabaseMap<Supervisor> supervisors = (DatabaseMap<Supervisor>) addMap();
	DatabaseMap<Student> students = (DatabaseMap<Student>) addMap();
	DatabaseMap<Project> projects = (DatabaseMap<Project>) addMap();
	DatabaseMap<Group> groups = (DatabaseMap<Group>) addMap();
	DatabaseMap<ChatRoom> chatRooms = (DatabaseMap<ChatRoom>) addMap();
	private final ConcurrentHashMap<String, String> chatRoomKeyIndex = new ConcurrentHashMap<>();

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
	public DatabaseMap<ChatRoom> getChatRooms() { return chatRooms; }
	public ConcurrentHashMap<String, String> getChatRoomKeyIndex() { return chatRoomKeyIndex; }
}
