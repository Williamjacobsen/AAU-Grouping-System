
package com.aau.grouping_system.StudentPage;

import java.util.List;

public class StudentQuestionnaireDTO {
	private String projectPriority1;
	private String projectPriority2;
	private Object previousSessionTeammates;
	private Object desiredGroupMembers;
	private Object desiredGroupSize;
	private String workingEnvironment;
	private String specialNeeds;
	private String otherComments;
	private List<String> personalSkills;
	private List<String> academicInterests;

	public StudentQuestionnaireDTO() {}

	public StudentQuestionnaireDTO(String projectPriority1, String projectPriority2, 
			Object previousSessionTeammates, Object desiredGroupMembers, Object desiredGroupSize,
			String workingEnvironment, String specialNeeds, String otherComments,
			List<String> personalSkills, List<String> academicInterests) {
		this.projectPriority1 = projectPriority1;
		this.projectPriority2 = projectPriority2;
		this.previousSessionTeammates = previousSessionTeammates;
		this.desiredGroupMembers = desiredGroupMembers;
		this.desiredGroupSize = desiredGroupSize;
		this.workingEnvironment = workingEnvironment;
		this.specialNeeds = specialNeeds;
		this.otherComments = otherComments;
		this.personalSkills = personalSkills;
		this.academicInterests = academicInterests;
	}

	public String getProjectPriority1() { return projectPriority1; }
	public void setProjectPriority1(String projectPriority1) { this.projectPriority1 = projectPriority1; }

	public String getProjectPriority2() { return projectPriority2; }
	public void setProjectPriority2(String projectPriority2) { this.projectPriority2 = projectPriority2; }

	public Object getPreviousSessionTeammates() { return previousSessionTeammates; }
	public void setPreviousSessionTeammates(Object previousSessionTeammates) { this.previousSessionTeammates = previousSessionTeammates; }

	public Object getDesiredGroupMembers() { return desiredGroupMembers; }
	public void setDesiredGroupMembers(Object desiredGroupMembers) { this.desiredGroupMembers = desiredGroupMembers; }

	public Object getDesiredGroupSize() { return desiredGroupSize; }
	public void setDesiredGroupSize(Object desiredGroupSize) { this.desiredGroupSize = desiredGroupSize; }

	public String getWorkingEnvironment() { return workingEnvironment; }
	public void setWorkingEnvironment(String workingEnvironment) { this.workingEnvironment = workingEnvironment; }

	public String getSpecialNeeds() { return specialNeeds; }
	public void setSpecialNeeds(String specialNeeds) { this.specialNeeds = specialNeeds; }

	public String getOtherComments() { return otherComments; }
	public void setOtherComments(String otherComments) { this.otherComments = otherComments; }

	public List<String> getPersonalSkills() { return personalSkills; }
	public void setPersonalSkills(List<String> personalSkills) { this.personalSkills = personalSkills; }

	public List<String> getAcademicInterests() { return academicInterests; }
	public void setAcademicInterests(List<String> academicInterests) { this.academicInterests = academicInterests; }
}