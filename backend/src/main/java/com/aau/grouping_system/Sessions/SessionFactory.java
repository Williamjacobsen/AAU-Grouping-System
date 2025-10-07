package com.aau.grouping_system.Sessions;

import java.util.concurrent.CopyOnWriteArrayList;

import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;

public class SessionFactory {
	public static Session create(Coordinator coordinator,
			CopyOnWriteArrayList<Supervisor> supervisors,
			CopyOnWriteArrayList<Student> students,
			CopyOnWriteArrayList<Project> projects,
			CopyOnWriteArrayList<Group> groups) {
		return new Session(coordinator, supervisors, students, projects, groups);
	}
}