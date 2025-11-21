package com.aau.grouping_system.User.Student;

import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.User;

public class Student extends User {

	private String sessionId;
	private String groupId = null;
	private String activeJoinRequestGroupId = null;

	private StudentQuestionnaire questionnaire = new StudentQuestionnaire();

	public Student(String email, String passwordHash, String name, Session session) {
		super(email, passwordHash, name);
		this.sessionId = session.getId();
	}

	@Override
	public Role getRole() {
		return Role.Student;
	}

	// @formatter:off
	public String getSessionId() { return sessionId; }
	public StudentQuestionnaire getQuestionnaire() { return questionnaire; }
	public void setQuestionnaire(StudentQuestionnaire questionnaire) { this.questionnaire = questionnaire; }
	public String getGroupId() { return groupId; }
	public void setGroupId(String groupId) { this.groupId = groupId; }
	public String getActiveJoinRequestGroupId() { return activeJoinRequestGroupId; }
	public void setActiveJoinRequestGroupId(String requestToJoinGroupId) { this.activeJoinRequestGroupId = requestToJoinGroupId; }
}
