package com.aau.grouping_system.User.Coordinator;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aau.grouping_system.Database.Database;

// TODO: REMOVE THIS
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Student.Student;

@Service
public class CoordinatorService {

	private final Database db;
	private final PasswordEncoder passwordEncoder;

	// constructors

	public CoordinatorService(Database db, PasswordEncoder passwordEncoder) {
		this.db = db;
		this.passwordEncoder = passwordEncoder;
	}

	// TODO: REMOVE THIS
	public void test() {
		System.out.println("Testing!");
		System.out.println("Testing!");
		System.out.println("Testing!");

		addCoordinator("coordinatorEmail0", "coordinatorEPassword0", "coordinatorName0");
		addCoordinator("coordinatorEEmail1", "coordinatorEPassword1", "coordinatorName1");

		Coordinator c0 = db.getCoordinators().getItem(0);
		Coordinator c1 = db.getCoordinators().getItem(1);

		Session s0 = new Session(db.getSessions(), c0);
		Session s1 = new Session(db.getSessions(), c0);
		Session s2 = new Session(db.getSessions(), c1);

		db.getSessions().put(s0);
		db.getSessions().put(s1);
		db.getSessions().put(s2);

		c0.sessions.add(s0);
		c0.sessions.add(s1);
		c1.sessions.add(s2);

		Student st0 = new Student(db.getStudents(), "studentEmail0", "studentPassword0", "studentName0", s0);
		Student st1 = new Student(db.getStudents(), "studentEmail1", "studentPassword1", "studentName1", s0);
		Student st2 = new Student(db.getStudents(), "studentEmail2", "studentPassword2", "studentName2", s1);
		Student st3 = new Student(db.getStudents(), "studentEmail3", "studentPassword3", "studentName3", s1);
		Student st4 = new Student(db.getStudents(), "studentEmail4", "studentPassword4", "studentName4", s2);
		Student st5 = new Student(db.getStudents(), "studentEmail5", "studentPassword5", "studentName5", s2);

		db.getStudents().put(st0);
		db.getStudents().put(st1);
		db.getStudents().put(st2);
		db.getStudents().put(st3);
		db.getStudents().put(st4);
		db.getStudents().put(st5);

		s0.students.add(st0);
		s0.students.add(st1);
		s1.students.add(st2);
		s1.students.add(st3);
		s2.students.add(st4);
		s2.students.add(st5);

		System.out.println("Students in session 0: ");
		for (Student s : s0.students) {
			System.out.println("- Student: " + s.getName());
		}
		System.out.println("Students in session 1: ");
		for (Student s : s1.students) {
			System.out.println("- Student: " + s.getName());
		}
		System.out.println("All coordinators: ");
		for (Coordinator c : db.getCoordinators().getAllItems().values()) {
			System.out.println("- Coordinator: " + c.getName());
		}
		System.out.println("All sesssions: ");
		for (Session s : db.getSessions().getAllItems().values()) {
			System.out.println("- Session ID: " + s.getMapId());
		}
		System.out.println("All students: ");
		for (Student s : db.getStudents().getAllItems().values()) {
			System.out.println("- Student: " + s.getName());
		}

		System.out.println("!!! Removing coordinator 0 !!!");
		db.getCoordinators().remove(c0);

		System.out.println("All coordinators: ");
		for (Coordinator c : db.getCoordinators().getAllItems().values()) {
			System.out.println("- Coordinator: " + c.getName());
		}
		System.out.println("All sessions: ");
		for (Session s : db.getSessions().getAllItems().values()) {
			System.out.println("- Session ID: " + s.getMapId());
		}
		System.out.println("All students: ");
		for (Student s : db.getStudents().getAllItems().values()) {
			System.out.println("- Student: " + s.getName());
		}
	}

	// methods

	public void addCoordinator(String email, String password, String name) {
		String passwordHash = passwordEncoder.encode(password);
		Coordinator newCoordinator = new Coordinator(null, email, passwordHash, name);
		db.getCoordinators().put(newCoordinator);
	}

	public void modifyEmail(String newEmail, Integer coordinatorId) {
		db.getCoordinators().getItem(coordinatorId).setEmail(newEmail);
	}

	public void modifyPassword(String newPassword, Integer coordinatorId) {
		String passwordHash = passwordEncoder.encode(newPassword);
		db.getCoordinators().getItem(coordinatorId).setPasswordHash(passwordHash);
	}

	public boolean isEmailDuplicate(String email) {
		for (Coordinator existingCoordinator : db.getCoordinators().getAllItems().values()) {
			if (existingCoordinator.getEmail().equals(email)) {
				return true;
			}
		}
		return false;
	}
}
