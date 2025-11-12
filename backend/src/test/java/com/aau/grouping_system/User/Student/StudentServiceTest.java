package com.aau.grouping_system.User.Student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseItemChildGroup;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Student.StudentQuestionnaire;

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
	
	@Mock
	private DatabaseMap<Student> mockStudentsMap;

	private StudentService studentService;

	@BeforeEach
	void setUp() {
		studentService = new StudentService(mockDatabase, mockPasswordEncoder);
		
		// Setup mock returns
		lenient().when(mockSession.getStudents()).thenReturn(mockStudentsGroup);
		lenient().when(mockSession.getId()).thenReturn("test-session-id");
		lenient().when(mockDatabase.getStudents()).thenReturn(mockStudentsMap);
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
		StudentQuestionnaire questionnaire = new StudentQuestionnaire();
		questionnaire.setDesiredGroupSizeMin(2);
		questionnaire.setDesiredGroupSizeMax(4);
		questionnaire.setDesiredWorkLocation(StudentQuestionnaire.WorkLocation.Located);
		questionnaire.setDesiredWorkStyle(StudentQuestionnaire.WorkStyle.Together);
		questionnaire.setPersonalSkills("Java, Spring Boot");
		questionnaire.setAcademicInterests("Software Engineering");
		questionnaire.setComments("Test comment");

		// Act
		studentService.applyQuestionnaireAnswers(student, questionnaire);

		// Assert
		verify(student).setQuestionnaire(questionnaire);
	}

	@Test
	void testApplyQuestionnaireAnswers_NullQuestionnaire_UpdatesStudent() {
		// Arrange
		Student student = mock(Student.class);
		StudentQuestionnaire questionnaire = null;

		// Act
		studentService.applyQuestionnaireAnswers(student, questionnaire);

		// Assert
		verify(student).setQuestionnaire(questionnaire);
	}

	@Test
	void testApplyQuestionnaireAnswers_EmptyQuestionnaire_UpdatesStudent() {
		// Arrange
		Student student = mock(Student.class);
		StudentQuestionnaire questionnaire = new StudentQuestionnaire();
		// Leave questionnaire with default values

		// Act
		studentService.applyQuestionnaireAnswers(student, questionnaire);

		// Assert
		verify(student).setQuestionnaire(questionnaire);
		// Verify default values are maintained
		assertEquals(-1, questionnaire.getDesiredGroupSizeMin());
		assertEquals(-1, questionnaire.getDesiredGroupSizeMax());
		assertEquals(StudentQuestionnaire.WorkLocation.NoPreference, questionnaire.getDesiredWorkLocation());
		assertEquals(StudentQuestionnaire.WorkStyle.NoPreference, questionnaire.getDesiredWorkStyle());
		assertEquals("", questionnaire.getPersonalSkills());
		assertEquals("", questionnaire.getSpecialNeeds());
		assertEquals("", questionnaire.getAcademicInterests());
		assertEquals("", questionnaire.getComments());
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