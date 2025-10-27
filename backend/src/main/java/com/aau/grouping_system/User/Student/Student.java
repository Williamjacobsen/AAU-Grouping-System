package com.aau.grouping_system.User.Student;

import com.aau.grouping_system.User.User;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.item.DatabaseItem;
import com.aau.grouping_system.Database.item.ItemReferenceList;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.StudentQuestionnaire.StudentQuestionnaire;

public class Student extends User {

	Session session;
	StudentQuestionnaire questionnaire;

	// constructors

	public Student(DatabaseMap<? extends DatabaseItem> parentDatabaseMap,
			ItemReferenceList<? extends DatabaseItem> parentReferenceList,
			String email, String passwordHash, String name, Session session) {
		super(parentDatabaseMap, parentReferenceList, email, passwordHash, name);
		this.session = session;
		this.questionnaire = null;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public StudentQuestionnaire getQuestionnaire() {
		return questionnaire;
	}

	public void setQuestionnaire(StudentQuestionnaire questionnaire) {
		this.questionnaire = questionnaire;
	}

	// abstract method overrides

	@Override
	public Role getRole() {
		return Role.STUDENT;
	}

}
