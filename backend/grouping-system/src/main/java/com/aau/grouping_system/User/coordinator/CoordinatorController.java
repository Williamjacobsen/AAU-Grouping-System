package com.aau.grouping_system.User.coordinator;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.database.Database;

@RestController // singleton bean
@RequestMapping("/coordinator")
public class CoordinatorController {

    private final Coordinator coordinator
            = new Coordinator("coordinator1", "John i guess", "IT?");

    private final Database db;

    // dependency injection
    public CoordinatorController(Database db) {
        this.db = db;
    }

    @GetMapping
    public Map<Integer, String> getSessions() {
        return db.getAllSessions();
    }

    // How to test: send a post request "http://localhost:8080/coordinator/createSession?sessionName=test"
    @PostMapping("/createSession")
    public String createSession(@RequestParam String sessionName) {
        return coordinator.createSession(db, sessionName);
    }

    @GetMapping("/me")
    public Coordinator getCoordinator() {
        return coordinator;
    }
}
