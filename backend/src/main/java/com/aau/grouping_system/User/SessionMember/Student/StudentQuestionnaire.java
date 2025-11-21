package com.aau.grouping_system.User.SessionMember.Student;

public class StudentQuestionnaire {

	public enum WorkLocation {
		NoPreference,
		Located,
		Remote;
	}

	public enum WorkStyle {
		NoPreference,
		Solo,
		Together;
	}

	private String desiredProjectId1 = "";
	private String desiredProjectId2 = "";
	private String desiredProjectId3 = "";
	/// -1 means no preference
	private Integer desiredGroupSizeMin = -1;
	/// -1 means no preference
	private Integer desiredGroupSizeMax = -1;
	private WorkLocation desiredWorkLocation = WorkLocation.NoPreference;
	private WorkStyle desiredWorkStyle = WorkStyle.NoPreference;
	private String personalSkills = "";
	private String specialNeeds = "";
	private String academicInterests = "";
	private String comments = "";

	public StudentQuestionnaire() {
	}

	public StudentQuestionnaire(
			String desiredProjectId1,
			String desiredProjectId2,
			String desiredProjectId3,
			Integer desiredGroupSizeMin,
			Integer desiredGroupSizeMax,
			WorkLocation desiredWorkLocation,
			WorkStyle desiredWorkStyle,
			String personalSkills,
			String specialNeeds,
			String academicInterests,
			String comments) {
		this.desiredProjectId1 = desiredProjectId1;
		this.desiredProjectId2 = desiredProjectId2;
		this.desiredProjectId3 = desiredProjectId3;
		this.desiredGroupSizeMin = desiredGroupSizeMin;
		this.desiredGroupSizeMax = desiredGroupSizeMax;
		this.desiredWorkLocation = desiredWorkLocation;
		this.desiredWorkStyle = desiredWorkStyle;
		this.personalSkills = personalSkills;
		this.specialNeeds = specialNeeds;
		this.academicInterests = academicInterests;
		this.comments = comments;
	}

	// @formatter:off
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
	public WorkLocation getDesiredWorkLocation() { return desiredWorkLocation; }
	public void setDesiredWorkLocation(WorkLocation desiredWorkLocation) { this.desiredWorkLocation = desiredWorkLocation; }
	public WorkStyle getDesiredWorkStyle() { return desiredWorkStyle; }
	public void setDesiredWorkStyle(WorkStyle desiredWorkStyle) { this.desiredWorkStyle = desiredWorkStyle; }
	public String getPersonalSkills() { return personalSkills; }
	public void setPersonalSkills(String personalSkills) { this.personalSkills = personalSkills; }
	public String getSpecialNeeds() { return specialNeeds; }
	public void setSpecialNeeds(String specialNeeds) { this.specialNeeds = specialNeeds; }
	public String getAcademicInterests() { return academicInterests; }
	public void setAcademicInterests(String academicInterests) { this.academicInterests = academicInterests; }
	public String getComments() { return comments; }
	public void setComments(String comments) { this.comments = comments; }

}
