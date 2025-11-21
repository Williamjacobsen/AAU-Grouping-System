package com.aau.grouping_system.User.SessionMember.Student;

import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.SessionMember.SessionMember;

public class Student extends SessionMember {

	private String groupId = null;
	private String activeJoinRequestGroupId = null;

	private StudentQuestionnaire questionnaire = new StudentQuestionnaire();

	public Student(String email, String name, Session session) {
		super(email, name, session);
	}

	@Override
	public Role getRole() {
		return Role.Student;
	}

	// @formatter:off
	public StudentQuestionnaire getQuestionnaire() { return questionnaire; }
	public void setQuestionnaire(StudentQuestionnaire questionnaire) { this.questionnaire = questionnaire; }
	public String getGroupId() { return groupId; }
	public void setGroupId(String groupId) { this.groupId = groupId; }
	public String getActiveJoinRequestGroupId() { return activeJoinRequestGroupId; }
	public void setActiveJoinRequestGroupId(String requestToJoinGroupId) { this.activeJoinRequestGroupId = requestToJoinGroupId; }
}
