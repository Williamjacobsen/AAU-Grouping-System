package com.aau.grouping_system.Group;

import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Database.DatabaseIdList;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;

import java.util.ArrayList;
import java.util.List;

public class Group extends DatabaseItem {

	private Supervisor supervisor;
	private List<Student> students;
	private Project project;
	private String groupEmail;
	private List<Student> joinRequests;
	private int maxStudents;
	private int maxRequests;

	public Group(DatabaseMap<? extends DatabaseItem> parentMap,
			DatabaseIdList parentReferences,
			Supervisor supervisor, Project project, String groupEmail, int maxStudents, int maxRequests) {
		super(parentMap, parentReferences);
		this.supervisor = supervisor;
		this.project = project;
		this.groupEmail = groupEmail;
		this.maxStudents = maxStudents;
		this.maxRequests = maxRequests;
		this.students = new ArrayList<>();
		this.joinRequests = new ArrayList<>();
	}

	public Supervisor getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(Supervisor supervisor) {
		this.supervisor = supervisor;
	}

	public List<Student> getStudents() {
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

	public List<Student> getJoinRequests() {
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