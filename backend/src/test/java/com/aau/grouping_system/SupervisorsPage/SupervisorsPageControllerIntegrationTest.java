package com.aau.grouping_system.SupervisorsPage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aau.grouping_system.Config.SecurityConfig;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.SessionMember.SessionMemberService;
import com.aau.grouping_system.User.SessionMember.Supervisor.Supervisor;
import com.aau.grouping_system.Utils.RequestRequirementService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest(SupervisorsPageController.class)
@AutoConfigureWebMvc
@ComponentScan(basePackages = {
		"com.aau.grouping_system.SupervisorsPage",
		"com.aau.grouping_system.InputValidation",
		"com.aau.grouping_system.Exceptions"
})
@Import({ SupervisorsPageControllerIntegrationTest.TestConfig.class, SecurityConfig.class })
class SupervisorsPageControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RequestRequirementService requestRequirementService;

	@MockitoBean
	private SupervisorsPageService supervisorsPageService;

	@MockitoBean
	private SessionMemberService sessionMemberService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Configuration
	static class TestConfig {
	}

	// Test data
	private static final String SESSION_ID = "session123";
	private static final String SUPERVISOR_ID = "supervisor123";

	@Test
	void testGetSupervisors_ValidSession_ReturnsOk() throws Exception {
		// Arrange
		Session session = Mockito.mock(Session.class);
		Coordinator coordinator = Mockito.mock(Coordinator.class);

		List<Map<String, Object>> supervisorList = Arrays.asList(
				createSupervisorMap("1", "John Doe", "john@example.com", 3),
				createSupervisorMap("2", "Jane Smith", "jane@example.com", 5));

		Mockito.when(requestRequirementService.requireUserCoordinatorExists(any(HttpServletRequest.class)))
				.thenReturn(coordinator);
		Mockito.when(requestRequirementService.requireSessionExists(SESSION_ID))
				.thenReturn(session);
		Mockito.doNothing().when(requestRequirementService)
				.requireCoordinatorIsAuthorizedSession(SESSION_ID, coordinator);
		Mockito.when(supervisorsPageService.getFormattedSupervisors(session))
				.thenReturn(supervisorList);

		// Act & Assert
		mockMvc.perform(get("/api/sessions/{sessionId}/supervisors", SESSION_ID)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].id").value("1"))
				.andExpect(jsonPath("$[0].name").value("John Doe"))
				.andExpect(jsonPath("$[1].id").value("2"))
				.andExpect(jsonPath("$[1].name").value("Jane Smith"));
	}

	@Test
	void testGetSupervisors_UnauthorizedCoordinator_ReturnsUnauthorized() throws Exception {
		// Arrange
		Coordinator coordinator = Mockito.mock(Coordinator.class);
		Session session = Mockito.mock(Session.class);

		Mockito.when(requestRequirementService.requireUserCoordinatorExists(any(HttpServletRequest.class)))
				.thenReturn(coordinator);
		Mockito.when(requestRequirementService.requireSessionExists(SESSION_ID))
				.thenReturn(session);
		Mockito.doThrow(new RequestException(HttpStatus.UNAUTHORIZED, "Coordinator not authorized"))
				.when(requestRequirementService)
				.requireCoordinatorIsAuthorizedSession(SESSION_ID, coordinator);

		// Act & Assert
		mockMvc.perform(get("/api/sessions/{sessionId}/supervisors", SESSION_ID)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized())
				.andExpect(content().string("Coordinator not authorized"));
	}

	@Test
	void testGetSupervisors_SessionNotFound_ReturnsNotFound() throws Exception {
		// Arrange
		Coordinator coordinator = Mockito.mock(Coordinator.class);

		Mockito.when(requestRequirementService.requireUserCoordinatorExists(any(HttpServletRequest.class)))
				.thenReturn(coordinator);
		Mockito.when(requestRequirementService.requireSessionExists(SESSION_ID))
				.thenThrow(new RequestException(HttpStatus.NOT_FOUND, "Session not found"));

		// Act & Assert
		mockMvc.perform(get("/api/sessions/{sessionId}/supervisors", SESSION_ID)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().string("Session not found"));
	}

	@Test
	void testGetSupervisors_InvalidSessionId_ReturnsNotFound() throws Exception {

		// Act & Assert
		mockMvc.perform(get("/api/sessions/{sessionId}/supervisors", "session<script>alert('xss')</script>")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	void testAddSupervisor_ValidRequest_ReturnsCreated() throws Exception {
		// Arrange
		Session session = Mockito.mock(Session.class);
		Coordinator coordinator = Mockito.mock(Coordinator.class);
		SupervisorsPageService.AddSupervisorRequest addRequest = new SupervisorsPageService.AddSupervisorRequest(
				"new@supervisor.com");

		String requestJson = objectMapper.writeValueAsString(addRequest);
		String expectedResult = "Supervisor added successfully";

		Mockito.when(requestRequirementService.requireUserCoordinatorExists(any(HttpServletRequest.class)))
				.thenReturn(coordinator);
		Mockito.when(requestRequirementService.requireSessionExists(SESSION_ID))
				.thenReturn(session);
		Mockito.doNothing().when(requestRequirementService)
				.requireCoordinatorIsAuthorizedSession(SESSION_ID, coordinator);
		Mockito.when(supervisorsPageService.addSupervisor(session, addRequest))
				.thenReturn(expectedResult);

		// Act & Assert
		mockMvc.perform(post("/api/sessions/{sessionId}/supervisors", SESSION_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
				.andExpect(status().isCreated())
				.andExpect(content().string(expectedResult));
	}

	@Test
	void testAddSupervisor_InvalidEmail_ReturnsBadRequest() throws Exception {
		// Arrange
		SupervisorsPageService.AddSupervisorRequest invalidRequest = new SupervisorsPageService.AddSupervisorRequest(
				"invalid-email");

		String requestJson = objectMapper.writeValueAsString(invalidRequest);

		// Act & Assert
		mockMvc.perform(post("/api/sessions/{sessionId}/supervisors", SESSION_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testRemoveSupervisor_ValidRequest_ReturnsOk() throws Exception {
		// Arrange
		Session session = Mockito.mock(Session.class);
		Coordinator coordinator = Mockito.mock(Coordinator.class);
		Supervisor supervisor = Mockito.mock(Supervisor.class);

		Mockito.when(requestRequirementService.requireUserCoordinatorExists(any(HttpServletRequest.class)))
				.thenReturn(coordinator);
		Mockito.when(requestRequirementService.requireSessionExists(SESSION_ID))
				.thenReturn(session);
		Mockito.doNothing().when(requestRequirementService)
				.requireCoordinatorIsAuthorizedSession(SESSION_ID, coordinator);
		Mockito.when(requestRequirementService.requireSupervisorExists(SUPERVISOR_ID))
				.thenReturn(supervisor);
		Mockito.doNothing().when(requestRequirementService)
				.requireSupervisorIsAuthorizedSession(SESSION_ID, supervisor);
		Mockito.doNothing().when(supervisorsPageService).removeSupervisor(SUPERVISOR_ID);

		// Act & Assert
		mockMvc.perform(delete("/api/sessions/{sessionId}/supervisors/{supervisorId}", SESSION_ID, SUPERVISOR_ID)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("Supervisor removed successfully"));
	}

	@Test
	void testRemoveSupervisor_SupervisorNotFound_ReturnsNotFound() throws Exception {
		// Arrange
		Session session = Mockito.mock(Session.class);
		Coordinator coordinator = Mockito.mock(Coordinator.class);

		Mockito.when(requestRequirementService.requireUserCoordinatorExists(any(HttpServletRequest.class)))
				.thenReturn(coordinator);
		Mockito.when(requestRequirementService.requireSessionExists(SESSION_ID))
				.thenReturn(session);
		Mockito.doNothing().when(requestRequirementService)
				.requireCoordinatorIsAuthorizedSession(SESSION_ID, coordinator);
		Mockito.when(requestRequirementService.requireSupervisorExists(SUPERVISOR_ID))
				.thenThrow(new RequestException(HttpStatus.NOT_FOUND, "Supervisor not found"));

		// Act & Assert
		mockMvc.perform(delete("/api/sessions/{sessionId}/supervisors/{supervisorId}", SESSION_ID, SUPERVISOR_ID)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().string("Supervisor not found"));
	}

	@Test
	void testSendNewPassword_ValidRequest_ReturnsOk() throws Exception {
		// Arrange
		Session session = Mockito.mock(Session.class);
		Coordinator coordinator = Mockito.mock(Coordinator.class);
		Supervisor supervisor = Mockito.mock(Supervisor.class);

		Mockito.when(requestRequirementService.requireUserCoordinatorExists(any(HttpServletRequest.class)))
				.thenReturn(coordinator);
		Mockito.when(requestRequirementService.requireSessionExists(SESSION_ID))
				.thenReturn(session);
		Mockito.doNothing().when(requestRequirementService)
				.requireCoordinatorIsAuthorizedSession(SESSION_ID, coordinator);
		Mockito.when(requestRequirementService.requireSupervisorExists(SUPERVISOR_ID))
				.thenReturn(supervisor);
		Mockito.doNothing().when(requestRequirementService)
				.requireSupervisorIsAuthorizedSession(SESSION_ID, supervisor);
		Mockito.doNothing().when(sessionMemberService)
				.applyAndEmailNewPassword(session, supervisor);

		// Act & Assert
		mockMvc
				.perform(
						post("/api/sessions/{sessionId}/supervisors/{supervisorId}/send-new-password", SESSION_ID, SUPERVISOR_ID)
								.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("Successfully reset and emailed a new password"));
	}

	@Test
	void testUpdateSupervisorMaxGroups_ValidRequest_ReturnsOk() throws Exception {
		// Arrange
		Session session = Mockito.mock(Session.class);
		Coordinator coordinator = Mockito.mock(Coordinator.class);
		Supervisor supervisor = Mockito.mock(Supervisor.class);
		SupervisorsPageController.UpdateMaxGroupsRequest updateRequest = new SupervisorsPageController.UpdateMaxGroupsRequest(
				5);

		String requestJson = objectMapper.writeValueAsString(updateRequest);

		Mockito.when(requestRequirementService.requireUserCoordinatorExists(any(HttpServletRequest.class)))
				.thenReturn(coordinator);
		Mockito.when(requestRequirementService.requireSessionExists(SESSION_ID))
				.thenReturn(session);
		Mockito.doNothing().when(requestRequirementService)
				.requireCoordinatorIsAuthorizedSession(SESSION_ID, coordinator);
		Mockito.when(requestRequirementService.requireSupervisorExists(SUPERVISOR_ID))
				.thenReturn(supervisor);
		Mockito.doNothing().when(requestRequirementService)
				.requireSupervisorIsAuthorizedSession(SESSION_ID, supervisor);

		// Act & Assert
		mockMvc.perform(post("/api/sessions/{sessionId}/supervisors/{supervisorId}/max-groups", SESSION_ID, SUPERVISOR_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
				.andExpect(status().isOk())
				.andExpect(content().string("Supervisor max groups updated successfully"));
	}

	@Test
	void testUpdateSupervisorMaxGroups_InvalidMaxGroupsTooLow_ReturnsBadRequest() throws Exception {
		// Arrange
		SupervisorsPageController.UpdateMaxGroupsRequest invalidRequest = new SupervisorsPageController.UpdateMaxGroupsRequest(
				0); // Below minimum of 1

		String requestJson = objectMapper.writeValueAsString(invalidRequest);

		// Act & Assert
		mockMvc.perform(post("/api/sessions/{sessionId}/supervisors/{supervisorId}/max-groups", SESSION_ID, SUPERVISOR_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testUpdateSupervisorMaxGroups_InvalidMaxGroupsTooHigh_ReturnsBadRequest() throws Exception {
		// Arrange
		SupervisorsPageController.UpdateMaxGroupsRequest invalidRequest = new SupervisorsPageController.UpdateMaxGroupsRequest(
				101); // Above maximum of 100

		String requestJson = objectMapper.writeValueAsString(invalidRequest);

		// Act & Assert
		mockMvc.perform(post("/api/sessions/{sessionId}/supervisors/{supervisorId}/max-groups", SESSION_ID, SUPERVISOR_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson))
				.andExpect(status().isBadRequest());
	}

	// Helper method to create supervisor map
	private Map<String, Object> createSupervisorMap(String id, String name, String email, int maxGroups) {
		Map<String, Object> supervisor = new HashMap<>();
		supervisor.put("id", id);
		supervisor.put("name", name);
		supervisor.put("email", email);
		supervisor.put("maxGroups", maxGroups);
		return supervisor;
	}
}