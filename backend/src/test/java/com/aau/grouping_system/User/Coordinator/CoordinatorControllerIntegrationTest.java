package com.aau.grouping_system.User.Coordinator;

import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aau.grouping_system.Config.CorsConfig;
import com.aau.grouping_system.Config.SecurityConfig;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.User.UserService;
import com.aau.grouping_system.Utils.RequestRequirementService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest(CoordinatorController.class)
@AutoConfigureWebMvc
@ComponentScan(basePackages = {
    "com.aau.grouping_system.User.Coordinator",
    "com.aau.grouping_system.InputValidation",
    "com.aau.grouping_system.Exceptions"
})
@Import({CoordinatorControllerIntegrationTest.TestConfig.class, SecurityConfig.class, CorsConfig.class})
class CoordinatorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CoordinatorService coordinatorService;

    @MockitoBean
    private RequestRequirementService requestRequirementService;

    @MockitoBean
    private UserService userService;

    private User mockCoordinator;

    @BeforeEach
    void setUp() {
        mockCoordinator = Mockito.mock(Coordinator.class);
        Mockito.when(mockCoordinator.getId()).thenReturn("coordinator-123");
        Mockito.when(mockCoordinator.getEmail()).thenReturn("coordinator@example.com");
    }

    @Test
    void testSignUp_ValidData_ReturnsCreated() throws Exception {
        // Arrange
        String email = "test@coordinator.com";
        String password = "validPassword123";
        String name = "Test Coordinator";

        String requestJson = objectMapper.writeValueAsString(new SignUpRequest(email, password, name));

        Mockito.doNothing().when(requestRequirementService).requireEmailNotDuplicate(email, null);
        Mockito.when(coordinatorService.addCoordinator(email, password, name)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/coordinator/signUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().string("Coordinator has been added to database."));

        Mockito.verify(requestRequirementService).requireEmailNotDuplicate(email, null);
        Mockito.verify(coordinatorService).addCoordinator(email, password, name);
    }

    @Test
    void testSignUp_InvalidEmail_ReturnsBadRequest() throws Exception {
        // Arrange
        String invalidEmail = "invalid-email";
        String password = "validPassword123";
        String name = "Test Coordinator";

        String requestJson = objectMapper.writeValueAsString(new SignUpRequest(invalidEmail, password, name));

        // Act & Assert
        mockMvc.perform(post("/coordinator/signUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSignUp_BlankEmail_ReturnsBadRequest() throws Exception {
        // Arrange
        String email = "";
        String password = "validPassword123";
        String name = "Test Coordinator";

        String requestJson = objectMapper.writeValueAsString(new SignUpRequest(email, password, name));

        // Act & Assert
        mockMvc.perform(post("/coordinator/signUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSignUp_BlankPassword_ReturnsBadRequest() throws Exception {
        // Arrange
        String email = "test@coordinator.com";
        String password = "";
        String name = "Test Coordinator";

        String requestJson = objectMapper.writeValueAsString(new SignUpRequest(email, password, name));

        // Act & Assert
        mockMvc.perform(post("/coordinator/signUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSignUp_BlankName_ReturnsBadRequest() throws Exception {
        // Arrange
        String email = "test@coordinator.com";
        String password = "validPassword123";
        String name = "";

        String requestJson = objectMapper.writeValueAsString(new SignUpRequest(email, password, name));

        // Act & Assert
        mockMvc.perform(post("/coordinator/signUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSignUp_EmailWithWhitespace_ReturnsBadRequest() throws Exception {
        // Arrange
        String email = "test @coordinator.com";
        String password = "validPassword123";
        String name = "Test Coordinator";

        String requestJson = objectMapper.writeValueAsString(new SignUpRequest(email, password, name));

        // Act & Assert
        mockMvc.perform(post("/coordinator/signUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSignUp_PasswordWithWhitespace_ReturnsBadRequest() throws Exception {
        // Arrange
        String email = "test@coordinator.com";
        String password = "valid Password123";
        String name = "Test Coordinator";

        String requestJson = objectMapper.writeValueAsString(new SignUpRequest(email, password, name));

        // Act & Assert
        mockMvc.perform(post("/coordinator/signUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSignUp_DuplicateEmail_ThrowsException() throws Exception {
        // Arrange
        String email = "duplicate@coordinator.com";
        String password = "validPassword123";
        String name = "Duplicate Coordinator";

        String requestJson = objectMapper.writeValueAsString(new SignUpRequest(email, password, name));

        Mockito.doThrow(new RequestException(HttpStatus.CONFLICT, "Email already exists"))
                .when(requestRequirementService).requireEmailNotDuplicate(email, null);

        // Act & Assert
        mockMvc.perform(post("/coordinator/signUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isConflict())
                .andExpect(content().string("Email already exists"));
    }

    @Test
    void testModifyPassword_ValidRequest_ReturnsOk() throws Exception {
        // Arrange
        String newPassword = "newValidPassword123";
        String requestJson = objectMapper.writeValueAsString(new ModifyPasswordRequest(newPassword));

        Mockito.when(requestRequirementService.requireUserExists(any(HttpServletRequest.class)))
                .thenReturn(mockCoordinator);
        Mockito.doNothing().when(userService).modifyPassword(newPassword, mockCoordinator);

        // Act & Assert
        mockMvc.perform(post("/coordinator/modifyPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Password has been changed."));

        Mockito.verify(requestRequirementService).requireUserExists(any(HttpServletRequest.class));
        Mockito.verify(userService).modifyPassword(newPassword, mockCoordinator);
    }

    @Test
    void testModifyPassword_BlankPassword_ReturnsBadRequest() throws Exception {
        // Arrange
        String newPassword = "";
        String requestJson = objectMapper.writeValueAsString(new ModifyPasswordRequest(newPassword));

        // Act & Assert
        mockMvc.perform(post("/coordinator/modifyPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testModifyPassword_PasswordWithWhitespace_ReturnsBadRequest() throws Exception {
        // Arrange
        String newPassword = "new ValidPassword123";
        String requestJson = objectMapper.writeValueAsString(new ModifyPasswordRequest(newPassword));

        // Act & Assert
        mockMvc.perform(post("/coordinator/modifyPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testModifyPassword_UserNotFound_ThrowsException() throws Exception {
        // Arrange
        String newPassword = "newValidPassword123";
        String requestJson = objectMapper.writeValueAsString(new ModifyPasswordRequest(newPassword));

        Mockito.when(requestRequirementService.requireUserExists(any(HttpServletRequest.class)))
                .thenThrow(new RequestException(HttpStatus.UNAUTHORIZED, "User not authorized"));

        // Act & Assert
        mockMvc.perform(post("/coordinator/modifyPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("User not authorized"));
    }

    // Helper classes for request bodies
    private record SignUpRequest(String email, String password, String name) {}
    private record ModifyPasswordRequest(String newPassword) {}

    @Configuration
    static class TestConfig {
    }
}