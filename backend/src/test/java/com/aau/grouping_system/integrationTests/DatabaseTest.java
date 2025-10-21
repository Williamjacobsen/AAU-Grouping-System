package com.aau.grouping_system.integrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

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

	ArrayList<Coordinator> coordinators = new ArrayList<>();
	ArrayList<Session> sessions = new ArrayList<>();
	ArrayList<Student> students = new ArrayList<>();

	@BeforeEach
	void fillDatabase() {

		// Create dependencies
		db = new Database();
		passwordEncoder = new BCryptPasswordEncoder();
		coordinatorService = new CoordinatorService(db, passwordEncoder);

		// Add 2 coordinators
		coordinators
				.add(coordinatorService.addCoordinator("coordinatorEmail0", "coordinatorPassword0", "coordinatorName0"));
		coordinators
				.add(coordinatorService.addCoordinator("coordinatorEmail1", "coordinatorPassword1", "coordinatorName1"));

		// Add 3 sessions
		sessions.add(new Session(db, coordinators.get(0).sessions, coordinators.get(0), "sessionName0"));
		sessions.add(new Session(db, coordinators.get(0).sessions, coordinators.get(0), "sessionName1"));
		sessions.add(new Session(db, coordinators.get(1).sessions, coordinators.get(1), "sessionName2"));

		// Add 6 students
		students.add(new Student(db, sessions.get(0).students, "studentEmail0", "studentPassword0", "studentName0",
				sessions.get(0)));
		students.add(new Student(db, sessions.get(0).students, "studentEmail1", "studentPassword1", "studentName1",
				sessions.get(0)));
		students.add(new Student(db, sessions.get(1).students, "studentEmail2", "studentPassword2", "studentName2",
				sessions.get(1)));
		students.add(new Student(db, sessions.get(1).students, "studentEmail3", "studentPassword3", "studentName3",
				sessions.get(1)));
		students.add(new Student(db, sessions.get(2).students, "studentEmail4", "studentPassword4", "studentName4",
				sessions.get(2)));
		students.add(new Student(db, sessions.get(2).students, "studentEmail5", "studentPassword5", "studentName5",
				sessions.get(2)));
	}

	@Test
	void databaseShouldBeFilled() {
		// Check map sizes
		assertEquals(2, db.getCoordinators().getAllItems().size());
		assertEquals(3, db.getSessions().getAllItems().size());
		assertEquals(6, db.getStudents().getAllItems().size());

		// Check coordinator emails
		assertEquals("coordinatorEmail0", coordinators.get(0).getEmail());
		assertEquals("coordinatorEmail1", coordinators.get(1).getEmail());

		// Check student emails
		assertEquals("studentEmail0", students.get(0).getEmail());
		assertEquals("studentEmail1", students.get(1).getEmail());
		assertEquals("studentEmail2", students.get(2).getEmail());
		assertEquals("studentEmail3", students.get(3).getEmail());
		assertEquals("studentEmail4", students.get(4).getEmail());
		assertEquals("studentEmail5", students.get(5).getEmail());
	}

	@Test
	void deletingDatabaseItemShouldDeleteChildItems() {
		// Before deletion
		assertEquals(2, db.getCoordinators().getAllItems().size());
		assertEquals(3, db.getSessions().getAllItems().size());
		assertEquals(6, db.getStudents().getAllItems().size());

		// Remove the first coordinator
		db.getCoordinators().remove(db, coordinators.get(0));

		// After deletion
		assertEquals(1, db.getCoordinators().getAllItems().size());
		assertEquals(1, db.getSessions().getAllItems().size());
		assertEquals(2, db.getStudents().getAllItems().size());
	}
}
