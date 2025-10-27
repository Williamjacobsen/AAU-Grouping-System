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
		return new Student(db, session.students, email, passwordHash, name, session);
	}

	public void applyQuestionnaireAnswers(Student student, Student.Questionnaire updatedQuestionnaire) {
		student.setQuestionnaire(updatedQuestionnaire);
	}
}
