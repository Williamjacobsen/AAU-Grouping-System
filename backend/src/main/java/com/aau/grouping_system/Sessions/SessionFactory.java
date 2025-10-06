package com.aau.grouping_system.Sessions;

import com.aau.grouping_system.EnhancedMap.EnhancedMap;
import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;

public class SessionFactory {
	public static Session create(Coordinator coordinator,
			EnhancedMap<Supervisor> supervisors,
			EnhancedMap<Student> students,
			EnhancedMap<Project> projects,
			EnhancedMap<Group> groups) {
		return new Session(coordinator, supervisors, students, projects, groups);
	}
}