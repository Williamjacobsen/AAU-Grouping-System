package com.aau.grouping_system.User.Student;

import com.aau.grouping_system.User.User;
import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Database.DatabaseMap;

import java.util.concurrent.CopyOnWriteArrayList;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseItemChildGroup;
import com.aau.grouping_system.Session.Session;

public class Student extends User {

	private String sessionId;
	private Questionnaire questionnaire = new Questionnaire();

	public static class Questionnaire {
		public CopyOnWriteArrayList<String> desiredProjectsIds = new CopyOnWriteArrayList<>();
		public CopyOnWriteArrayList<String> desiredStudentIds = new CopyOnWriteArrayList<>();
		/// -1 means no preference
		public Integer desiredGroupSizeMin = -1;
		/// -1 means no preference
		public Integer desiredGroupSizeMax = -1;
		public WorkLocation desiredWorkLocation = WorkLocation.NoPreference;
		public WorkStyle desiredWorkStyle = WorkStyle.NoPreference;
		public String personalSkills = "";
		public String specialNeeds = "";
		public String academicInterests = "";
		public String comments = "";
	}

	public Student(Database db, DatabaseItemChildGroup parentItemChildIdList,
			String email, String passwordHash, String name, Session session) {
		super(db, parentItemChildIdList, email, passwordHash, name);
		this.sessionId = session.getId();
	}

	private enum WorkLocation {
		NoPreference,
		Located,
		Remote;
	}

	private enum WorkStyle {
		NoPreference,
		Solo,
		Together;
	}

	@Override
	protected DatabaseMap<? extends DatabaseItem> getDatabaseMap(Database db) {
		return db.getStudents();
	}

	@Override
	public Role getRole() {
		return Role.Student;
	}

	public String getSessionId() {
		return sessionId;
	}

	public Questionnaire getQuestionnaire() {
		return questionnaire;
	}

	public void setQuestionnaire(Questionnaire questionnaire) {
		this.questionnaire = questionnaire;
	}

}
