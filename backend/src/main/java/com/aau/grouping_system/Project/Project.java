package com.aau.grouping_system.Project;

public class Project {
    private String projectName;
    private String description;
    private int projectId;

    public Project(String projectName, String description, int projectId) {
        this.projectName = projectName;
        this.description = description;
        this.projectId = projectId;
    }

    // Getters and setters
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}