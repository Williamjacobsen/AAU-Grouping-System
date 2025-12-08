package com.aau.grouping_system.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aau.grouping_system.Config.SecurityConfig;
import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Coordinator.CoordinatorService;
import com.aau.grouping_system.User.SessionMember.Student.Student;
import com.aau.grouping_system.Utils.RequestRequirementService;

import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest(UserController.class)
@AutoConfigureWebMvc
@ComponentScan(basePackages = { "com.aau.grouping_system.User", "com.aau.grouping_system.Exceptions" })
@Import({ UserControllerIntegrationTest.TestConfig.class, SecurityConfig.class })
class UserControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RequestRequirementService requestRequirementService;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private Database database;

	@MockitoBean
	private PasswordEncoder passwordEncoder;

	@MockitoBean
	private CoordinatorService coordinatorService;

	@MockitoBean
	private com.aau.grouping_system.EmailSystem.EmailService emailService;

	@MockitoBean
	private com.aau.grouping_system.User.SessionMember.SessionMemberService sessionMemberService;

	private Student testStudent;
	private Coordinator testCoordinator;

	@BeforeEach
	void setUp() {
		testStudent = mock(Student.class);
		testCoordinator = mock(Coordinator.class);

		when(testStudent.getEmail()).thenReturn("student@test.com");
		when(testStudent.getId()).thenReturn("student-123");
		when(testStudent.getName()).thenReturn("John Doe");

		when(testCoordinator.getEmail()).thenReturn("coordinator@test.com");
		when(testCoordinator.getId()).thenReturn("coordinator-123");
		when(testCoordinator.getName()).thenReturn("Jane Smith");
	}

	@Test
	void testModifyEmail_ValidEmail_ReturnsSuccess() throws Exception {
		// Arrange
		String requestBody = """
				{
				    "newEmail": "newemail@student.aau.dk"
				}
				""";

		when(requestRequirementService.requireUserExists(any(HttpServletRequest.class)))
				.thenReturn(testStudent);

		// Act & Assert
		mockMvc.perform(post("/api/user/modifyEmail")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(content().string("Email has been changed."));

		verify(requestRequirementService).requireUserExists(any(HttpServletRequest.class));
		verify(requestRequirementService).requireEmailNotDuplicate("newemail@student.aau.dk", testStudent);
		verify(userService).modifyEmail("newemail@student.aau.dk", testStudent);
	}

	@Test
	void testModifyEmail_DuplicateEmail_ReturnsConflict() throws Exception {
		// Arrange
		String requestBody = """
				{
				    "newEmail": "existing@student.aau.dk"
				}
				""";

		when(requestRequirementService.requireUserExists(any(HttpServletRequest.class)))
				.thenReturn(testStudent);
		doThrow(new RequestException(HttpStatus.CONFLICT, "Email already exists"))
				.when(requestRequirementService).requireEmailNotDuplicate("existing@student.aau.dk", testStudent);

		// Act & Assert
		mockMvc.perform(post("/api/user/modifyEmail")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isConflict());

		verify(requestRequirementService).requireUserExists(any(HttpServletRequest.class));
		verify(requestRequirementService).requireEmailNotDuplicate("existing@student.aau.dk", testStudent);
		verify(userService, never()).modifyEmail(any(), any());
	}

	@Test
	void testModifyEmail_InvalidEmail_ReturnsBadRequest() throws Exception {
		// Arrange
		String requestBody = """
				{
				    "newEmail": "invalid-email-format"
				}
				""";

		// Act & Assert
		mockMvc.perform(post("/api/user/modifyEmail")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isBadRequest());

		verify(requestRequirementService, never()).requireUserExists(any());
		verify(userService, never()).modifyEmail(any(), any());
	}

	@Test
	void testModifyEmail_EmptyEmail_ReturnsBadRequest() throws Exception {
		// Arrange
		String requestBody = """
				{
				    "newEmail": ""
				}
				""";

		// Act & Assert
		mockMvc.perform(post("/api/user/modifyEmail")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isBadRequest());

		verify(requestRequirementService, never()).requireUserExists(any());
		verify(userService, never()).modifyEmail(any(), any());
	}

	@Test
	void testModifyEmail_EmailWithWhitespace_ReturnsBadRequest() throws Exception {
		// Arrange
		String requestBody = """
				{
				    "newEmail": "test @student.aau.dk"
				}
				""";

		// Act & Assert
		mockMvc.perform(post("/api/user/modifyEmail")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isBadRequest());

		verify(requestRequirementService, never()).requireUserExists(any());
		verify(userService, never()).modifyEmail(any(), any());
	}

	@Test
	void testModifyEmail_UserNotFound_ReturnsNotFound() throws Exception {
		// Arrange
		String requestBody = """
				{
				    "newEmail": "valid@student.aau.dk"
				}
				""";

		when(requestRequirementService.requireUserExists(any(HttpServletRequest.class)))
				.thenThrow(new RequestException(HttpStatus.NOT_FOUND, "User not found"));

		// Act & Assert
		mockMvc.perform(post("/api/user/modifyEmail")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isNotFound());

		verify(requestRequirementService).requireUserExists(any(HttpServletRequest.class));
		verify(userService, never()).modifyEmail(any(), any());
	}

	@Test
	void testModifyName_ValidName_ReturnsSuccess() throws Exception {
		// Arrange
		String requestBody = """
				{
				    "newName": "John Updated Doe"
				}
				""";

		when(requestRequirementService.requireUserExists(any(HttpServletRequest.class)))
				.thenReturn(testStudent);
		when(userService.isNameDuplicate("John Updated Doe", testStudent))
				.thenReturn(false);

		// Act & Assert
		mockMvc.perform(post("/api/user/modifyName")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(content().string("Password has been changed."));

		verify(requestRequirementService).requireUserExists(any(HttpServletRequest.class));
		verify(userService).isNameDuplicate("John Updated Doe", testStudent);
		verify(userService).modifyName("John Updated Doe", testStudent);
	}

	@Test
	void testModifyName_DuplicateName_ReturnsConflict() throws Exception {
		// Arrange
		String requestBody = """
				{
				    "newName": "Existing User Name"
				}
				""";

		when(requestRequirementService.requireUserExists(any(HttpServletRequest.class)))
				.thenReturn(testStudent);
		when(userService.isNameDuplicate("Existing User Name", testStudent))
				.thenReturn(true);

		// Act & Assert
		mockMvc.perform(post("/api/user/modifyName")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isConflict());

		verify(requestRequirementService).requireUserExists(any(HttpServletRequest.class));
		verify(userService).isNameDuplicate("Existing User Name", testStudent);
		verify(userService, never()).modifyName(any(), any());
	}

	@Test
	void testModifyName_EmptyName_ReturnsBadRequest() throws Exception {
		// Arrange
		String requestBody = """
				{
				    "newName": ""
				}
				""";

		// Act & Assert
		mockMvc.perform(post("/api/user/modifyName")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isBadRequest());

		verify(requestRequirementService, never()).requireUserExists(any());
		verify(userService, never()).modifyName(any(), any());
	}

	@Test
	void testModifyName_NameWithDangerousCharacters_ReturnsBadRequest() throws Exception {
		// Arrange
		String requestBody = """
				{
				    "newName": "John<script>alert('xss')</script>Doe"
				}
				""";

		// Act & Assert
		mockMvc.perform(post("/api/user/modifyName")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isBadRequest());

		verify(requestRequirementService, never()).requireUserExists(any());
		verify(userService, never()).modifyName(any(), any());
	}

	@Test
	void testModifyName_CoordinatorUser_ReturnsSuccess() throws Exception {
		// Arrange
		String requestBody = """
				{
				    "newName": "Jane Updated Smith"
				}
				""";

		when(requestRequirementService.requireUserExists(any(HttpServletRequest.class)))
				.thenReturn(testCoordinator);
		when(userService.isNameDuplicate("Jane Updated Smith", testCoordinator))
				.thenReturn(false);

		// Act & Assert
		mockMvc.perform(post("/api/user/modifyName")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(content().string("Password has been changed."));

		verify(requestRequirementService).requireUserExists(any(HttpServletRequest.class));
		verify(userService).isNameDuplicate("Jane Updated Smith", testCoordinator);
		verify(userService).modifyName("Jane Updated Smith", testCoordinator);
	}

	@Configuration
	static class TestConfig {
	}
}