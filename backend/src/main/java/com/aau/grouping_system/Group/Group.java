package com.aau.grouping_system.Group;

import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseIdList;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;

import java.util.concurrent.CopyOnWriteArrayList;

public class Group extends DatabaseItem {

	private Supervisor supervisor;
	private CopyOnWriteArrayList<Student> students;
	private Project project;
	private String groupEmail;
	private CopyOnWriteArrayList<Student> joinRequests;
	private int maxStudents;
	private int maxRequests;

	public Group(Database db, DatabaseIdList parentItemChildIdList,
			Supervisor supervisor, Project project, String groupEmail, int maxStudents, int maxRequests) {
		super(db, parentItemChildIdList);
		this.supervisor = supervisor;
		this.project = project;
		this.groupEmail = groupEmail;
		this.maxStudents = maxStudents;
		this.maxRequests = maxRequests;
		this.students = new CopyOnWriteArrayList<>();
		this.joinRequests = new CopyOnWriteArrayList<>();
	}

	@Override
	protected DatabaseMap<? extends DatabaseItem> getDatabaseMap(Database db) {
		return db.getGroups();
	}

	// getters & setters

	public Supervisor getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(Supervisor supervisor) {
		this.supervisor = supervisor;
	}

	public CopyOnWriteArrayList<Student> getStudents() {
		return students;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getGroupEmail() {
		return groupEmail;
	}

	public void setGroupEmail(String groupEmail) {
		this.groupEmail = groupEmail;
	}

	public CopyOnWriteArrayList<Student> getJoinRequests() {
		return joinRequests;
	}

	public int getMaxStudents() {
		return maxStudents;
	}

	public void setMaxStudents(int maxStudents) {
		this.maxStudents = maxStudents;
	}

	public int getMaxRequests() {
		return maxRequests;
	}

	public void setMaxRequests(int maxRequests) {
		this.maxRequests = maxRequests;
	}

}