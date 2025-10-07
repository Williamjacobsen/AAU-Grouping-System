package com.aau.grouping_system.Database;

import org.springframework.stereotype.Component;

import com.aau.grouping_system.EnhancedMap.EnhancedMap;
import com.aau.grouping_system.User.Coordinator.Coordinator;

@Component // so we can do dependency injection
public class Database {

	private final EnhancedMap<Coordinator> coordinators = new EnhancedMap<>();

	// getters & setters

	public EnhancedMap<Coordinator> getCoordinators() {
		return coordinators;
	}

}
