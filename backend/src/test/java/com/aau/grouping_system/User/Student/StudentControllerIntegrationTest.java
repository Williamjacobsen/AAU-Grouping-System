package com.aau.grouping_system.User.Student;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aau.grouping_system.Authentication.AuthService;
import com.aau.grouping_system.Config.SecurityConfig;
import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.Session.SessionService;
import com.aau.grouping_system.User.SessionMember.Student.Student;
import com.aau.grouping_system.User.SessionMember.Student.StudentController;
import com.aau.grouping_system.User.SessionMember.Student.StudentService;
import com.aau.grouping_system.Utils.RequestRequirementService;

import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest({ StudentController.class, com.aau.grouping_system.Session.SessionSetupController.class })
@AutoConfigureWebMvc
@ComponentScan(basePackages = { "com.aau.grouping_system.User.SessionMember.Student", "com.aau.grouping_system.Session",
		"com.aau.grouping_system.Config",
		"com.aau.grouping_system.Exceptions" })
@Import({ StudentControllerIntegrationTest.TestConfig.class, SecurityConfig.class })
class StudentControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private StudentService studentService;

	@MockitoBean
	private AuthService authService;

	@MockitoBean
	private SessionService sessionService;

	@MockitoBean
	private Database database;

	@MockitoBean
	private RequestRequirementService requirementService;

	@MockitoBean
	private com.aau.grouping_system.User.SessionMember.SessionMemberService sessionMemberService;

	@MockitoBean
	private com.aau.grouping_system.Session.SessionSetupService sessionSetupService;

	private Student testStudent;
	private Session testSession;

	@BeforeEach
	void setUp() {
		testStudent = mock(Student.class);
		testSession = mock(Session.class);

		when(testStudent.getEmail()).thenReturn("student@test.com");
		when(testStudent.getId()).thenReturn("student-123");
		when(testStudent.getSessionId()).thenReturn("session-123");
		when(testSession.getId()).thenReturn("session-123");
	}

	@Test
	void testCreateStudentsViaSessionSetup_ValidRequest_ReturnsSuccess() throws Exception {
		// Arrange
		String requestBody = """
				{
				    "name": "Test Session",
				    "minGroupSize": 5,
				    "maxGroupSize": 7,
				    "allowStudentProjectProposals": true,
				    "questionnaireDeadlineISODateString": "2025-12-31T23:59:59Z",
				    "supervisorEmailAndNamePairs": "supervisor1@test.com Supervisor One",
				    "studentEmailAndNamePairs": "john.doe@student.aau.dk John Doe"
				}
				""";

		when(requirementService.requireSessionExists("session-123")).thenReturn(testSession);
		when(requirementService.requireUserCoordinatorExists(any()))
				.thenReturn(mock(com.aau.grouping_system.User.Coordinator.Coordinator.class));

		// Act & Assert
		mockMvc.perform(post("/api/sessionSetup/session-123/saveSetup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(content().string("Session setup saved successfully!"));

		verify(requirementService).requireSessionExists("session-123");
	}

	@Test
	void testSessionSetup_MissingRequiredFields_ReturnsBadRequest() throws Exception {
		// Arrange
		String requestBody = """
				{
				    "minGroupSize": 5,
				    "maxGroupSize": 7,
				    "allowStudentProjectProposals": true,
				    "questionnaireDeadlineISODateString": "2025-12-31T23:59:59Z",
				    "supervisorEmails": "supervisor1@test.com",
				    "studentEmails": "john.doe@student.aau.dk"
				}
				""";

		when(requirementService.requireSessionExists("session-123")).thenReturn(testSession);
		when(requirementService.requireUserCoordinatorExists(any()))
				.thenReturn(mock(com.aau.grouping_system.User.Coordinator.Coordinator.class));

		// Act & Assert
		mockMvc.perform(post("/api/sessionSetup/session-123/saveSetup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testSessionSetup_SessionNotFound_ReturnsNotFound() throws Exception {
		// Arrange
		String requestBody = """
				{
				    "name": "Test Session",
				    "minGroupSize": 5,
				    "maxGroupSize": 7,
				    "allowStudentProjectProposals": true,
				    "questionnaireDeadlineISODateString": "2025-12-31T23:59:59Z",
				    "supervisorEmailAndNamePairs": "supervisor1@test.com Supervisor One",
				    "studentEmailAndNamePairs": "john.doe@student.aau.dk John Doe"
				}
				""";

		when(requirementService.requireSessionExists("nonexistent-session"))
				.thenThrow(new RequestException(HttpStatus.NOT_FOUND, "Session not found"));

		// Act & Assert
		mockMvc.perform(post("/api/sessionSetup/nonexistent-session/saveSetup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isNotFound());

		verify(requirementService).requireSessionExists("nonexistent-session");
	}

	@Test
	void testSaveQuestionnaireAnswers_ValidStudentAndSession_ReturnsSuccess() throws Exception {
		// Arrange
		String requestBody = """
				{
				    "desiredProjectId1": "project-1",
				    "desiredProjectId2": "project-2",
				    "desiredProjectId3": "project-3",
				    "desiredGroupSizeMin": 5,
				    "desiredGroupSizeMax": 7,
				    "desiredWorkLocation": "NoPreference",
				    "desiredWorkStyle": "NoPreference",
				    "personalSkills": "Java, Spring Boot",
				    "specialNeeds": "None",
				    "academicInterests": "Software Engineering",
				    "comments": "Looking forward to working on this project"
				}
				""";

		when(requirementService.requireUserStudentExists(any(HttpServletRequest.class)))
				.thenReturn(testStudent);
		when(requirementService.requireSessionExists("session-123"))
				.thenReturn(testSession);

		// Act & Assert
		mockMvc.perform(post("/api/student/saveQuestionnaireAnswers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(content().string("Saved questionnaire answers successfully."));

		verify(requirementService).requireUserStudentExists(any(HttpServletRequest.class));
		verify(requirementService).requireSessionExists("session-123");
		verify(requirementService).requireQuestionnaireDeadlineNotExceeded(testSession);
		verify(studentService).applyQuestionnaireAnswers(eq(testStudent), any());
	}

	@Test
	void testSaveQuestionnaireAnswers_StudentNotFound_ReturnsNotFound() throws Exception {
		// Arrange
		String requestBody = """
				{
				    "desiredProjectId1": "project-1",
				    "desiredProjectId2": "project-2",
				    "desiredProjectId3": "project-3",
				    "desiredGroupSizeMin": 5,
				    "desiredGroupSizeMax": 7,
				    "desiredWorkLocation": "NoPreference",
				    "desiredWorkStyle": "NoPreference",
				    "personalSkills": "Java, Spring Boot",
				    "specialNeeds": "None",
				    "academicInterests": "Software Engineering",
				    "comments": "Looking forward to working on this project"
				}
				""";

		when(requirementService.requireUserStudentExists(any(HttpServletRequest.class)))
				.thenThrow(new RequestException(HttpStatus.BAD_REQUEST, "User not authorized as a valid student"));

		// Act & Assert
		mockMvc.perform(post("/api/student/saveQuestionnaireAnswers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isBadRequest());

		verify(requirementService).requireUserStudentExists(any(HttpServletRequest.class));
		verify(studentService, never()).applyQuestionnaireAnswers(any(), any());
	}

	@Test
	void testSaveQuestionnaireAnswers_DeadlineExceeded_ReturnsUnauthorized() throws Exception {
		// Arrange
		String requestBody = """
				{
				    "desiredProjectId1": "project-1",
				    "desiredProjectId2": "project-2",
				    "desiredProjectId3": "project-3",
				    "desiredGroupSizeMin": 5,
				    "desiredGroupSizeMax": 7,
				    "desiredWorkLocation": "NoPreference",
				    "desiredWorkStyle": "NoPreference",
				    "personalSkills": "Java, Spring Boot",
				    "specialNeeds": "None",
				    "academicInterests": "Software Engineering",
				    "comments": "Looking forward to working on this project"
				}
				""";

		when(requirementService.requireUserStudentExists(any(HttpServletRequest.class)))
				.thenReturn(testStudent);
		when(requirementService.requireSessionExists("session-123"))
				.thenReturn(testSession);
		doThrow(new RequestException(HttpStatus.UNAUTHORIZED, "Questionnaire submission deadline exceeded."))
				.when(requirementService).requireQuestionnaireDeadlineNotExceeded(testSession);

		// Act & Assert
		mockMvc.perform(post("/api/student/saveQuestionnaireAnswers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isUnauthorized());

		verify(requirementService).requireUserStudentExists(any(HttpServletRequest.class));
		verify(requirementService).requireSessionExists("session-123");
		verify(requirementService).requireQuestionnaireDeadlineNotExceeded(testSession);
		verify(studentService, never()).applyQuestionnaireAnswers(any(), any());
	}

	@Configuration
	static class TestConfig {
	}
}