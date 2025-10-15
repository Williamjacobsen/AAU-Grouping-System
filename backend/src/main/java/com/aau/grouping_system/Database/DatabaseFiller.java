package com.aau.grouping_system.Database;

import org.springframework.stereotype.Component;

import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;

import jakarta.annotation.PostConstruct;

@Component
public class DatabaseFiller {

	private final Database db;

	public DatabaseFiller(Database db) {
		this.db = db;
	}

	@SuppressWarnings("unused") // To suppress warnings relative to unused code.
	private void fillDatabaseWithExampleData() {
		Coordinator c0 = new Coordinator(db.getCoordinators(), null, db, "CoordinatorA@example.com", "PasswordHashA",
				"CoordinatorA");
		Coordinator c1 = new Coordinator(db.getCoordinators(), null, db, "CoordinatorB@example.com", "PasswordHashB",
				"CoordinatorB");
		Coordinator c2 = new Coordinator(db.getCoordinators(), null, db, "CoordinatorC@example.com", "PasswordHashC",
				"CoordinatorC");

		Session s0 = new Session(db.getSessions(), c0.sessions, db, c0);
		Session s1 = new Session(db.getSessions(), c0.sessions, db, c0);
		Session s2 = new Session(db.getSessions(), c1.sessions, db, c1);

		Student st0 = new Student(db.getStudents(), s0.students, "Student0@example.com", "PasswordHash0", "Student0", s0);
		Student st1 = new Student(db.getStudents(), s0.students, "Student1@example.com", "PasswordHash1", "Student1", s0);
		Student st2 = new Student(db.getStudents(), s0.students, "Student2@example.com", "PasswordHash2", "Student2", s0);
		Student st3 = new Student(db.getStudents(), s1.students, "Student3@example.com", "PasswordHash3", "Student3", s1);
		Student st4 = new Student(db.getStudents(), s1.students, "Student4@example.com", "PasswordHash4", "Student4", s1);
		Student st5 = new Student(db.getStudents(), s2.students, "Student5@example.com", "PasswordHash5", "Student5", s2);
		Student st6 = new Student(db.getStudents(), s2.students, "Student6@example.com", "PasswordHash6", "Student6", s2);
	}

	@PostConstruct
	public void init() {
		fillDatabaseWithExampleData();
	}
}
