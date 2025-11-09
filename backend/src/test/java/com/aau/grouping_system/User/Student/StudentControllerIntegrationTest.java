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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aau.grouping_system.Authentication.AuthService;
import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.Session.SessionService;

import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest(StudentController.class)
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

        when(database.getSessions()).thenReturn(mock(DatabaseMap.class));
        when(database.getSessions().getItem("session-123")).thenReturn(testSession);
        when(studentService.addStudent(testSession, "john.doe@student.aau.dk", "password123", "John Doe"))
            .thenReturn(testStudent);

        // Act & Assert
        mockMvc.perform(post("/student/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().string("Student created successfully with ID: student-123"));

        verify(database.getSessions()).getItem("session-123");
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

        when(database.getSessions()).thenReturn(mock(DatabaseMap.class));
        when(database.getSessions().getItem("nonexistent-session")).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/student/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound());

        verify(database.getSessions()).getItem("nonexistent-session");
        verify(studentService, never()).addStudent(any(), anyString(), anyString(), anyString());
    }

    @Test
    void testSaveQuestionnaireAnswers_ValidStudentAndSession_ReturnsSuccess() throws Exception {
        // Arrange
        String requestBody = """
            {
                "projectPriorities": ["project-1", "project-2", "project-3"]
            }
            """;

        when(authService.getStudentByUser(any(HttpServletRequest.class)))
            .thenReturn(testStudent);
        when(database.getSessions()).thenReturn(mock(DatabaseMap.class));
        when(database.getSessions().getItem("session-123")).thenReturn(testSession);
        when(sessionService.isQuestionnaireDeadlineExceeded(testSession))
            .thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/student/saveQuestionnaireAnswers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Saved questionnaire answers successfully."));

        verify(authService).getStudentByUser(any(HttpServletRequest.class));
        verify(sessionService).isQuestionnaireDeadlineExceeded(testSession);
        verify(studentService).applyQuestionnaireAnswers(eq(testStudent), any());
    }

    @Test
    void testSaveQuestionnaireAnswers_StudentNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        String requestBody = """
            {
                "projectPriorities": ["project-1", "project-2", "project-3"]
            }
            """;

        when(authService.getStudentByUser(any(HttpServletRequest.class)))
            .thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/student/saveQuestionnaireAnswers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound());

        verify(authService).getStudentByUser(any(HttpServletRequest.class));
        verify(studentService, never()).applyQuestionnaireAnswers(any(), any());
    }

    @Test
    void testSaveQuestionnaireAnswers_DeadlineExceeded_ReturnsUnauthorized() throws Exception {
        // Arrange
        String requestBody = """
            {
                "projectPriorities": ["project-1", "project-2", "project-3"]
            }
            """;

        when(authService.getStudentByUser(any(HttpServletRequest.class)))
            .thenReturn(testStudent);
        when(database.getSessions()).thenReturn(mock(DatabaseMap.class));
        when(database.getSessions().getItem("session-123")).thenReturn(testSession);
        when(sessionService.isQuestionnaireDeadlineExceeded(testSession))
            .thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/student/saveQuestionnaireAnswers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnauthorized());

        verify(authService).getStudentByUser(any(HttpServletRequest.class));
        verify(sessionService).isQuestionnaireDeadlineExceeded(testSession);
        verify(studentService, never()).applyQuestionnaireAnswers(any(), any());
    }
}