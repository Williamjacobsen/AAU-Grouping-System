package com.aau.grouping_system.Sessions;

public class Session {
    private Coordinator coordinator;
    private Supervisor[] supervisors;
    private Student[] students;
    private Project[] projects;
    private Group[] groups;

    // Constructor
    public Session(Coordinator coordinator, Supervisor[] supervisors, Student[] students,
                   Project[] projects, Group[] groups) {
        this.coordinator = coordinator;
        this.supervisors = supervisors;
        this.students = students;
        this.projects = projects;
        this.groups = groups;
    }

    // Getters og setters
    public Coordinator getCoordinator() { return coordinator; }
    public void setCoordinator(Coordinator coordinator) { this.coordinator = coordinator; }

    public Supervisor[] getSupervisors() { return supervisors; }
    public void setSupervisors(Supervisor[] supervisors) { this.supervisors = supervisors; }

    public Student[] getStudents() { return students; }
    public void setStudents(Student[] students) { this.students = students; }

    public Project[] getProjects() { return projects; }
    public void setProjects(Project[] projects) { this.projects = projects; }

    public Group[] getGroups() { return groups; }
    public void setGroups(Group[] groups) { this.groups = groups; }

}