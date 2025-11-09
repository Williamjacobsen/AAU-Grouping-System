package com.aau.grouping_system.Session;

import java.time.LocalDateTime;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.DatabaseItemChildGroup;
import com.aau.grouping_system.User.Coordinator.Coordinator;

public class Session extends DatabaseItem {

	private String coordinatorId;

	private DatabaseItemChildGroup supervisors;
	private DatabaseItemChildGroup students;
	private DatabaseItemChildGroup projects;
	private DatabaseItemChildGroup groups;
	private String name;
	private LocalDateTime questionnaireDeadline = null;

	public Session(Database db, DatabaseItemChildGroup parentItemChildIdList,
			Coordinator coordinator, String name) {
		super(db, parentItemChildIdList);
		this.coordinatorId = coordinator.getId();
		this.supervisors = new DatabaseItemChildGroup(db.getSupervisors(), this);
		this.students = new DatabaseItemChildGroup(db.getStudents(), this);
		this.projects = new DatabaseItemChildGroup(db.getProjects(), this);
		this.groups = new DatabaseItemChildGroup(db.getGroups(), this);
		this.name = name;
	}

	@Override
	protected DatabaseMap<? extends DatabaseItem> getDatabaseMap(Database db) {
		return db.getSessions();
	}

	// @formatter:off
	public String getCoordinatorId() { return coordinatorId; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public DatabaseItemChildGroup getSupervisors() { return supervisors; }
	public void setSupervisors(DatabaseItemChildGroup supervisors) { this.supervisors = supervisors; }
	public DatabaseItemChildGroup getStudents() { return students; }
	public void setStudents(DatabaseItemChildGroup students) { this.students = students; }
	public DatabaseItemChildGroup getProjects() { return projects; }
	public void setProjects(DatabaseItemChildGroup projects) { this.projects = projects; }
	public DatabaseItemChildGroup getGroups() { return groups; }
	public void setGroups(DatabaseItemChildGroup groups) { this.groups = groups; }
	public LocalDateTime getQuestionnaireDeadline() { return questionnaireDeadline; }
	public void setQuestionnaireDeadline(LocalDateTime questionnaireDeadline) { this.questionnaireDeadline = questionnaireDeadline; }

}