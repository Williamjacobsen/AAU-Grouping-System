package com.aau.grouping_system.Database;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.aau.grouping_system.User.Coordinator.Coordinator;

import jakarta.annotation.PostConstruct;

@Component // so we can do dependency injection
public class Database {

	private final Map<Integer, Coordinator> coordinators = new ConcurrentHashMap<>();
	private final AtomicInteger idGenerator = new AtomicInteger();

	public int saveCoordinator(Coordinator coordinator) {
		int id = idGenerator.incrementAndGet();
		coordinator.setDatabaseID(id);
		coordinators.put(id, coordinator);
		return id;
	}

	public Map<Integer, Coordinator> getAllCoordinators() {
		return coordinators;
	}
	
	// Test coordinator

	@PostConstruct
	public void init() {
    PasswordEncoder encoder = new BCryptPasswordEncoder();
    String hashedPassword = encoder.encode("Password123");
    Coordinator testUser = new Coordinator("test@example.com", hashedPassword, "Test User");
    saveCoordinator(testUser);
}

}
