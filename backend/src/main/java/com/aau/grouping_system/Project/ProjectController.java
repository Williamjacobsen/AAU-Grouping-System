package com.aau.grouping_system.Project;

import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Database.Database;

import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/project") // mapping, all URLs that has /project are handled here
public class ProjectController {

	private final Database db; // storage in db (final never changes once set)

	// constructor
	// dependency injection
	public ProjectController(Database db) {
		this.db = db;
	}

}
