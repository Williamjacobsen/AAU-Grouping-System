package com.aau.grouping_system.EnhancedMap.Project;

import java.util.List;
import java.util.ArrayList;

public class ProjectController {

	// todo: fjern name og email
	// todo: lav liste af projekter til en del af databasen.

	private String name;
	private String email;
	private List<Project> projects;

	// Constructor (forklar)
	public ProjectController(String name, String email) {
		this.name = name;
		this.email = email;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}