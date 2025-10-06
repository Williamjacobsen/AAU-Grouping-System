package com.aau.grouping_system.Group;

import com.aau.grouping_system.EnhancedMap.EnhancedMappable;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;

public class Group extends EnhancedMappable {

	// todo: Use lists instead of arrays.

	private Supervisor supervisor;
	private Student[] students;
	private Project project;
	private String groupEmail;
	private Student[] joinRequests;

	public Group(Supervisor supervisor, Project project, String groupEmail, int maxStudents, int maxRequests) {
		this.supervisor = supervisor;
		this.project = project;
		this.groupEmail = groupEmail;
		this.students = new Student[maxStudents];
		this.joinRequests = new Student[maxRequests];
	}

	public void joinGroup(Student student) {
		for (Student s : students) {
			if (s != null && s.equals(student)) {
				throw new IllegalStateException("Student is already in the group");
			}
		}

		for (int i = 0; i < students.length; i++) {
			if (students[i] == null) {
				students[i] = student;
				return;
			}
		}
	}

	public void leaveGroup(Student student) {
		for (int i = 0; i < students.length; i++) {
			if (students[i] != null && students[i].equals(student)) {
				students[i] = null;
				return;
			}
		}
	}

	public void requestToJoin(Student student) {
		if (student == null) {
			throw new IllegalArgumentException("Student cannot be null");
		}

		for (Student s : joinRequests) {
			if (s != null && s.equals(student)) {
				throw new IllegalStateException("Student already has a pending request");
			}
		}

		for (int i = 0; i < joinRequests.length; i++) {
			if (joinRequests[i] == null) {
				joinRequests[i] = student;
				System.out.println(student.getName() + " requested to join.");
				return;
			}
		}
	}

	public void acceptJoinRequest(Student student) {
		for (int i = 0; i < joinRequests.length; i++) {
			if (joinRequests[i] != null && joinRequests[i].equals(student)) {
				joinRequests[i] = null;
				joinGroup(student);
				return;
			}
		}
	}

	public Supervisor getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(Supervisor supervisor) {
		this.supervisor = supervisor;
	}

	public Student[] getStudents() {
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

	public Student[] getJoinRequests() {
		return joinRequests;
	}
}
