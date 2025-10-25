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

	private CopyOnWriteArrayList<String> desiredProjectsIds = new CopyOnWriteArrayList<>();
	private CopyOnWriteArrayList<String> desiredStudentIds = new CopyOnWriteArrayList<>();
	private Integer desiredGroupSize = null;
	private WorkLocation desiredWorkLocation = WorkLocation.NoPreference;
	private WorkStyle desiredWorkStyle = WorkStyle.NoPreference;
	private String personalSkills = "";
	private String specialNeeds = "";
	private String academicInterests = "";
	private String comments = "";

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

}
