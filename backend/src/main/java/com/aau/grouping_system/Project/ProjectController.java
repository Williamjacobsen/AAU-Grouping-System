package com.aau.grouping_system.Project;

import java.util.List;
import java.util.ArrayList;

public class ProjectController {

	// todo: lav liste af projekter til en del af databasen.

	private List<Project> projects;

	// Constructor (forklar)
	public ProjectController(String name, String email) {
		this.projects = new ArrayList<>();
	}

	// returnerer intet(void); hvis denne kører, så tager den project som parameter
	public void addProject(Project project) {
		projects.add(project);
	}

	// Sæt kommentarer
	public void removeProject(Project project) {
		projects.remove(project);
	}

	public List<Project> getProjects() {
		return projects;
	}
}