package com.aau.grouping_system.Group;

import java.util.concurrent.CopyOnWriteArrayList;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Database.DatabaseItemChildGroup;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.User.Supervisor.Supervisor;

public class Group extends DatabaseItem {

	private String supervisorId;
	private CopyOnWriteArrayList<String> studentIds;
	private String projectId;
	private String groupEmail;
	private CopyOnWriteArrayList<String> joinRequestStudentIds;
	private int maxStudents;
	private int maxRequests;

	public Group(Database db, DatabaseItemChildGroup parentItemChildIdList,
			Supervisor supervisor, Project project, String groupEmail, int maxStudents, int maxRequests) {
		super(db, parentItemChildIdList);
		this.supervisorId = supervisor.getId();
		this.projectId = project.getId();
		this.groupEmail = groupEmail;
		this.maxStudents = maxStudents;
		this.maxRequests = maxRequests;
		this.studentIds = new CopyOnWriteArrayList<>();
		this.joinRequestStudentIds = new CopyOnWriteArrayList<>();
	}

	@Override
	protected DatabaseMap<? extends DatabaseItem> getDatabaseMap(Database db) {
		return db.getGroups();
	}

	// @formatter:off
	public String getSupervisorId() { return supervisorId; }
	public void setSupervisorId(String supervisorId) { this.supervisorId = supervisorId; }
	public CopyOnWriteArrayList<String> getStudentIds() { return studentIds; }
	public String getProjectId() { return projectId; }
	public void setProject(String projectId) { this.projectId = projectId; }
	public String getGroupEmail() { return groupEmail; }
	public void setGroupEmail(String groupEmail) { this.groupEmail = groupEmail; }
	public CopyOnWriteArrayList<String> getJoinRequestStudentIds() { return joinRequestStudentIds; }
	public int getMaxStudents() { return maxStudents; }
	public void setMaxStudents(int maxStudents) { this.maxStudents = maxStudents; }
	public int getMaxRequests() { return maxRequests; }
	public void setMaxRequests(int maxRequests) { this.maxRequests = maxRequests; }
}