package com.aau.grouping_system.StudentPage;

public class StudentGroupDTO {
	private String id;
	private Boolean hasGroup;
	private String project;
	private Integer groupSize;
	private Integer maxSize;

	public StudentGroupDTO() {}

	public StudentGroupDTO(String id, Boolean hasGroup, String project, Integer groupSize, Integer maxSize) {
		this.id = id;
		this.hasGroup = hasGroup;
		this.project = project;
		this.groupSize = groupSize;
		this.maxSize = maxSize;
	}

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }

	public Boolean getHasGroup() { return hasGroup; }
	public void setHasGroup(Boolean hasGroup) { this.hasGroup = hasGroup; }

	public String getProject() { return project; }
	public void setProject(String project) { this.project = project; }

	public Integer getGroupSize() { return groupSize; }
	public void setGroupSize(Integer groupSize) { this.groupSize = groupSize; }

	public Integer getMaxSize() { return maxSize; }
	public void setMaxSize(Integer maxSize) { this.maxSize = maxSize; }
}