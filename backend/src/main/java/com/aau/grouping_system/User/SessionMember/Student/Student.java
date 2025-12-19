package com.aau.grouping_system.User.SessionMember.Student;

import com.aau.grouping_system.Database.Database;
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
	protected void onCascadeRemove(Database db) {
		// Disconnect from linked group
		if (groupId != null) {
			db.getGroups().getItem(this.groupId).getStudentIds().remove(this.getId());
		}
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
