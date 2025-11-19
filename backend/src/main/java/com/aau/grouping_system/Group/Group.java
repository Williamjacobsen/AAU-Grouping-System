package com.aau.grouping_system.Group;

import java.util.concurrent.CopyOnWriteArrayList;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Database.DatabaseItemChildGroup;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Session.Session;

public class Group extends DatabaseItem {

	private String sessionId;
	private String name = "";
	private String supervisorId = null;
	private String projectId = null;
	private CopyOnWriteArrayList<String> studentIds = new CopyOnWriteArrayList<>();
	private CopyOnWriteArrayList<String> joinRequestStudentIds = new CopyOnWriteArrayList<>();

	public Group(
			Database db,
			DatabaseItemChildGroup parentItemChildIdList,
			Session session,
			String name) {
		super(db, parentItemChildIdList);
		this.sessionId = session.getId();
		this.name = name;
	}

	@Override
	protected DatabaseMap<? extends DatabaseItem> getDatabaseMap(Database db) {
		return db.getGroups();
	}

	// @formatter:off
	public String getSessionId() { return sessionId; }
	public void setSessionId(String sessionId) { this.sessionId = sessionId; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getSupervisorId() { return supervisorId; }
	public void setSupervisorId(String supervisorId) { this.supervisorId = supervisorId; }
	public CopyOnWriteArrayList<String> getStudentIds() { return studentIds; }
	public String getProjectId() { return projectId; }
	public void setProjectId(String projectId) { this.projectId = projectId; }
	public CopyOnWriteArrayList<String> getJoinRequestStudentIds() { return joinRequestStudentIds; }
}