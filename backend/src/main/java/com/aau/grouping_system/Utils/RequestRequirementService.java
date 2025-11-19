package com.aau.grouping_system.Utils;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.aau.grouping_system.Authentication.AuthService;
import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.Session.SessionService;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.User.UserService;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;

import jakarta.servlet.http.HttpServletRequest;

@Service
/// Service for HTTP request requirements, for example requiring that a specific
/// coordinator exists in the database.
public class RequestRequirementService {

	private final Database db;
	private final AuthService authService;
	private final SessionService sessionService;
	private final UserService userService;

	public RequestRequirementService(Database db,
			AuthService authService,
			SessionService sessionService,
			UserService userService) {
		this.db = db;
		this.authService = authService;
		this.sessionService = sessionService;
		this.userService = userService;
	}

	// Require X to exist

	public User requireUserExists(HttpServletRequest servlet) {
		User user = authService.getUser(servlet);
		if (user == null) {
			throw new RequestException(HttpStatus.UNAUTHORIZED, "User not authorized");
		}
		return user;
	}

	public Coordinator requireUserCoordinatorExists(HttpServletRequest servlet) {
		Coordinator coordinator = authService.getCoordinatorByUser(servlet);
		if (coordinator == null) {
			throw new RequestException(HttpStatus.UNAUTHORIZED, "User not authorized as a valid coordinator");
		}
		return coordinator;
	}

	public Student requireUserStudentExists(HttpServletRequest servlet) {
		Student student = authService.getStudentByUser(servlet);
		if (student == null) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "User not authorized as a valid student");
		}
		return student;
	}

	public Student requireStudentExists(String studentId) {
		Student student = db.getStudents().getItem(studentId);
		if (student == null) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Student not found");
		}
		return student;
	}

	public Session requireSessionExists(String sessionId) {
		Session session = db.getSessions().getItem(sessionId);
		if (session == null) {
			throw new RequestException(HttpStatus.NOT_FOUND, "Session not found");
		}
		return session;
	}

	public Group requireGroupExists(String groupId) {
		Group group = db.getGroups().getItem(groupId);
		if (group == null) {
			throw new RequestException(HttpStatus.NOT_FOUND, "Group not found");
		}
		return group;
	}

	public Supervisor requireSupervisorExists(String supervisorId) {
		Supervisor supervisor = db.getSupervisors().getItem(supervisorId);
		if (supervisor == null) {
			throw new RequestException(HttpStatus.NOT_FOUND, "Supervisor not found");
		}
		return supervisor;
	}

	public Project requireProjectExists(String projectId) {
		Project project = db.getProjects().getItem(projectId);
		if (project == null) {
			throw new RequestException(HttpStatus.NOT_FOUND, "Project not found");
		}
		return project;
	}

	// Require authorization

	public void requireUserIsAuthorizedSession(String sessionId, User user) {
		if (!sessionService.isUserAuthorizedSession(sessionId, user)) {
			throw new RequestException(HttpStatus.UNAUTHORIZED, "User is not authorized session");
		}
	}

	public void requireCoordinatorIsAuthorizedSession(String sessionId, Coordinator coordinator) {
		if (!sessionService.isUserAuthorizedSession(sessionId, coordinator)) {
			throw new RequestException(HttpStatus.FORBIDDEN, "Coordinator user is not authorized session");
		}
	}

	// Require user stuff

	public void requireEmailNotDuplicate(String newEmail, User user) {
		if (userService.isEmailDuplicate(newEmail, user)) {
			throw new RequestException(HttpStatus.CONFLICT, "Inputted email is already used by another " + user.getRole());
		}
	}

	// miscellaneous

	public void requireQuestionnaireDeadlineNotExceeded(Session session) {
		if (sessionService.isQuestionnaireDeadlineExceeded(session)) {
			throw new RequestException(HttpStatus.UNAUTHORIZED, "Questionnaire submission deadline exceeded.");
		}
	}

}
