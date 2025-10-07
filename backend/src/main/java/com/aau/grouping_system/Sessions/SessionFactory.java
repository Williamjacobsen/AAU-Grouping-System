package com.aau.grouping_system.Sessions;

import com.aau.grouping_system.EnhancedMap.EnhancedMap;
import com.aau.grouping_system.EnhancedMap.EnhancedMapItem;
import com.aau.grouping_system.EnhancedMap.EnhancedMapReference;
import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;

public class SessionFactory {
	public static Session create(EnhancedMap<EnhancedMapItem> parentMap,
			Coordinator coordinator,
			EnhancedMapReference<Supervisor> supervisors,
			EnhancedMapReference<Student> students,
			EnhancedMapReference<Project> projects,
			EnhancedMapReference<Group> groups) {
		return new Session(parentMap, coordinator, supervisors, students, projects, groups);
	}
}