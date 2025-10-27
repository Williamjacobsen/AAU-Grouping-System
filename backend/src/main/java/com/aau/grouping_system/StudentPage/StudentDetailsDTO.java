package com.aau.grouping_system.StudentPage;

public class StudentDetailsDTO {
	private Integer id;
	private String name;
	private String email;
	private StudentQuestionnaireDTO questionnaire;
	private StudentGroupDTO group;

	public StudentDetailsDTO() {}

	public StudentDetailsDTO(Integer id, String name, String email, 
			StudentQuestionnaireDTO questionnaire, StudentGroupDTO group) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.questionnaire = questionnaire;
		this.group = group;
	}

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public StudentQuestionnaireDTO getQuestionnaire() { return questionnaire; }
	public void setQuestionnaire(StudentQuestionnaireDTO questionnaire) { this.questionnaire = questionnaire; }

	public StudentGroupDTO getGroup() { return group; }
	public void setGroup(StudentGroupDTO group) { this.group = group; }
}