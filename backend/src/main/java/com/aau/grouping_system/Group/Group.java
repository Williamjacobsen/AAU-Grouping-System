package com.aau.grouping_system.Group;

import java.util.concurrent.CopyOnWriteArrayList;

import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Session.Session;

public class Group extends DatabaseItem {

	private String sessionId;
	private String name = "";
	private String supervisorId = null;
	private String assignedProjectId = "";
	private String desiredProjectId1 = "";
	private String desiredProjectId2 = "";
	private String desiredProjectId3 = "";
	/// -1 means no preference
	private Integer desiredGroupSizeMin = -1;
	/// -1 means no preference
	private Integer desiredGroupSizeMax = -1;
	private CopyOnWriteArrayList<String> studentIds = new CopyOnWriteArrayList<>();
	private CopyOnWriteArrayList<String> joinRequestStudentIds = new CopyOnWriteArrayList<>();

	public Group(
			Session session,
			String name) {
		this.sessionId = session.getId();
		this.name = name;
	}

	// @formatter:off
	public String getAssignedProjectId() { return assignedProjectId; }
	public void setAssignedProjectId(String assignedProjectId) { this.assignedProjectId = assignedProjectId; }
	public String getDesiredProjectId1() { return desiredProjectId1; }
	public void setDesiredProjectId1(String desiredProjectId1) { this.desiredProjectId1 = desiredProjectId1; }
	public String getDesiredProjectId2() { return desiredProjectId2; }
	public void setDesiredProjectId2(String desiredProjectId2) { this.desiredProjectId2 = desiredProjectId2; }
	public String getDesiredProjectId3() { return desiredProjectId3; }
	public void setDesiredProjectId3(String desiredProjectId3) { this.desiredProjectId3 = desiredProjectId3; }
	public Integer getDesiredGroupSizeMin() { return desiredGroupSizeMin; }
	public void setDesiredGroupSizeMin(Integer desiredGroupSizeMin) { this.desiredGroupSizeMin = desiredGroupSizeMin; }
	public Integer getDesiredGroupSizeMax() { return desiredGroupSizeMax; }
	public void setDesiredGroupSizeMax(Integer desiredGroupSizeMax) { this.desiredGroupSizeMax = desiredGroupSizeMax; }
	public String getSessionId() { return sessionId; }
	public void setSessionId(String sessionId) { this.sessionId = sessionId; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getSupervisorId() { return supervisorId; }
	public void setSupervisorId(String supervisorId) { this.supervisorId = supervisorId; }
	public CopyOnWriteArrayList<String> getStudentIds() { return studentIds; }
	public CopyOnWriteArrayList<String> getJoinRequestStudentIds() { return joinRequestStudentIds; }
}