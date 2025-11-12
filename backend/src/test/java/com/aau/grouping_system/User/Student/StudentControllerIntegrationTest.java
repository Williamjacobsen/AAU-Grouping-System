package com.aau.grouping_system.User.Student;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aau.grouping_system.Authentication.AuthService;
import com.aau.grouping_system.Config.CorsConfig;
import com.aau.grouping_system.Config.SecurityConfig;
import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.Session.SessionService;
import com.aau.grouping_system.Utils.RequirementService;
import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest(StudentController.class)
@AutoConfigureWebMvc
@ComponentScan(basePackages = {"com.aau.grouping_system.User.Student", "com.aau.grouping_system.Config", "com.aau.grouping_system.Exceptions"})
@Import({StudentControllerIntegrationTest.TestConfig.class, SecurityConfig.class, CorsConfig.class})
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
    private RequirementService requirementService;

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
    void testCreateStudent_ValidRequest_ReturnsCreatedStudent() throws Exception {
        // Arrange
        String requestBody = """
            {
                "sessionId": "session-123",
                "email": "john.doe@student.aau.dk",
                "password": "password123",
                "name": "John Doe"
            }
            """;

        when(requirementService.requireSessionExists("session-123")).thenReturn(testSession);
        when(studentService.addStudent(testSession, "john.doe@student.aau.dk", "password123", "John Doe"))
            .thenReturn(testStudent);

        // Act & Assert
        mockMvc.perform(post("/student/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().string("Student created successfully with ID: student-123"));

        verify(requirementService).requireSessionExists("session-123");
        verify(studentService).addStudent(testSession, "john.doe@student.aau.dk", "password123", "John Doe");
    }

    @Test
    void testCreateStudent_MissingRequiredFields_ReturnsBadRequest() throws Exception {
        // Arrange
        String requestBody = """
            {
                "sessionId": "session-123",
                "email": "john.doe@student.aau.dk"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/student/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(studentService, never()).addStudent(any(), anyString(), anyString(), anyString());
    }

    @Test
    void testCreateStudent_SessionNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        String requestBody = """
            {
                "sessionId": "nonexistent-session",
                "email": "john.doe@student.aau.dk",
                "password": "password123",
                "name": "John Doe"
            }
            """;

        when(requirementService.requireSessionExists("nonexistent-session"))
            .thenThrow(new RequestException(HttpStatus.NOT_FOUND, "Session not found"));

        // Act & Assert
        mockMvc.perform(post("/student/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound());

        verify(requirementService).requireSessionExists("nonexistent-session");
        verify(studentService, never()).addStudent(any(), anyString(), anyString(), anyString());
    }

    @Test
    void testSaveQuestionnaireAnswers_ValidStudentAndSession_ReturnsSuccess() throws Exception {
        // Arrange
        String requestBody = """
            {
                "desiredProjectId1": "project-1",
                "desiredProjectId2": "project-2", 
                "desiredProjectId3": "project-3",
                "desiredGroupSizeMin": 2,
                "desiredGroupSizeMax": 4,
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
        when(sessionService.isQuestionnaireDeadlineExceeded(testSession))
            .thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/student/saveQuestionnaireAnswers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Saved questionnaire answers successfully."));

        verify(requirementService).requireUserStudentExists(any(HttpServletRequest.class));
        verify(sessionService).isQuestionnaireDeadlineExceeded(testSession);
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
                "desiredGroupSizeMin": 2,
                "desiredGroupSizeMax": 4,
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
        mockMvc.perform(post("/student/saveQuestionnaireAnswers")
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
                "desiredGroupSizeMin": 2,
                "desiredGroupSizeMax": 4,
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
        when(sessionService.isQuestionnaireDeadlineExceeded(testSession))
            .thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/student/saveQuestionnaireAnswers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnauthorized());

        verify(requirementService).requireUserStudentExists(any(HttpServletRequest.class));
        verify(sessionService).isQuestionnaireDeadlineExceeded(testSession);
        verify(studentService, never()).applyQuestionnaireAnswers(any(), any());
    }

    @Configuration
    static class TestConfig {
        // Configuration class
    }
}