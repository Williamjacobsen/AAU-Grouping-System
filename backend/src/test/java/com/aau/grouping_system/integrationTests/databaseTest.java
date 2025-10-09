package com.aau.grouping_system.integrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Coordinator.CoordinatorService;
import com.aau.grouping_system.User.Student.Student;

public class databaseTest {

	Database db;
	PasswordEncoder passwordEncoder;
	CoordinatorService coordinatorService;

	@BeforeEach
	void fillDatabase() {

		// Create dependencies
		db = new Database();
		passwordEncoder = new BCryptPasswordEncoder();
		coordinatorService = new CoordinatorService(db, passwordEncoder);

		// Add 2 coordinators
		coordinatorService.addCoordinator("coordinatorEmail0", "coordinatorEPassword0", "coordinatorName0");
		coordinatorService.addCoordinator("coordinatorEEmail1", "coordinatorEPassword1", "coordinatorName1");
		Coordinator coordinator0 = db.getCoordinators().getItem(0);
		Coordinator coordinator1 = db.getCoordinators().getItem(1);

		// Add 3 sessions
		// Create objects
		Session session0coordinator0 = new Session(db.getSessions(), coordinator0);
		Session session1coordinator0 = new Session(db.getSessions(), coordinator0);
		Session session2coordinator1 = new Session(db.getSessions(), coordinator1);
		// Save in database
		db.getSessions().put(session0coordinator0);
		db.getSessions().put(session1coordinator0);
		db.getSessions().put(session2coordinator1);
		// Declare parent
		coordinator0.sessions.add(session0coordinator0);
		coordinator0.sessions.add(session1coordinator0);
		coordinator1.sessions.add(session2coordinator1);

		// Add 6 students
		// Create objects
		Student student0session0 = new Student(db.getStudents(), "studentEmail0", "studentPassword0", "studentName0",
				session0coordinator0);
		Student student1session0 = new Student(db.getStudents(), "studentEmail1", "studentPassword1", "studentName1",
				session0coordinator0);
		Student student2session1 = new Student(db.getStudents(), "studentEmail2", "studentPassword2", "studentName2",
				session1coordinator0);
		Student student3session1 = new Student(db.getStudents(), "studentEmail3", "studentPassword3", "studentName3",
				session1coordinator0);
		Student student4session2 = new Student(db.getStudents(), "studentEmail4", "studentPassword4", "studentName4",
				session2coordinator1);
		Student student5session2 = new Student(db.getStudents(), "studentEmail5", "studentPassword5", "studentName5",
				session2coordinator1);
		// Save in database
		db.getStudents().put(student0session0);
		db.getStudents().put(student1session0);
		db.getStudents().put(student2session1);
		db.getStudents().put(student3session1);
		db.getStudents().put(student4session2);
		db.getStudents().put(student5session2);
		// Declare parent
		session0coordinator0.students.add(student0session0);
		session0coordinator0.students.add(student1session0);
		session1coordinator0.students.add(student2session1);
		session1coordinator0.students.add(student3session1);
		session2coordinator1.students.add(student4session2);
		session2coordinator1.students.add(student5session2);
	}

	@Test
	void databaseShouldBeFilled() {
		assertEquals(db.getCoordinators().getAllItems().size(), 2);
		assertEquals(db.getSessions().getAllItems().size(), 3);
		assertEquals(db.getStudents().getAllItems().size(), 6);
	}

	@Test
	void deletingDatabaseItemShouldDeleteChildItems() {
		// Before deletion
		assertEquals(db.getCoordinators().getAllItems().size(), 2);
		assertEquals(db.getSessions().getAllItems().size(), 3);
		assertEquals(db.getStudents().getAllItems().size(), 6);

		// Remove coordinator at ID = 0
		Coordinator coordinator0 = db.getCoordinators().getItem(0);
		db.getCoordinators().remove(coordinator0);

		// After deletion
		assertEquals(db.getCoordinators().getAllItems().size(), 1);
		assertEquals(db.getSessions().getAllItems().size(), 1);
		assertEquals(db.getStudents().getAllItems().size(), 2);
	}
}
