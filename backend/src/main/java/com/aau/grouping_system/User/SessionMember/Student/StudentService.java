package com.aau.grouping_system.User.SessionMember.Student;

import org.springframework.stereotype.Service;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Session.Session;

@Service
public class StudentService {

	private final Database db;

	public StudentService(
			Database db) {
		this.db = db;
	}

	public Student addStudent(Session session, String email, String name) {
		Student newStudent = db.getStudents().addItem(
				session.getStudents(),
				new Student(email, name, session));
		return newStudent;
	}

	public void applyQuestionnaireAnswers(Student student, StudentQuestionnaire updatedQuestionnaire) {
		student.setQuestionnaire(updatedQuestionnaire);
	}

	public void removeStudent(Student student) {
		db.getStudents().cascadeRemoveItem(db, student);
	}
}
