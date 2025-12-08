package com.aau.grouping_system.User.Student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseItemChildGroup;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.SessionMember.Student.Student;
import com.aau.grouping_system.User.SessionMember.Student.StudentQuestionnaire;
import com.aau.grouping_system.User.SessionMember.Student.StudentService;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

	@Mock
	private Database mockDatabase;

	@Mock
	private Session mockSession;

	@Mock
	private DatabaseItemChildGroup mockStudentsGroup;

	@Mock
	private DatabaseMap<Student> mockStudentsMap;

	private StudentService studentService;

	@BeforeEach
	void setUp() {
		studentService = new StudentService(mockDatabase);

		// Setup mock returns
		lenient().when(mockSession.getStudents()).thenReturn(mockStudentsGroup);
		lenient().when(mockSession.getId()).thenReturn("test-session-id");
		lenient().when(mockDatabase.getStudents()).thenReturn(mockStudentsMap);
	}

	@Test
	void testAddStudent_ValidData_ReturnsStudent() {
		// Arrange
		String email = "student@test.com";
		String name = "Test Student";

		// Create a mock student
		Student mockStudent = mock(Student.class);

		// Mock the addItem method call
		when(mockStudentsMap.addItem(
				any(), any(), any(Student.class)))
				.thenReturn(mockStudent);

		// Act
		Student result = studentService.addStudent(mockSession, email, name);

		// Assert
		assertNotNull(result);
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

		// Act
		studentService.applyQuestionnaireAnswers(student, questionnaire);

		// Assert
		verify(student).setQuestionnaire(questionnaire);
		
		assertEquals(-1, questionnaire.getDesiredGroupSizeMin());
		assertEquals(-1, questionnaire.getDesiredGroupSizeMax());
		assertEquals(StudentQuestionnaire.WorkLocation.NoPreference, questionnaire.getDesiredWorkLocation());
		assertEquals(StudentQuestionnaire.WorkStyle.NoPreference, questionnaire.getDesiredWorkStyle());
		assertEquals("", questionnaire.getPersonalSkills());
		assertEquals("", questionnaire.getSpecialNeeds());
		assertEquals("", questionnaire.getAcademicInterests());
		assertEquals("", questionnaire.getComments());
	}
}