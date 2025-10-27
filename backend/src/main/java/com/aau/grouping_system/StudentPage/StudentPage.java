package com.aau.grouping_system.StudentPage;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.StudentQuestionnaire.StudentQuestionnaire;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/session/{sessionId}/student")
public class StudentPage {

	private final Database db;

	public StudentPage(Database db) {
		this.db = db;
	}

	private Coordinator getCurrentCoordinator(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		return (Coordinator) session.getAttribute("user");
	}

	@GetMapping("/{studentId}")
	public ResponseEntity<StudentDetailsDTO> getStudentDetails(
			@PathVariable Integer sessionId,
			@PathVariable Integer studentId, 
			HttpServletRequest request) {
		Coordinator coordinator = getCurrentCoordinator(request);
		if (coordinator == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Student student = db.getStudents().getItem(studentId);
		if (student == null) {
			return ResponseEntity.notFound().build();
		}

		if (student.getSession().getId() != sessionId) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		if (!student.getSession().getCoordinator().equals(coordinator)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		StudentQuestionnaireDTO questionnaireDTO = buildQuestionnaireDTO(student, true);
		StudentGroupDTO groupDTO = buildGroupDTO(student);
		
		StudentDetailsDTO studentData = new StudentDetailsDTO(
			student.getId(),
			student.getName(),
			student.getEmail(),
			questionnaireDTO,
			groupDTO
		);

		return ResponseEntity.ok(studentData);
	}

	@GetMapping("/{studentId}/public")
	public ResponseEntity<StudentDetailsDTO> getStudentPublicDetails(
			@PathVariable Integer sessionId,
			@PathVariable Integer studentId) {
		Student student = db.getStudents().getItem(studentId);
		if (student == null) {
			return ResponseEntity.notFound().build();
		}

		if (student.getSession().getId() != sessionId) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		StudentQuestionnaireDTO questionnaireDTO = buildQuestionnaireDTO(student, false);
		StudentGroupDTO groupDTO = buildGroupDTO(student);
		
		StudentDetailsDTO studentData = new StudentDetailsDTO(
			student.getId(),
			student.getName(),
			null,
			questionnaireDTO,
			groupDTO
		);

		return ResponseEntity.ok(studentData);
	}

	private StudentQuestionnaireDTO buildQuestionnaireDTO(Student student, boolean includePrivate) {
		if (student.getQuestionnaire() == null) {
			return new StudentQuestionnaireDTO(
				"Not specified",
				"Not specified",
				"Not specified",
				"Not specified",
				"Not specified",
				"Not specified",
				"Not specified",
				"Student has not filled out questionnaire yet.",
				null,
				null
			);
		}

		StudentQuestionnaire q = student.getQuestionnaire();
		return new StudentQuestionnaireDTO(
			getProjectNameById(q.getProjectPriorities()),
			"Not specified",
			q.getPreviousSessionTeammates(),
			q.getDesiredGroupMembers(),
			q.getDesiredGroupSize(),
			q.getWorkingEnvironment(),
			q.getSpecialNeeds(),
			q.getOtherComments(),
			q.getPersonalSkills(),
			q.getAcademicInterests()
		);
	}

	private StudentGroupDTO buildGroupDTO(Student student) {
		for (Group group : db.getGroups().getAllItems().values()) {
			if (group.getStudents().contains(student)) {
				String projectName = "No project assigned";
				if (group.getProject() != null) {
					projectName = group.getProject().getProjectName();
				}
				
				return new StudentGroupDTO(
					group.getId(),
					true,
					projectName,
					group.getStudents().size(),
					group.getMaxStudents()
				);
			}
		}
		
		return new StudentGroupDTO(
			null,
			false,
			"Not in a group",
			0,
			0
		);
	}

	private String getProjectNameById(int projectId) {
		for (Project project : db.getProjects().getAllItems().values()) {
			if (project.getProjectId() == projectId) {
				return project.getProjectName();
			}
		}
		return "Project not found (ID: " + projectId + ")";
	}

	@DeleteMapping("/{studentId}")
	public ResponseEntity<String> removeStudent(
			@PathVariable Integer sessionId,
			@PathVariable Integer studentId,
			HttpServletRequest request) {
		Coordinator coordinator = getCurrentCoordinator(request);
		if (coordinator == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Student student = db.getStudents().getItem(studentId);
		if (student == null) {
			return ResponseEntity.notFound().build();
		}

		// Validate student belongs to session
		if (student.getSession().getId() != sessionId) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		// Validate coordinator session
		if (!student.getSession().getCoordinator().equals(coordinator)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		try {
			// Remove student from group
			for (Group group : db.getGroups().getAllItems().values()) {
				if (group.getStudents().contains(student)) {
					group.getStudents().remove(student);
					break;
				}
			}

			// Remove student from database
			student.removeChildren();
			db.getStudents().remove(student);

			return ResponseEntity.ok("Student removed successfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to remove student: " + e.getMessage());
		}
	}
}
