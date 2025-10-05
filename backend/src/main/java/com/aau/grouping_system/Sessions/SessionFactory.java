package com.aau.grouping_system.Sessions;
import java.util.List;

import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.User.Coordinator.Coordinator;

public class SessionFactory {
    public static Session create(Coordinator coordinator, List<Supervisor> supervisors, List<Student> students,
                                 List<Project> projects, List<Group> groups) {
        return new Session(coordinator, supervisors, students, projects, groups);
    }
}