package com.aau.grouping_system.Session;

import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
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
import com.aau.grouping_system.Database.DatabaseItemChildGroup;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.EmailSystem.EmailService;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.SessionMember.SessionMember;
import com.aau.grouping_system.User.SessionMember.SessionMemberService;
import com.aau.grouping_system.User.SessionMember.Student.Student;
import com.aau.grouping_system.User.SessionMember.Supervisor.Supervisor;
import com.aau.grouping_system.User.UserService;
import com.aau.grouping_system.Utils.RequestRequirementService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest(SessionSetupController.class)
@AutoConfigureWebMvc
@ComponentScan(basePackages = {
    "com.aau.grouping_system.Session",
    "com.aau.grouping_system.InputValidation",
    "com.aau.grouping_system.Exceptions"
})
@Import({ SessionSetupControllerIntegrationTest.TestConfig.class, SecurityConfig.class })
class SessionSetupControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RequestRequirementService requestRequirementService;

    @MockitoBean
    private SessionSetupService sessionSetupService;

    @MockitoBean
    private SessionMemberService sessionMemberService;

    @MockitoBean
    private Database database;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private SessionService sessionService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private EmailService emailService;

    @Configuration
    static class TestConfig {
    }

   
    @Test
    void testSaveSetup_ValidRequest_ReturnsOk() throws Exception {
        // Arrange
        String sessionId = "test-session-id";
        Session mockSession = Mockito.mock(Session.class);
        Coordinator mockCoordinator = Mockito.mock(Coordinator.class);

        SessionSetupRecord setupRecord = new SessionSetupRecord(
            "Test Session",
            2,
            6,
            true,
            "2024-12-31T23:59:59.00Z",
            "supervisor1@example.com Supervisor One\nsupervisor2@example.com Supervisor Two",
            "student1@example.com Student One\nstudent2@example.com Student Two"
        );

        String requestJson = objectMapper.writeValueAsString(setupRecord);

        Mockito.when(requestRequirementService.requireSessionExists(sessionId)).thenReturn(mockSession);
        Mockito.when(requestRequirementService.requireUserCoordinatorExists(any(HttpServletRequest.class))).thenReturn(mockCoordinator);

        // Act & Assert
        mockMvc.perform(post("/api/sessionSetup/{sessionId}/saveSetup", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Session setup saved successfully!"));

        // Verify service interactions
        Mockito.verify(requestRequirementService).requireSessionExists(sessionId);
        Mockito.verify(requestRequirementService).requireUserCoordinatorExists(any(HttpServletRequest.class));
        Mockito.verify(requestRequirementService).requireCoordinatorIsAuthorizedSession(sessionId, mockCoordinator);
        Mockito.verify(sessionSetupService).updateSessionSetup(eq(mockSession), any(SessionSetupRecord.class));
    }

    @Test
    void testSaveSetup_SessionNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        String sessionId = "nonexistent-session";
        SessionSetupRecord setupRecord = new SessionSetupRecord(
            "Test Session",
            2,
            6,
            true,
            "2024-12-31T23:59:59.00Z",
            "supervisor@example.com Supervisor Name",
            "student@example.com Student Name"
        );

        String requestJson = objectMapper.writeValueAsString(setupRecord);

        Mockito.when(requestRequirementService.requireSessionExists(sessionId))
                .thenThrow(new RequestException(HttpStatus.NOT_FOUND, "Session not found"));

        // Act & Assert
        mockMvc.perform(post("/api/sessionSetup/{sessionId}/saveSetup", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Session not found"));
    }

    @Test
    void testSaveSetup_InvalidSessionId_ReturnsNotFound() throws Exception {

        SessionSetupRecord setupRecord = new SessionSetupRecord(
            "Test Session",
            2,
            6,
            true,
            "2024-12-31T23:59:59.00Z",
            "supervisor@example.com Supervisor Name",
            "student@example.com Student Name"
        );

        String requestJson = objectMapper.writeValueAsString(setupRecord);

        // Act & Assert
        mockMvc.perform(post("/api/sessionSetup/{sessionId}/saveSetup", "session<script>alert('xss')</script>")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSaveSetup_InvalidRequestBody_ReturnsBadRequest() throws Exception {
        // Arrange
        String sessionId = "test-session-id";
        
        String invalidRequestJson = """
                {
                    "name": "",
                    "minGroupSize": -1,
                    "maxGroupSize": 100001,
                    "allowStudentProjectProposals": true,
                    "questionnaireDeadlineISODateString": "invalid-date",
                    "supervisorEmails": "invalid-email",
                    "studentEmails": "another-invalid-email"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/sessionSetup/{sessionId}/saveSetup", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSaveSetup_UnauthorizedCoordinator_ReturnsForbidden() throws Exception {
        // Arrange
        String sessionId = "test-session-id";
        Session mockSession = Mockito.mock(Session.class);
        Coordinator mockCoordinator = Mockito.mock(Coordinator.class);

        SessionSetupRecord setupRecord = new SessionSetupRecord(
            "Test Session",
            2,
            6,
            true,
            "2024-12-31T23:59:59.00Z",
            "supervisor@example.com Supervisor Name",
            "student@example.com Student Name"
        );

        String requestJson = objectMapper.writeValueAsString(setupRecord);

        Mockito.when(requestRequirementService.requireSessionExists(sessionId)).thenReturn(mockSession);
        Mockito.when(requestRequirementService.requireUserCoordinatorExists(any(HttpServletRequest.class))).thenReturn(mockCoordinator);
        Mockito.doThrow(new RequestException(HttpStatus.FORBIDDEN, "Coordinator not authorized for this session"))
                .when(requestRequirementService).requireCoordinatorIsAuthorizedSession(sessionId, mockCoordinator);

        // Act & Assert
        mockMvc.perform(post("/api/sessionSetup/{sessionId}/saveSetup", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Coordinator not authorized for this session"));
    }

    @Test
    @SuppressWarnings("rawtypes")
    void testEmailNewPasswordsToStudents_ValidRequest_ReturnsOk() throws Exception {
        // Arrange
        String sessionId = "test-session-id";
        Session mockSession = Mockito.mock(Session.class);
        Coordinator mockCoordinator = Mockito.mock(Coordinator.class);
        DatabaseItemChildGroup mockStudents = Mockito.mock(DatabaseItemChildGroup.class);
        CopyOnWriteArrayList<SessionMember> mockStudentList = new CopyOnWriteArrayList<>();
        
        SessionSetupController.emailNewPasswordsRecord emailRecord = 
            new SessionSetupController.emailNewPasswordsRecord(true);
        
        String requestJson = objectMapper.writeValueAsString(emailRecord);

        Mockito.when(requestRequirementService.requireSessionExists(sessionId)).thenReturn(mockSession);
        Mockito.when(requestRequirementService.requireUserCoordinatorExists(any(HttpServletRequest.class))).thenReturn(mockCoordinator);
        Mockito.when(mockSession.getId()).thenReturn(sessionId);
        Mockito.when(mockSession.getStudents()).thenReturn(mockStudents);
        CopyOnWriteArrayList<String> studentIds = new CopyOnWriteArrayList<>();
        mockStudentList.forEach(student -> studentIds.add(student.getId()));
        
        DatabaseMap<Student> mockStudentMap = mock(DatabaseMap.class);
        Mockito.when(mockStudents.getIds()).thenReturn(studentIds);
        Mockito.when(database.getStudents()).thenReturn(mockStudentMap);
        Mockito.when(mockStudentMap.getItems(studentIds)).thenReturn((CopyOnWriteArrayList) mockStudentList);

        // Act & Assert
        mockMvc.perform(post("/api/sessionSetup/{sessionId}/emailNewPasswordTo/students", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Emails have been sent to students."));

        // Verify service interactions
        Mockito.verify(requestRequirementService).requireSessionExists(sessionId);
        Mockito.verify(requestRequirementService).requireUserCoordinatorExists(any(HttpServletRequest.class));
        Mockito.verify(requestRequirementService).requireCoordinatorIsAuthorizedSession(sessionId, mockCoordinator);
        Mockito.verify(sessionMemberService).applyAndEmailNewPasswords(eq(mockSession), any(CopyOnWriteArrayList.class));
    }

    @Test
    void testEmailNewPasswordsToStudents_SessionNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        String sessionId = "nonexistent-session";
        SessionSetupController.emailNewPasswordsRecord emailRecord = 
            new SessionSetupController.emailNewPasswordsRecord(false);
        
        String requestJson = objectMapper.writeValueAsString(emailRecord);

        Mockito.when(requestRequirementService.requireSessionExists(sessionId))
                .thenThrow(new RequestException(HttpStatus.NOT_FOUND, "Session not found"));

        // Act & Assert
        mockMvc.perform(post("/api/sessionSetup/{sessionId}/emailNewPasswordTo/students", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Session not found"));
    }

    @Test
    @SuppressWarnings("rawtypes")
    void testEmailNewPasswordsToSupervisors_ValidRequest_ReturnsOk() throws Exception {
        // Arrange
        String sessionId = "test-session-id";
        Session mockSession = Mockito.mock(Session.class);
        Coordinator mockCoordinator = Mockito.mock(Coordinator.class);
        DatabaseItemChildGroup mockSupervisors = Mockito.mock(DatabaseItemChildGroup.class);
        CopyOnWriteArrayList<SessionMember> mockSupervisorList = new CopyOnWriteArrayList<>();
        
        SessionSetupController.emailNewPasswordsRecord emailRecord = 
            new SessionSetupController.emailNewPasswordsRecord(false);
        
        String requestJson = objectMapper.writeValueAsString(emailRecord);

        Mockito.when(requestRequirementService.requireSessionExists(sessionId)).thenReturn(mockSession);
        Mockito.when(requestRequirementService.requireUserCoordinatorExists(any(HttpServletRequest.class))).thenReturn(mockCoordinator);
        Mockito.when(mockSession.getId()).thenReturn(sessionId);
        Mockito.when(mockSession.getSupervisors()).thenReturn(mockSupervisors);
        
        CopyOnWriteArrayList<String> supervisorIds = new CopyOnWriteArrayList<>();
        mockSupervisorList.forEach(supervisor -> supervisorIds.add(supervisor.getId()));
        
        DatabaseMap<Supervisor> mockSupervisorMap = mock(DatabaseMap.class);
        Mockito.when(mockSupervisors.getIds()).thenReturn(supervisorIds);
        Mockito.when(database.getSupervisors()).thenReturn(mockSupervisorMap);
        Mockito.when(mockSupervisorMap.getItems(supervisorIds)).thenReturn((CopyOnWriteArrayList) mockSupervisorList);

        // Act & Assert
        mockMvc.perform(post("/api/sessionSetup/{sessionId}/emailNewPasswordTo/supervisors", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Emails have been sent to supervisors."));

        // Verify service interactions
        Mockito.verify(requestRequirementService).requireSessionExists(sessionId);
        Mockito.verify(requestRequirementService).requireUserCoordinatorExists(any(HttpServletRequest.class));
        Mockito.verify(requestRequirementService).requireCoordinatorIsAuthorizedSession(sessionId, mockCoordinator);
        Mockito.verify(sessionMemberService).applyAndEmailNewPasswords(eq(mockSession), any(CopyOnWriteArrayList.class));
    }

    @Test
    void testEmailNewPasswordsToSupervisors_UnauthorizedCoordinator_ReturnsForbidden() throws Exception {
        // Arrange
        String sessionId = "test-session-id";
        Session mockSession = Mockito.mock(Session.class);
        Coordinator mockCoordinator = Mockito.mock(Coordinator.class);

        SessionSetupController.emailNewPasswordsRecord emailRecord = 
            new SessionSetupController.emailNewPasswordsRecord(true);
        
        String requestJson = objectMapper.writeValueAsString(emailRecord);

        DatabaseItemChildGroup mockSupervisors = Mockito.mock(DatabaseItemChildGroup.class);
        CopyOnWriteArrayList<SessionMember> mockSupervisorList = new CopyOnWriteArrayList<>();

        Mockito.when(requestRequirementService.requireSessionExists(sessionId)).thenReturn(mockSession);
        Mockito.when(requestRequirementService.requireUserCoordinatorExists(any(HttpServletRequest.class))).thenReturn(mockCoordinator);
        Mockito.when(mockSession.getId()).thenReturn(sessionId);
        Mockito.when(mockSession.getSupervisors()).thenReturn(mockSupervisors);
        
        CopyOnWriteArrayList<String> supervisorIds2 = new CopyOnWriteArrayList<>();
        mockSupervisorList.forEach(supervisor -> supervisorIds2.add(supervisor.getId()));
        
        DatabaseMap<Supervisor> mockSupervisorMap2 = mock(DatabaseMap.class);
        Mockito.when(mockSupervisors.getIds()).thenReturn(supervisorIds2);
        Mockito.when(database.getSupervisors()).thenReturn(mockSupervisorMap2);
        Mockito.when(mockSupervisorMap2.getItems(supervisorIds2)).thenReturn((CopyOnWriteArrayList) mockSupervisorList);
        Mockito.doThrow(new RequestException(HttpStatus.FORBIDDEN, "Coordinator not authorized for this session"))
                .when(requestRequirementService).requireCoordinatorIsAuthorizedSession(sessionId, mockCoordinator);

        // Act & Assert
        mockMvc.perform(post("/api/sessionSetup/{sessionId}/emailNewPasswordTo/supervisors", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Coordinator not authorized for this session"));
    }

    @Test
    void testEmailNewPasswordsToSupervisors_InvalidSessionId_ReturnsNotFound() throws Exception {
        SessionSetupController.emailNewPasswordsRecord emailRecord = 
            new SessionSetupController.emailNewPasswordsRecord(true);
        
        String requestJson = objectMapper.writeValueAsString(emailRecord);

        // Act & Assert
        mockMvc.perform(post("/api/sessionSetup/{sessionId}/emailNewPasswordTo/supervisors", "session<script>alert('xss')</script>")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isNotFound());
    }
}