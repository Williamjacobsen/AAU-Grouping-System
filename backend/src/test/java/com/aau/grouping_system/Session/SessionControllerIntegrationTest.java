package com.aau.grouping_system.Session;

import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aau.grouping_system.Authentication.AuthService;
import com.aau.grouping_system.Config.CorsConfig;
import com.aau.grouping_system.Config.SecurityConfig;
import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseItemChildGroup;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.SessionMember.Student.Student;
import com.aau.grouping_system.User.SessionMember.Supervisor.Supervisor;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.Utils.RequestRequirementService;

import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest(SessionController.class)
@AutoConfigureWebMvc
@ComponentScan(basePackages = { 
    "com.aau.grouping_system.Session", 
    "com.aau.grouping_system.Exceptions", 
    "com.aau.grouping_system.User.SessionMember" 
})
@Import({ SessionControllerIntegrationTest.TestConfig.class, SecurityConfig.class, CorsConfig.class })
class SessionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Database database;

    @MockitoBean
    private SessionService sessionService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private RequestRequirementService requestRequirementService;

    @MockitoBean
    private com.aau.grouping_system.User.SessionMember.SessionMemberService sessionMemberService;

    @MockitoBean
    private SessionSetupService sessionSetupService;

    private Session mockSession;
    private Session serializableSession;
    private Coordinator mockCoordinator;
    private Student mockStudent;
    private Supervisor mockSupervisor;
    private Project mockProject;
    private User mockUser;
    private DatabaseItemChildGroup mockSupervisorGroup;
    private DatabaseItemChildGroup mockStudentGroup;
    private DatabaseItemChildGroup mockProjectGroup;
    private DatabaseMap<Session> mockSessionMap;

    // Custom Session class
    private static class TestSession extends Session {
        public TestSession(Database db, Coordinator coordinator, String name) {
            super(db, coordinator, name);
        }
        
        @Override
        public DatabaseItemChildGroup getSupervisors() {
            return null;
        }
        
        @Override
        public DatabaseItemChildGroup getStudents() {
            return null;
        }
        
        @Override
        public DatabaseItemChildGroup getProjects() {
            return null;
        }
    }

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        // Create mock objects
        mockSession = mock(Session.class);
        mockCoordinator = mock(Coordinator.class);
        mockStudent = mock(Student.class);
        mockSupervisor = mock(Supervisor.class);
        mockProject = mock(Project.class);
        mockUser = mock(User.class);
        mockSupervisorGroup = mock(DatabaseItemChildGroup.class);
        mockStudentGroup = mock(DatabaseItemChildGroup.class);
        mockProjectGroup = mock(DatabaseItemChildGroup.class);
        mockSessionMap = mock(DatabaseMap.class);
        
        // Setup mock database
        when(database.getSessions()).thenReturn(mockSessionMap);
        
        // Setup mock session
        when(mockSession.getId()).thenReturn("session-123");
        when(mockSession.getName()).thenReturn("Test Session");
        when(mockSession.getSupervisors()).thenReturn(mockSupervisorGroup);
        when(mockSession.getStudents()).thenReturn(mockStudentGroup);
        when(mockSession.getProjects()).thenReturn(mockProjectGroup);
        
        try {
            serializableSession = new TestSession(database, mockCoordinator, "Test Session");
        } catch (Exception e) {
            serializableSession = mock(Session.class);
            when(serializableSession.getId()).thenReturn("session-123");
            when(serializableSession.getName()).thenReturn("Test Session");
            when(serializableSession.getSupervisors()).thenReturn(null);
            when(serializableSession.getStudents()).thenReturn(null);
            when(serializableSession.getProjects()).thenReturn(null);
        }
        
        // Setup mock user
        when(mockCoordinator.getId()).thenReturn("coordinator-123");
        when(mockCoordinator.getEmail()).thenReturn("coordinator@test.com");
        when(mockStudent.getId()).thenReturn("student-123");
        when(mockStudent.getEmail()).thenReturn("student@test.com");
        when(mockSupervisor.getId()).thenReturn("supervisor-123");
        when(mockSupervisor.getEmail()).thenReturn("supervisor@test.com");
        when(mockUser.getId()).thenReturn("user-123");
        
        // Setup mock project
        when(mockProject.getId()).thenReturn("project-123");
        when(mockProject.getName()).thenReturn("Test Project");
    }

    @Test
    void testCreateSession_ValidRequest_ReturnsSuccess() throws Exception {
        // Arrange
        String requestBody = """
                {
                    "name": "New Test Session"
                }
                """;

        when(requestRequirementService.requireUserCoordinatorExists(any(HttpServletRequest.class)))
                .thenReturn(mockCoordinator);
        when(sessionService.createSession("New Test Session", mockCoordinator))
                .thenReturn(serializableSession);

        // Act & Assert
        mockMvc.perform(post("/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .sessionAttr("user", mockCoordinator))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("session-123"))
                .andExpect(jsonPath("$.name").value("Test Session"));

        verify(requestRequirementService).requireUserCoordinatorExists(any(HttpServletRequest.class));
        verify(sessionService).createSession("New Test Session", mockCoordinator);
    }

    @Test
    void testCreateSession_InvalidName_ReturnsBadRequest() throws Exception {
        // Arrange
        String requestBody = """
                {
                    "name": ""
                }
                """;

        when(requestRequirementService.requireUserCoordinatorExists(any(HttpServletRequest.class)))
                .thenReturn(mockCoordinator);

        // Act & Assert
        mockMvc.perform(post("/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .sessionAttr("user", mockCoordinator))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllSessions_ValidCoordinator_ReturnsSessionList() throws Exception {
        // Arrange
        CopyOnWriteArrayList<Session> sessions = new CopyOnWriteArrayList<>();
        sessions.add(serializableSession);

        when(requestRequirementService.requireUserCoordinatorExists(any(HttpServletRequest.class)))
                .thenReturn(mockCoordinator);
        when(sessionService.getSessionsByCoordinator(mockCoordinator))
                .thenReturn(sessions);

        // Act & Assert
        mockMvc.perform(get("/sessions")
                .sessionAttr("user", mockCoordinator))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("session-123"));

        verify(requestRequirementService).requireUserCoordinatorExists(any(HttpServletRequest.class));
        verify(sessionService).getSessionsByCoordinator(mockCoordinator);
    }

    @Test
    void testGetSession_ValidSessionAndUser_ReturnsSession() throws Exception {
        // Arrange
        String sessionId = "session-123";

        when(requestRequirementService.requireSessionExists(sessionId))
                .thenReturn(serializableSession);
        when(requestRequirementService.requireUserExists(any(HttpServletRequest.class)))
                .thenReturn(mockUser);

        // Act & Assert
        mockMvc.perform(get("/sessions/{sessionId}", sessionId)
                .sessionAttr("user", mockUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("session-123"))
                .andExpect(jsonPath("$.name").value("Test Session"));

        verify(requestRequirementService).requireSessionExists(sessionId);
        verify(requestRequirementService).requireUserExists(any(HttpServletRequest.class));
        verify(requestRequirementService).requireUserIsAuthorizedSession(sessionId, mockUser);
    }

    @Test
    void testGetSupervisors_ValidSessionAndUser_ReturnsSupervisorList() throws Exception {
        // Arrange
        String sessionId = "session-123";
        CopyOnWriteArrayList<Supervisor> supervisors = new CopyOnWriteArrayList<>();
        supervisors.add(mockSupervisor);

        when(requestRequirementService.requireSessionExists(sessionId))
                .thenReturn(mockSession);
        when(requestRequirementService.requireUserExists(any(HttpServletRequest.class)))
                .thenReturn(mockUser);
        doReturn(supervisors).when(mockSupervisorGroup).getItems(database);

        // Act & Assert
        mockMvc.perform(get("/sessions/{sessionId}/getSupervisors", sessionId)
                .sessionAttr("user", mockUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("supervisor-123"));

        verify(requestRequirementService).requireSessionExists(sessionId);
        verify(requestRequirementService).requireUserExists(any(HttpServletRequest.class));
        verify(requestRequirementService).requireUserIsAuthorizedSession(sessionId, mockUser);
    }

    @Test
    void testGetStudents_ValidSessionAndUser_ReturnsStudentList() throws Exception {
        // Arrange
        String sessionId = "session-123";
        CopyOnWriteArrayList<Student> students = new CopyOnWriteArrayList<>();
        students.add(mockStudent);

        when(requestRequirementService.requireSessionExists(sessionId))
                .thenReturn(mockSession);
        when(requestRequirementService.requireUserExists(any(HttpServletRequest.class)))
                .thenReturn(mockUser);
        doReturn(students).when(mockStudentGroup).getItems(database);

        // Act & Assert
        mockMvc.perform(get("/sessions/{sessionId}/getStudents", sessionId)
                .sessionAttr("user", mockUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("student-123"));

        verify(requestRequirementService).requireSessionExists(sessionId);
        verify(requestRequirementService).requireUserExists(any(HttpServletRequest.class));
        verify(requestRequirementService).requireUserIsAuthorizedSession(sessionId, mockUser);
    }

    @Test
    void testGetProjects_ValidSession_ReturnsProjectList() throws Exception {
        // Arrange
        String sessionId = "session-123";
        CopyOnWriteArrayList<Project> projects = new CopyOnWriteArrayList<>();
        projects.add(mockProject);

        when(mockSessionMap.getItem(sessionId)).thenReturn(mockSession);
        doReturn(projects).when(mockProjectGroup).getItems(database);

        // Act & Assert
        mockMvc.perform(get("/sessions/{sessionId}/getProjects", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("project-123"));

        verify(mockSessionMap).getItem(sessionId);
    }

    @Test
    void testGetProjects_SessionNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        String sessionId = "nonexistent-session";

        when(mockSessionMap.getItem(sessionId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/sessions/{sessionId}/getProjects", sessionId))
                .andExpect(status().isNotFound());

        verify(mockSessionMap).getItem(sessionId);
    }

    @Test
    void testDeleteSession_ValidCoordinatorAndSession_ReturnsSuccess() throws Exception {
        // Arrange
        String sessionId = "session-123";

        when(requestRequirementService.requireUserCoordinatorExists(any(HttpServletRequest.class)))
                .thenReturn(mockCoordinator);
        when(sessionService.deleteSession(sessionId, mockCoordinator))
                .thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/sessions/{sessionId}", sessionId)
                .sessionAttr("user", mockCoordinator))
                .andExpect(status().isOk())
                .andExpect(content().string("Session deleted successfully"));

        verify(requestRequirementService).requireUserCoordinatorExists(any(HttpServletRequest.class));
        verify(sessionService).deleteSession(sessionId, mockCoordinator);
    }

    @Test
    void testDeleteSession_UnauthorizedUser_ReturnsForbidden() throws Exception {
        // Arrange
        String sessionId = "session-123";

        when(requestRequirementService.requireUserCoordinatorExists(any(HttpServletRequest.class)))
                .thenReturn(mockCoordinator);
        when(sessionService.deleteSession(sessionId, mockCoordinator))
                .thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/sessions/{sessionId}", sessionId)
                .sessionAttr("user", mockCoordinator))
                .andExpect(status().isForbidden());

        verify(requestRequirementService).requireUserCoordinatorExists(any(HttpServletRequest.class));
        verify(sessionService).deleteSession(sessionId, mockCoordinator);
    }

    @Test
    void testGetSessions_EmptyList_ReturnsEmptyArray() throws Exception {
        // Arrange
        CopyOnWriteArrayList<Session> emptySessions = new CopyOnWriteArrayList<>();

        when(requestRequirementService.requireUserCoordinatorExists(any(HttpServletRequest.class)))
                .thenReturn(mockCoordinator);
        when(sessionService.getSessionsByCoordinator(mockCoordinator))
                .thenReturn(emptySessions);

        // Act & Assert
        mockMvc.perform(get("/sessions")
                .sessionAttr("user", mockCoordinator))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(requestRequirementService).requireUserCoordinatorExists(any(HttpServletRequest.class));
        verify(sessionService).getSessionsByCoordinator(mockCoordinator);
    }

    @Configuration
    static class TestConfig {
    }
}