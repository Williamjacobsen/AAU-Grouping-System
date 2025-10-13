package com.aau.grouping_system.Database;

import org.springframework.stereotype.Component;

import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;

import jakarta.annotation.PostConstruct;

@Component // so we can do dependency injection
public class Database {

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

	// Fill database with example data
	@PostConstruct
	public void init() {

		Coordinator c0 = new Coordinator(coordinators, null, "CoordinatorA@example.com", "PasswordHashA", "CoordinatorA");
		Coordinator c1 = new Coordinator(coordinators, null, "CoordinatorB@example.com", "PasswordHashB", "CoordinatorB");
		Coordinator c2 = new Coordinator(coordinators, null, "CoordinatorC@example.com", "PasswordHashC", "CoordinatorC");

		Session s0 = new Session(sessions, c0.sessions, c0);
		Session s1 = new Session(sessions, c0.sessions, c0);
		Session s2 = new Session(sessions, c1.sessions, c1);

		Student st0 = new Student(students, s0.students, "Student0@example.com", "PasswordHash0", "Student0", s0);
		Student st1 = new Student(students, s0.students, "Student1@example.com", "PasswordHash1", "Student1", s0);
		Student st2 = new Student(students, s0.students, "Student2@example.com", "PasswordHash2", "Student2", s0);
		Student st3 = new Student(students, s1.students, "Student3@example.com", "PasswordHash3", "Student3", s1);
		Student st4 = new Student(students, s1.students, "Student4@example.com", "PasswordHash4", "Student4", s1);
		Student st5 = new Student(students, s2.students, "Student5@example.com", "PasswordHash5", "Student5", s2);
		Student st6 = new Student(students, s2.students, "Student6@example.com", "PasswordHash6", "Student6", s2);

	}
}
