package com.aau.grouping_system.User.Student;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Session.Session;

@Service
public class StudentService {

	private final Database db;
	private final PasswordEncoder passwordEncoder;

	public StudentService(Database db, PasswordEncoder passwordEncoder) {
		this.db = db;
		this.passwordEncoder = passwordEncoder;
	}

	public Student addStudent(Session session, String email, String password, String name) {
		String passwordHash = passwordEncoder.encode(password);
		Student newStudent = db.getStudents().addItem(
				db,
				session.getStudents(),
				new Student(email, passwordHash, name, session));
		return newStudent;
	}

	public void applyQuestionnaireAnswers(Student student, StudentQuestionnaire updatedQuestionnaire) {
		student.setQuestionnaire(updatedQuestionnaire);
	}

	public void removeStudent(Student student) {
		db.getStudents().cascadeRemoveItem(db, student);
	}
}
