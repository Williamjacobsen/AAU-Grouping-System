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
	private String questionnaireDeadline = null;
	private String description;
	private String coordinatorName;
	private String initialProjects;
	private String optionalQuestionnaire;
	private int groupSize;

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
	public String getQuestionnaireDeadline() { return questionnaireDeadline; }
	public void setQuestionnaireDeadline(String questionnaireDeadline) { this.questionnaireDeadline = questionnaireDeadline; }

	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }

	public String getCoordinatorName() { return coordinatorName; }
	public void setCoordinatorName(String coordinatorName) { this.coordinatorName = coordinatorName; }

	public String getInitialProjects() { return initialProjects; }
	public void setInitialProjects(String initialProjects) { this.initialProjects = initialProjects; }

	public String getOptionalQuestionnaire() { return optionalQuestionnaire; }
	public void setOptionalQuestionnaire(String optionalQuestionnaire) { this.optionalQuestionnaire = optionalQuestionnaire; }

	public int getGroupSize() { return groupSize; }
	public void setGroupSize(int groupSize) { this.groupSize = groupSize; }

}