package com.aau.grouping_system.User.Student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseItemChildGroup;
import com.aau.grouping_system.Session.Session;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private Database mockDatabase;
    
    @Mock
    private PasswordEncoder mockPasswordEncoder;
    
    @Mock
    private Session mockSession;
    
    @Mock
    private DatabaseItemChildGroup mockStudentsGroup;

    private StudentService studentService;

    @BeforeEach
    void setUp() {
        studentService = new StudentService(mockDatabase, mockPasswordEncoder);
        when(mockSession.getStudents()).thenReturn(mockStudentsGroup);
    }

    @Test
    void testAddStudent_ValidData_ReturnsStudent() {
        // Arrange
        String email = "student@test.com";
        String password = "plainPassword";
        String name = "Test Student";
        String hashedPassword = "hashedPassword123";
        
        when(mockPasswordEncoder.encode(password)).thenReturn(hashedPassword);

        // Act
        Student result = studentService.addStudent(mockSession, email, password, name);

        // Assert
        assertNotNull(result);
        verify(mockPasswordEncoder).encode(password);
    }

    @Test
    void testAddStudent_EmptyEmail_StillCreatesStudent() {
        // Arrange
        String email = "";
        String password = "plainPassword";
        String name = "Test Student";
        String hashedPassword = "hashedPassword123";
        
        when(mockPasswordEncoder.encode(password)).thenReturn(hashedPassword);

        // Act
        Student result = studentService.addStudent(mockSession, email, password, name);

        // Assert
        assertNotNull(result);
        verify(mockPasswordEncoder).encode(password);
    }

    @Test
    void testAddStudent_NullPassword_HandlesGracefully() {
        // Arrange
        String email = "student@test.com";
        String password = null;
        String name = "Test Student";
        
        when(mockPasswordEncoder.encode(password)).thenThrow(new IllegalArgumentException("Password cannot be null"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            studentService.addStudent(mockSession, email, password, name);
        });
        
        verify(mockPasswordEncoder).encode(password);
    }

    @Test
    void testApplyQuestionnaireAnswers_ValidQuestionnaire_UpdatesStudent() {
        // Arrange
        Student student = mock(Student.class);
        Student.Questionnaire questionnaire = new Student.Questionnaire();
        questionnaire.desiredGroupSizeMin = 2;
        questionnaire.desiredGroupSizeMax = 4;
        questionnaire.desiredWorkLocation = Student.WorkLocation.Located;
        questionnaire.desiredWorkStyle = Student.WorkStyle.Together;
        questionnaire.personalSkills = "Java, Spring Boot";
        questionnaire.academicInterests = "Software Engineering";
        questionnaire.comments = "Test comment";

        // Act
        studentService.applyQuestionnaireAnswers(student, questionnaire);

        // Assert
        verify(student).setQuestionnaire(questionnaire);
    }

    @Test
    void testApplyQuestionnaireAnswers_NullQuestionnaire_UpdatesStudent() {
        // Arrange
        Student student = mock(Student.class);
        Student.Questionnaire questionnaire = null;

        // Act
        studentService.applyQuestionnaireAnswers(student, questionnaire);

        // Assert
        verify(student).setQuestionnaire(questionnaire);
    }

    @Test
    void testApplyQuestionnaireAnswers_EmptyQuestionnaire_UpdatesStudent() {
        // Arrange
        Student student = mock(Student.class);
        Student.Questionnaire questionnaire = new Student.Questionnaire();
        // Leave questionnaire with default values

        // Act
        studentService.applyQuestionnaireAnswers(student, questionnaire);

        // Assert
        verify(student).setQuestionnaire(questionnaire);
        // Verify default values are maintained
        assertEquals(-1, questionnaire.desiredGroupSizeMin);
        assertEquals(-1, questionnaire.desiredGroupSizeMax);
        assertEquals(Student.WorkLocation.NoPreference, questionnaire.desiredWorkLocation);
        assertEquals(Student.WorkStyle.NoPreference, questionnaire.desiredWorkStyle);
        assertEquals("", questionnaire.personalSkills);
        assertEquals("", questionnaire.specialNeeds);
        assertEquals("", questionnaire.academicInterests);
        assertEquals("", questionnaire.comments);
    }

    @Test
    void testPasswordEncoding_VerifyEncryptionCalled() {
        // Arrange
        String email = "student@test.com";
        String password = "testPassword123";
        String name = "Test Student";
        String expectedHash = "encryptedHash456";
        
        when(mockPasswordEncoder.encode(password)).thenReturn(expectedHash);

        // Act
        Student result = studentService.addStudent(mockSession, email, password, name);

        // Assert
        verify(mockPasswordEncoder, times(1)).encode(password);
        verify(mockPasswordEncoder, never()).encode(argThat(arg -> !password.equals(arg)));
    }
}