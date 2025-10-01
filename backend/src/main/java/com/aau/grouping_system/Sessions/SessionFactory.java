package com.aau.grouping_system.Sessions;

public class SessionFactory {
    public static Session create(Coordinator coordinator, Supervisor[] supervisors, Student[] students,
                                 Project[] projects, Group[] groups) {
        return new Session(coordinator, supervisors, students, projects, groups);
    }
}