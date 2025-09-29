package com.aau.grouping_system.User.coordinator;

import com.aau.grouping_system.User.User;
import com.aau.grouping_system.database.Database;

// Inheritance
public class Coordinator extends User {

    private String department;

    public Coordinator() {
    }

    public Coordinator(String id, String name, String department) {
        super(id, name);
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String createSession(Database db, String sessionName) {
        // add logic like validation
        int id = db.saveSession(sessionName);
        return "Coordinator " + getName() + " created session: " + sessionName + " (id=" + id + ")";
    }

    // If some method implemented in the parent class is @Override here,
    // then that would be polymorphism
}
