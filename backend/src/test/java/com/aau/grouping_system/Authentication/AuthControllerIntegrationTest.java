package com.aau.grouping_system.Authentication;

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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aau.grouping_system.Config.CorsConfig;
import com.aau.grouping_system.Config.SecurityConfig;
import com.aau.grouping_system.EmailSystem.EmailService;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.User;

import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest(AuthController.class)
@AutoConfigureWebMvc
@ComponentScan(basePackages = {"com.aau.grouping_system.Authentication", "com.aau.grouping_system.Exceptions"})
@Import({AuthControllerIntegrationTest.TestConfig.class, SecurityConfig.class, CorsConfig.class})
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;
    
    @MockitoBean
    private EmailService emailService;

    private Coordinator testCoordinator;
    private Student testStudent;

    @BeforeEach
    void setUp() {
        testCoordinator = mock(Coordinator.class);
        testStudent = mock(Student.class);
        
        when(testCoordinator.getEmail()).thenReturn("coordinator@test.com");
        when(testStudent.getEmail()).thenReturn("student@test.com");
    }

    @Test
    void testSignIn_ValidCoordinatorCredentials_ReturnsSuccess() throws Exception {
        // Arrange
        String requestBody = """
            {
                "emailOrId": "coordinator@test.com",
                "password": "password123",
                "role": "Coordinator"
            }
            """;

        when(authService.findByEmailOrId("coordinator@test.com", User.Role.Coordinator))
            .thenReturn(testCoordinator);
        when(authService.isPasswordCorrect("password123", testCoordinator))
            .thenReturn(true);

        mockMvc.perform(post("/auth/signIn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Signed in, user: coordinator@test.com"));

        verify(authService).findByEmailOrId("coordinator@test.com", User.Role.Coordinator);
        verify(authService).isPasswordCorrect("password123", testCoordinator);
        verify(authService).invalidateOldSession(any(HttpServletRequest.class));
        verify(authService).createNewSession(any(HttpServletRequest.class), eq(testCoordinator));
    }

    @Test
    void testSignIn_ValidStudentCredentials_ReturnsSuccess() throws Exception {
        // Arrange
        String studentId = "student-uuid-123";
        String requestBody = """
            {
                "emailOrId": "%s",
                "password": "password123",
                "role": "Student"
            }
            """.formatted(studentId);

        when(authService.findByEmailOrId(studentId, User.Role.Student))
            .thenReturn(testStudent);
        when(authService.isPasswordCorrect("password123", testStudent))
            .thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/auth/signIn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Signed in, user: student@test.com"));

        verify(authService).findByEmailOrId(studentId, User.Role.Student);
        verify(authService).isPasswordCorrect("password123", testStudent);
    }

    @Test
    void testSignIn_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        // Arrange
        String requestBody = """
            {
                "emailOrId": "invalid@test.com",
                "password": "wrongpassword",
                "role": "Coordinator"
            }
            """;

        when(authService.findByEmailOrId("invalid@test.com", User.Role.Coordinator))
            .thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/auth/signIn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnauthorized());

        verify(authService).findByEmailOrId("invalid@test.com", User.Role.Coordinator);
        verify(authService, never()).isPasswordCorrect(anyString(), any(User.class));
        verify(authService, never()).createNewSession(any(HttpServletRequest.class), any(User.class));
    }

    @Test
    void testSignIn_ValidUserWrongPassword_ReturnsUnauthorized() throws Exception {
        // Arrange
        String requestBody = """
            {
                "emailOrId": "coordinator@test.com",
                "password": "wrongpassword",
                "role": "Coordinator"
            }
            """;

        when(authService.findByEmailOrId("coordinator@test.com", User.Role.Coordinator))
            .thenReturn(testCoordinator);
        when(authService.isPasswordCorrect("wrongpassword", testCoordinator))
            .thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/auth/signIn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnauthorized());

        verify(authService).findByEmailOrId("coordinator@test.com", User.Role.Coordinator);
        verify(authService).isPasswordCorrect("wrongpassword", testCoordinator);
        verify(authService, never()).createNewSession(any(HttpServletRequest.class), any(User.class));
    }

    @Test
    void testSignIn_MissingEmailOrId_ReturnsBadRequest() throws Exception {
        // Arrange
        String requestBody = """
            {
                "password": "password123",
                "role": "Coordinator"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/auth/signIn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSignIn_InvalidRole_ReturnsBadRequest() throws Exception {
        // Arrange
        String requestBody = """
            {
                "emailOrId": "test@test.com",
                "password": "password123",
                "role": "InvalidRole"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/auth/signIn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSignOut_ValidSession_ReturnsSuccess() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/auth/signOut"))
                .andExpect(status().isOk())
                .andExpect(content().string("Signed out"));

        verify(authService).invalidateOldSession(any(HttpServletRequest.class));
    }

        @Test
    void testGetUser_ValidSession_ReturnsUser() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/auth/getUser")
                .sessionAttr("user", testCoordinator))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("coordinator@test.com"));
    }

    @Test
    void testGetUser_NoSession_ReturnsUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/auth/getUser"))
                .andExpect(status().isUnauthorized());
    }

    @Configuration
    static class TestConfig {
        // Configuration class
    }
}