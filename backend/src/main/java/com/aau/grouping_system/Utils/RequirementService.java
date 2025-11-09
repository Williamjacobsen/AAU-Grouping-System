package com.aau.grouping_system.Utils;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.aau.grouping_system.Authentication.AuthService;
import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.Session.SessionService;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;

import jakarta.servlet.http.HttpServletRequest;

@Service
/// Service for HTTP request requirements, for example requiring that a specific
/// coordinator exists in the database.
public class RequirementService {

	private final Database db;
	private final AuthService authService;
	private final SessionService sessionService;

	public RequirementService(Database db,
			AuthService authService,
			SessionService sessionService) {
		this.db = db;
		this.authService = authService;
		this.sessionService = sessionService;
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

}
