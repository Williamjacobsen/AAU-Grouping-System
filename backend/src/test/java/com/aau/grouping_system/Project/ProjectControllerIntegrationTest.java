package com.aau.grouping_system.Project;

import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseItemChildGroup;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.Utils.RequestRequirementService;

import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest(ProjectController.class)
@AutoConfigureWebMvc
@ComponentScan(basePackages = {
		"com.aau.grouping_system.Project",
		"com.aau.grouping_system.Exceptions",
		"com.aau.grouping_system.Utils"
})
@Import({ ProjectControllerIntegrationTest.TestConfig.class })
class ProjectControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private Database database;

	@MockitoBean
	private RequestRequirementService requestRequirementService;

	private Session mockSession;
	private Project mockProject;
	private User mockUser;
	private DatabaseItemChildGroup mockProjectGroup;
	private DatabaseMap<Session> mockSessionMap;
	private DatabaseMap<Project> mockProjectMap;

	@BeforeEach
	@SuppressWarnings("unchecked")
	void setUp() {
		// Create mock objects
		mockSession = mock(Session.class);
		mockProject = mock(Project.class);
		mockUser = mock(User.class);
		mockProjectGroup = mock(DatabaseItemChildGroup.class);
		mockSessionMap = mock(DatabaseMap.class);
		mockProjectMap = mock(DatabaseMap.class);

		// Setup database mock
		when(database.getSessions()).thenReturn(mockSessionMap);
		when(database.getProjects()).thenReturn(mockProjectMap);

		// Setup session mock
		when(mockSession.getProjects()).thenReturn(mockProjectGroup);
		when(mockSession.getAllowStudentProjectProposals()).thenReturn(true);

		// Setup user mock
		when(mockUser.getId()).thenReturn("user123");
		when(mockUser.getRole()).thenReturn(User.Role.Student);

		// Setup project mock
		when(mockProject.getId()).thenReturn("project123");
		when(mockProject.getCreatorUserId()).thenReturn("user123");
	}

	@Test
	void testGetSessionsProjects_ValidSession_ReturnsProjects() throws Exception {
		// Arrange
		String sessionId = "session123";
		CopyOnWriteArrayList<Project> projects = new CopyOnWriteArrayList<>();
		projects.add(mockProject);

		CopyOnWriteArrayList<String> projectIds = new CopyOnWriteArrayList<>();
		projectIds.add("project123");

		when(mockSessionMap.getItem(sessionId)).thenReturn(mockSession);
		when(mockProjectGroup.getIds()).thenReturn(projectIds);
		when(mockProjectMap.getItems(projectIds)).thenReturn(projects);

		// Act & Assert
		mockMvc.perform(get("/api/project/sessions/{sessionId}/getProjects", sessionId)
				.sessionAttr("user", mockUser))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(1));

		verify(mockSessionMap).getItem(sessionId);
		verify(mockProjectGroup).getIds();
		verify(mockProjectMap).getItems(projectIds);
	}

	@Test
	void testGetSessionsProjects_SessionNotFound_ReturnsBadRequest() throws Exception {
		// Arrange
		String sessionId = "nonexistent";
		when(mockSessionMap.getItem(sessionId)).thenReturn(null);

		// Act & Assert
		mockMvc.perform(get("/api/project/sessions/{sessionId}/getProjects", sessionId))
				.andExpect(status().isBadRequest());

		verify(mockSessionMap).getItem(sessionId);
		verify(mockProjectGroup, never()).getIds();
	}

	@Test
	void testCreateProject_ValidRequest_ReturnsSuccess() throws Exception {
		// Arrange
		String sessionId = "session123";
		String projectName = "TestProject";
		String description = "TestDescription";

		// Create empty project list and ID list
		CopyOnWriteArrayList<Project> emptyProjects = new CopyOnWriteArrayList<>();
		CopyOnWriteArrayList<String> emptyIds = new CopyOnWriteArrayList<>();

		when(requestRequirementService.requireUserExists(any(HttpServletRequest.class))).thenReturn(mockUser);
		when(requestRequirementService.requireSessionExists(sessionId)).thenReturn(mockSession);
		when(mockProjectGroup.getIds()).thenReturn(emptyIds);
		when(mockProjectMap.getItems(emptyIds)).thenReturn(emptyProjects);
		when(mockProjectMap.addItem(eq(mockProjectGroup), any(Project.class))).thenReturn(mockProject);

		// Act & Assert
		mockMvc.perform(post("/api/project/create/{sessionId}/{projectName}/{description}",
				sessionId, projectName, description))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Project '" + projectName + "' has been created successfully."))
				.andExpect(jsonPath("$.id").value("project123"));

		verify(requestRequirementService).requireUserExists(any(HttpServletRequest.class));
		verify(requestRequirementService).requireSessionExists(sessionId);
		verify(mockProjectGroup, times(2)).getIds();
		verify(mockProjectMap, times(2)).getItems(emptyIds);
		verify(mockProjectMap).addItem(eq(mockProjectGroup), any(Project.class));
	}

	@Test
	void testDeleteProject_ValidRequest_ReturnsSuccess() throws Exception {
		// Arrange
		String projectId = "project123";
		String sessionId = "session123";

		when(requestRequirementService.requireProjectExists(projectId)).thenReturn(mockProject);
		when(requestRequirementService.requireSessionExists(sessionId)).thenReturn(mockSession);
		when(requestRequirementService.requireUserExists(any(HttpServletRequest.class))).thenReturn(mockUser);

		// Act & Assert
		mockMvc.perform(delete("/api/project/delete/{projectId}/{sessionId}", projectId, sessionId))
				.andExpect(status().isOk())
				.andExpect(content().string("Project with id " + projectId + " has been deleted successfully."));

		verify(requestRequirementService).requireProjectExists(projectId);
		verify(requestRequirementService).requireSessionExists(sessionId);
		verify(requestRequirementService).requireUserExists(any(HttpServletRequest.class));
		verify(requestRequirementService).requireUserIsAuthorizedSession(sessionId, mockUser);
		verify(mockProjectMap).cascadeRemoveItem(eq(database), eq(mockProject));
	}

	@Test
	void testGetSessionsProjects_EmptyProjectList_ReturnsEmptyArray() throws Exception {
		// Arrange
		String sessionId = "session123";
		CopyOnWriteArrayList<Project> emptyProjects = new CopyOnWriteArrayList<>();

		CopyOnWriteArrayList<String> emptyIds = new CopyOnWriteArrayList<>();

		when(mockSessionMap.getItem(sessionId)).thenReturn(mockSession);
		when(mockProjectGroup.getIds()).thenReturn(emptyIds);
		when(mockProjectMap.getItems(emptyIds)).thenReturn(emptyProjects);

		// Act & Assert
		mockMvc.perform(get("/api/project/sessions/{sessionId}/getProjects", sessionId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(0));

		verify(mockSessionMap).getItem(sessionId);
		verify(mockProjectGroup).getIds();
		verify(mockProjectMap).getItems(emptyIds);
	}

	@Test
	void testGetSessionProjects_AlternativeEndpoint_ReturnsProjects() throws Exception {
		// Arrange
		String sessionId = "session123";
		CopyOnWriteArrayList<Project> projects = new CopyOnWriteArrayList<>();
		projects.add(mockProject);

		CopyOnWriteArrayList<String> projectIds = new CopyOnWriteArrayList<>();
		projectIds.add(mockProject.getId());

		when(mockSessionMap.getItem(sessionId)).thenReturn(mockSession);
		when(mockProjectGroup.getIds()).thenReturn(projectIds);
		when(mockProjectMap.getItems(projectIds)).thenReturn(projects);

		// Act & Assert
		mockMvc.perform(get("/api/project/getSessionProjects/{sessionId}", sessionId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(1));

		verify(mockSessionMap).getItem(sessionId);
		verify(mockProjectGroup).getIds();
		verify(mockProjectMap).getItems(projectIds);
	}

	@Test
	void testCreateProject_StudentNotAllowedProjectProposals_ReturnsUnauthorized() throws Exception {
		// Arrange
		String sessionId = "session123";
		String projectName = "TestProject";
		String description = "TestDescription";

		when(mockSession.getAllowStudentProjectProposals()).thenReturn(false);
		when(requestRequirementService.requireUserExists(any(HttpServletRequest.class))).thenReturn(mockUser);
		when(requestRequirementService.requireSessionExists(sessionId)).thenReturn(mockSession);

		// Act & Assert
		mockMvc.perform(post("/api/project/create/{sessionId}/{projectName}/{description}",
				sessionId, projectName, description))
				.andExpect(status().isUnauthorized());

		verify(requestRequirementService).requireUserExists(any(HttpServletRequest.class));
		verify(requestRequirementService).requireSessionExists(sessionId);
		verify(mockProjectMap, never()).addItem(any(), any());
	}

	@Test
	void testCreateProject_CoordinatorBypassesDeadline_ReturnsSuccess() throws Exception {
		// Arrange
		String sessionId = "session123";
		String projectName = "CoordinatorProject";
		String description = "CoordinatorDescription";

		when(mockUser.getRole()).thenReturn(User.Role.Coordinator);
		when(requestRequirementService.requireUserExists(any(HttpServletRequest.class))).thenReturn(mockUser);
		when(requestRequirementService.requireSessionExists(sessionId)).thenReturn(mockSession);
		when(mockProjectMap.addItem(eq(mockProjectGroup), any(Project.class))).thenReturn(mockProject);

		// Act & Assert
		mockMvc.perform(post("/api/project/create/{sessionId}/{projectName}/{description}",
				sessionId, projectName, description))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Project '" + projectName + "' has been created successfully."))
				.andExpect(jsonPath("$.id").value("project123"));

		verify(requestRequirementService).requireUserExists(any(HttpServletRequest.class));
		verify(requestRequirementService).requireSessionExists(sessionId);
		verify(requestRequirementService, never()).requireQuestionnaireDeadlineNotExceeded(any());
		verify(mockProjectMap).addItem(eq(mockProjectGroup), any(Project.class));
	}

	@Test
	void testDeleteProject_StudentNotOwner_ReturnsUnauthorized() throws Exception {
		// Arrange
		String projectId = "project123";
		String sessionId = "session123";

		when(mockProject.getCreatorUserId()).thenReturn("differentUser");
		when(requestRequirementService.requireProjectExists(projectId)).thenReturn(mockProject);
		when(requestRequirementService.requireSessionExists(sessionId)).thenReturn(mockSession);
		when(requestRequirementService.requireUserExists(any(HttpServletRequest.class))).thenReturn(mockUser);

		// Act & Assert
		mockMvc.perform(delete("/api/project/delete/{projectId}/{sessionId}", projectId, sessionId))
				.andExpect(status().isUnauthorized());

		verify(requestRequirementService).requireProjectExists(projectId);
		verify(requestRequirementService).requireSessionExists(sessionId);
		verify(requestRequirementService).requireUserExists(any(HttpServletRequest.class));
		verify(requestRequirementService).requireUserIsAuthorizedSession(sessionId, mockUser);
		verify(mockProjectMap, never()).cascadeRemoveItem(eq(database), any(Project.class));
	}

	@Test
	void testDeleteProject_CoordinatorCanDeleteAnyProject_ReturnsSuccess() throws Exception {
		// Arrange
		String projectId = "project123";
		String sessionId = "session123";

		when(mockUser.getRole()).thenReturn(User.Role.Coordinator);
		when(mockProject.getCreatorUserId()).thenReturn("differentUser");
		when(requestRequirementService.requireProjectExists(projectId)).thenReturn(mockProject);
		when(requestRequirementService.requireSessionExists(sessionId)).thenReturn(mockSession);
		when(requestRequirementService.requireUserExists(any(HttpServletRequest.class))).thenReturn(mockUser);

		// Act & Assert
		mockMvc.perform(delete("/api/project/delete/{projectId}/{sessionId}", projectId, sessionId))
				.andExpect(status().isOk())
				.andExpect(content().string("Project with id " + projectId + " has been deleted successfully."));

		verify(requestRequirementService).requireProjectExists(projectId);
		verify(requestRequirementService).requireSessionExists(sessionId);
		verify(requestRequirementService).requireUserExists(any(HttpServletRequest.class));
		verify(requestRequirementService).requireUserIsAuthorizedSession(sessionId, mockUser);
		verify(requestRequirementService, never()).requireQuestionnaireDeadlineNotExceeded(any());
		verify(mockProjectMap).cascadeRemoveItem(eq(database), eq(mockProject));
	}

	@Test
	void testCreateProject_StudentAlreadyHasProject_ReturnsUnauthorized() throws Exception {
		// Arrange
		String sessionId = "session123";
		String projectName = "SecondProject";
		String description = "SecondDescription";

		// Create existing project
		Project existingProject = mock(Project.class);
		when(existingProject.getCreatorUserId()).thenReturn("user123");
		when(existingProject.getName()).thenReturn("FirstProject");

		// Create list with existing project
		CopyOnWriteArrayList<Project> existingProjects = new CopyOnWriteArrayList<>();
		existingProjects.add(existingProject);

		CopyOnWriteArrayList<String> projectIds = new CopyOnWriteArrayList<>();
		projectIds.add(existingProject.getId());

		when(requestRequirementService.requireUserExists(any(HttpServletRequest.class))).thenReturn(mockUser);
		when(requestRequirementService.requireSessionExists(sessionId)).thenReturn(mockSession);
		when(mockProjectGroup.getIds()).thenReturn(projectIds);
		when(mockProjectMap.getItems(projectIds)).thenReturn(existingProjects);

		// Act & Assert
		mockMvc.perform(post("/api/project/create/{sessionId}/{projectName}/{description}",
				sessionId, projectName, description))
				.andExpect(status().isBadRequest());

		verify(requestRequirementService).requireUserExists(any(HttpServletRequest.class));
		verify(requestRequirementService).requireSessionExists(sessionId);
		verify(mockProjectGroup, times(2)).getIds();
		verify(mockProjectMap, times(2)).getItems(projectIds);
		verify(mockProjectMap, never()).addItem(any(), any());
	}

	@Test
	void testCreateProject_StudentNoExistingProject_ReturnsSuccess() throws Exception {
		// Arrange
		String sessionId = "session123";
		String projectName = "FirstProject";
		String description = "FirstDescription";

		// Create empty project list
		CopyOnWriteArrayList<Project> emptyProjects = new CopyOnWriteArrayList<>();
		CopyOnWriteArrayList<String> emptyIds = new CopyOnWriteArrayList<>();

		when(requestRequirementService.requireUserExists(any(HttpServletRequest.class))).thenReturn(mockUser);
		when(requestRequirementService.requireSessionExists(sessionId)).thenReturn(mockSession);
		when(mockProjectGroup.getIds()).thenReturn(emptyIds);
		when(mockProjectMap.getItems(emptyIds)).thenReturn(emptyProjects);
		when(mockProjectMap.addItem(eq(mockProjectGroup), any(Project.class))).thenReturn(mockProject);

		// Act & Assert
		mockMvc.perform(post("/api/project/create/{sessionId}/{projectName}/{description}",
				sessionId, projectName, description))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Project '" + projectName + "' has been created successfully."))
				.andExpect(jsonPath("$.id").value("project123"));

		verify(requestRequirementService).requireUserExists(any(HttpServletRequest.class));
		verify(requestRequirementService).requireSessionExists(sessionId);
		verify(mockProjectGroup, times(2)).getIds();
		verify(mockProjectMap, times(2)).getItems(emptyIds);
		verify(mockProjectMap).addItem(eq(mockProjectGroup), any(Project.class));
	}

	@Configuration
	static class TestConfig {

		@Bean
		public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
			http
					.cors(cors -> cors.disable())
					.csrf(csrf -> csrf.disable())
					.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
			return http.build();
		}
	}
}