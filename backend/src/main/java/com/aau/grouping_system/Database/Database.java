package com.aau.grouping_system.Database;

import org.springframework.stereotype.Component;

import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;

@Component
public class Database {

	private DatabaseData data = new DatabaseData();

	// getters & setters

	void setData(DatabaseData data) {
		this.data = data;
	}

	DatabaseData getData() {
		return data;
	}

	public DatabaseMap<Coordinator> getCoordinators() {
		return data.getCoordinators();
	}

	public DatabaseMap<Session> getSessions() {
		return data.getSessions();
	}

	public DatabaseMap<Supervisor> getSupervisors() {
		return data.getSupervisors();
	}

	public DatabaseMap<Student> getStudents() {
		return data.getStudents();
	}

	public DatabaseMap<Project> getProjects() {
		return data.getProjects();
	}

	public DatabaseMap<Group> getGroups() {
		return data.getGroups();
	}

}
