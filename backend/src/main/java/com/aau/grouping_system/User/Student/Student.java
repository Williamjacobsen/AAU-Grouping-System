package com.aau.grouping_system.User.Student;

import java.util.concurrent.CopyOnWriteArrayList;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Database.DatabaseItemChildGroup;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.User;

public class Student extends User {

	private String sessionId;
	private Questionnaire questionnaire = new Questionnaire();

	public static class Questionnaire {
		public CopyOnWriteArrayList<String> desiredProjectIds = new CopyOnWriteArrayList<>();
		/// -1 means no preference
		public Integer desiredGroupSizeMin = -1;
		/// -1 means no preference
		public Integer desiredGroupSizeMax = -1;
		public Student.WorkLocation desiredWorkLocation = Student.WorkLocation.NoPreference;
		public Student.WorkStyle desiredWorkStyle = Student.WorkStyle.NoPreference;
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

	public enum WorkLocation {
		NoPreference,
		Located,
		Remote;
	}

	public enum WorkStyle {
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
