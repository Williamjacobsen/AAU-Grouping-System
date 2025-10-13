package com.aau.grouping_system.integrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Coordinator.CoordinatorService;
import com.aau.grouping_system.User.Student.Student;

@SpringBootTest
public class DatabaseTest {

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
		coordinatorService.addCoordinator("coordinatorEmail0", "coordinatorPassword0", "coordinatorName0");
		coordinatorService.addCoordinator("coordinatorEmail1", "coordinatorPassword1", "coordinatorName1");
		Coordinator c0 = db.getCoordinators().getItem(0);
		Coordinator c1 = db.getCoordinators().getItem(1);

		// Add 3 sessions
		Session s0c0 = new Session(db.getSessions(), c0.sessions, c0);
		Session s1c0 = new Session(db.getSessions(), c0.sessions, c0);
		Session s2c1 = new Session(db.getSessions(), c1.sessions, c1);

		// Add 6 students
		new Student(db.getStudents(), s0c0.students, "studentEmail0", "studentPassword0", "studentName0", s0c0);
		new Student(db.getStudents(), s0c0.students, "studentEmail1", "studentPassword1", "studentName1", s0c0);
		new Student(db.getStudents(), s1c0.students, "studentEmail2", "studentPassword2", "studentName2", s1c0);
		new Student(db.getStudents(), s1c0.students, "studentEmail3", "studentPassword3", "studentName3", s1c0);
		new Student(db.getStudents(), s2c1.students, "studentEmail4", "studentPassword4", "studentName4", s2c1);
		new Student(db.getStudents(), s2c1.students, "studentEmail5", "studentPassword5", "studentName5", s2c1);
	}

	@Test
	void databaseShouldBeFilled() {
		// Check map sizes
		assertEquals(2, db.getCoordinators().getAllItems().size());
		assertEquals(3, db.getSessions().getAllItems().size());
		assertEquals(6, db.getStudents().getAllItems().size());

		// Check coordinator emails
		assertEquals("coordinatorEmail0", db.getCoordinators().getItem(0).getEmail());
		assertEquals("coordinatorEmail1", db.getCoordinators().getItem(1).getEmail());

		// Check student emails
		assertEquals("studentEmail0", db.getStudents().getItem(0).getEmail());
		assertEquals("studentEmail1", db.getStudents().getItem(1).getEmail());
		assertEquals("studentEmail2", db.getStudents().getItem(2).getEmail());
		assertEquals("studentEmail3", db.getStudents().getItem(3).getEmail());
		assertEquals("studentEmail4", db.getStudents().getItem(4).getEmail());
		assertEquals("studentEmail5", db.getStudents().getItem(5).getEmail());
	}

	@Test
	void deletingDatabaseItemShouldDeleteChildItems() {
		// Before deletion
		assertEquals(2, db.getCoordinators().getAllItems().size());
		assertEquals(3, db.getSessions().getAllItems().size());
		assertEquals(6, db.getStudents().getAllItems().size());

		// Remove coordinator at ID = 0
		Coordinator c0 = db.getCoordinators().getItem(0);
		db.getCoordinators().remove(c0);

		// After deletion
		assertEquals(1, db.getCoordinators().getAllItems().size());
		assertEquals(1, db.getSessions().getAllItems().size());
		assertEquals(2, db.getStudents().getAllItems().size());
	}
}
