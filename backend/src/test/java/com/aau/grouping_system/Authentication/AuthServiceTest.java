package com.aau.grouping_system.Authentication;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;
import com.aau.grouping_system.User.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private Database mockDatabase;
    
    @Mock
    private PasswordEncoder mockPasswordEncoder;
    
    @Mock
    private DatabaseMap<Coordinator> mockCoordinators;
    
    @Mock
    private DatabaseMap<Student> mockStudents;
    
    @Mock
    private DatabaseMap<Supervisor> mockSupervisors;
    
    @Mock
    private HttpServletRequest mockRequest;
    
    @Mock
    private HttpSession mockSession;

    private AuthService authService;
    private Coordinator testCoordinator;
    private Student testStudent;
    private Supervisor testSupervisor;

    @BeforeEach
    void setUp() {
        authService = new AuthService(mockDatabase, mockPasswordEncoder);
        
        // Create test users
        testCoordinator = mock(Coordinator.class);
        testStudent = mock(Student.class);
        testSupervisor = mock(Supervisor.class);
        
        // Setup mock returns
        when(mockDatabase.getCoordinators()).thenReturn(mockCoordinators);
        when(mockDatabase.getStudents()).thenReturn(mockStudents);
        when(mockDatabase.getSupervisors()).thenReturn(mockSupervisors);
        
        when(testCoordinator.getEmail()).thenReturn("coordinator@test.com");
        when(testCoordinator.getPasswordHash()).thenReturn("hashedPassword");
        when(testStudent.getPasswordHash()).thenReturn("hashedPassword");
        when(testSupervisor.getPasswordHash()).thenReturn("hashedPassword");
    }

    @Test
    void testIsPasswordCorrect_ValidPassword_ReturnsTrue() {
        // Arrange
        String plainPassword = "testPassword";
        when(mockPasswordEncoder.matches(plainPassword, "hashedPassword")).thenReturn(true);

        // Act
        boolean result = authService.isPasswordCorrect(plainPassword, testCoordinator);

        // Assert
        assertTrue(result);
        verify(mockPasswordEncoder).matches(plainPassword, "hashedPassword");
    }

    @Test
    void testIsPasswordCorrect_InvalidPassword_ReturnsFalse() {
        // Arrange
        String plainPassword = "wrongPassword";
        when(mockPasswordEncoder.matches(plainPassword, "hashedPassword")).thenReturn(false);

        // Act
        boolean result = authService.isPasswordCorrect(plainPassword, testCoordinator);

        // Assert
        assertFalse(result);
        verify(mockPasswordEncoder).matches(plainPassword, "hashedPassword");
    }

    @Test
    void testFindByEmailOrId_CoordinatorWithValidEmail_ReturnsCoordinator() {
        // Arrange
        String email = "coordinator@test.com";
        ConcurrentHashMap<String, Coordinator> coordinatorsMap = new ConcurrentHashMap<>();
        coordinatorsMap.put("coord1", testCoordinator);
        when(mockCoordinators.getAllItems()).thenReturn(coordinatorsMap);

        // Act
        User result = authService.findByEmailOrId(email, User.Role.Coordinator);

        // Assert
        assertEquals(testCoordinator, result);
    }

    @Test
    void testFindByEmailOrId_CoordinatorWithInvalidEmail_ReturnsNull() {
        // Arrange
        String email = "nonexistent@test.com";
        ConcurrentHashMap<String, Coordinator> coordinatorsMap = new ConcurrentHashMap<>();
        coordinatorsMap.put("coord1", testCoordinator);
        when(mockCoordinators.getAllItems()).thenReturn(coordinatorsMap);

        // Act
        User result = authService.findByEmailOrId(email, User.Role.Coordinator);

        // Assert
        assertNull(result);
    }

    @Test
    void testFindByEmailOrId_StudentWithValidId_ReturnsStudent() {
        // Arrange
        String studentId = "student123";
        when(mockStudents.getItem(studentId)).thenReturn(testStudent);

        // Act
        User result = authService.findByEmailOrId(studentId, User.Role.Student);

        // Assert
        assertEquals(testStudent, result);
        verify(mockStudents).getItem(studentId);
    }

    @Test
    void testFindByEmailOrId_StudentWithInvalidId_ReturnsNull() {
        // Arrange
        String studentId = "invalidStudent";
        when(mockStudents.getItem(studentId)).thenReturn(null);

        // Act
        User result = authService.findByEmailOrId(studentId, User.Role.Student);

        // Assert
        assertNull(result);
        verify(mockStudents).getItem(studentId);
    }

    @Test
    void testFindByEmailOrId_SupervisorWithValidId_ReturnsSupervisor() {
        // Arrange
        String supervisorId = "supervisor123";
        when(mockSupervisors.getItem(supervisorId)).thenReturn(testSupervisor);

        // Act
        User result = authService.findByEmailOrId(supervisorId, User.Role.Supervisor);

        // Assert
        assertEquals(testSupervisor, result);
        verify(mockSupervisors).getItem(supervisorId);
    }

    @Test
    void testInvalidateOldSession_ExistingSession_InvalidatesSession() {
        // Arrange
        when(mockRequest.getSession(false)).thenReturn(mockSession);

        // Act
        authService.invalidateOldSession(mockRequest);

        // Assert
        verify(mockRequest).getSession(false);
        verify(mockSession).invalidate();
    }

    @Test
    void testInvalidateOldSession_NoExistingSession_DoesNothing() {
        // Arrange
        when(mockRequest.getSession(false)).thenReturn(null);

        // Act
        authService.invalidateOldSession(mockRequest);

        // Assert
        verify(mockRequest).getSession(false);
        verifyNoInteractions(mockSession);
    }

    @Test
    void testCreateNewSession_CreatesSessionWithUser() {
        // Arrange
        when(mockRequest.getSession(true)).thenReturn(mockSession);

        // Act
        authService.createNewSession(mockRequest, testCoordinator);

        // Assert
        verify(mockRequest).getSession(true);
        verify(mockSession).setMaxInactiveInterval(86400); // 1 day
        verify(mockSession).setAttribute("user", testCoordinator);
    }

    @Test
    void testHasAuthorizedRole_UserWithAuthorizedRole_ReturnsTrue() {
        // Arrange
        when(testCoordinator.getRole()).thenReturn(User.Role.Coordinator);
        User.Role[] authorizedRoles = {User.Role.Coordinator, User.Role.Supervisor};

        // Act
        Boolean result = authService.hasAuthorizedRole(testCoordinator, authorizedRoles);

        // Assert
        assertTrue(result);
    }

    @Test
    void testHasAuthorizedRole_UserWithUnauthorizedRole_ReturnsFalse() {
        // Arrange
        when(testStudent.getRole()).thenReturn(User.Role.Student);
        User.Role[] authorizedRoles = {User.Role.Coordinator, User.Role.Supervisor};

        // Act
        Boolean result = authService.hasAuthorizedRole(testStudent, authorizedRoles);

        // Assert
        assertFalse(result);
    }

    @Test
    void testGetUser_ValidSession_ReturnsUser() {
        // Arrange
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("user")).thenReturn(testCoordinator);

        // Act
        User result = authService.getUser(mockRequest);

        // Assert
        assertEquals(testCoordinator, result);
        verify(mockRequest).getSession(false);
        verify(mockSession).getAttribute("user");
    }

    @Test
    void testGetUser_NoSession_ReturnsNull() {
        // Arrange
        when(mockRequest.getSession(false)).thenReturn(null);

        // Act
        User result = authService.getUser(mockRequest);

        // Assert
        assertNull(result);
        verify(mockRequest).getSession(false);
    }

    @Test
    void testGetUser_NoUserInSession_ReturnsNull() {
        // Arrange
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("user")).thenReturn(null);

        // Act
        User result = authService.getUser(mockRequest);

        // Assert
        assertNull(result);
        verify(mockRequest).getSession(false);
        verify(mockSession).getAttribute("user");
    }
}