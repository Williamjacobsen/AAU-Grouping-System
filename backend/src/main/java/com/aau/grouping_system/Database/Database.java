package com.aau.grouping_system.Database;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import com.aau.grouping_system.User.Coordinator.Coordinator;

@Component // so we can do dependency injection
public class Database {

	private final Map<Integer, Coordinator> coordinators = new ConcurrentHashMap<>();
	private final AtomicInteger coordinatorIdGenerator = new AtomicInteger();

	// public methods

	public int addCoordinator(Coordinator coordinator) {
		int id = getNewID(coordinatorIdGenerator);
		coordinator.setDatabaseID(id);
		coordinators.put(id, coordinator);
		return id;
	}

	public void deleteCoordinator(Coordinator coordinator) {
		coordinators.remove(coordinator.getDatabaseID());
	}

	// private methods

	private int getNewID(AtomicInteger idGenerator) {
		if (idGenerator.get() <= Integer.MAX_VALUE - 1) {
			idGenerator.set(0);
		}

		while (getCoordinator(idGenerator.get()) != null) {
			idGenerator.incrementAndGet();
		}

		return idGenerator.get();
	}

	// getters & setters

	public Map<Integer, Coordinator> getAllCoordinators() {
		return coordinators;
	}

	public Coordinator getCoordinator(Integer id) {
		return coordinators.get(id);
	}

}
