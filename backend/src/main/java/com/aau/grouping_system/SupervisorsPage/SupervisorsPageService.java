package com.aau.grouping_system.SupervisorsPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.InputValidation.NoWhitespace;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.SessionMember.SessionMemberService;
import com.aau.grouping_system.User.SessionMember.Supervisor.Supervisor;
import com.aau.grouping_system.User.UserService;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Service
public class SupervisorsPageService {

	private final Database db;
	private final SessionMemberService sessionMemberService;
	private final UserService userService;

	public SupervisorsPageService(
			Database db,
			SessionMemberService sessionMemberService,
			UserService userService) {
		this.db = db;
		this.sessionMemberService = sessionMemberService;
		this.userService = userService;
	}



	// Get supervisor data
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getFormattedSupervisors(Session session) {
		CopyOnWriteArrayList<Supervisor> supervisors = (CopyOnWriteArrayList<Supervisor>) session.getSupervisors().getItems(db);

		return supervisors.stream()
				.map(supervisor -> {
					Map<String, Object> supervisorMap = new HashMap<>();
					supervisorMap.put("id", supervisor.getId());
					supervisorMap.put("email", supervisor.getEmail());
					supervisorMap.put("name", supervisor.getName());
					supervisorMap.put("maxGroups", supervisor.getMaxGroups());
					return supervisorMap;
				})
				.collect(Collectors.toList());
	}

	public record AddSupervisorRequest(
			@NoDangerousCharacters @NotBlank @NoWhitespace @Email String email) {
	}

	// Add new supervisor to session
	@SuppressWarnings("unchecked")
	public String addSupervisor(Session session, AddSupervisorRequest request) {
		CopyOnWriteArrayList<Supervisor> existingSupervisors = (CopyOnWriteArrayList<Supervisor>) session.getSupervisors().getItems(db);

		// Check if supervisor with email is already in session
		boolean supervisorExists = existingSupervisors.stream()
				.anyMatch(supervisor -> supervisor.getEmail().equals(request.email.trim()));

		if (supervisorExists) {
			throw new RequestException(HttpStatus.CONFLICT,
					"Supervisor with this email already exists in this session");
		}

		// Generate password and create supervisor
		String password = UUID.randomUUID().toString();

		Supervisor newSupervisor = db.getSupervisors().addItem(
				session.getSupervisors(),
				new Supervisor(
						request.email.trim(),
						request.email.trim().split("@")[0], // Use email as default name
						session));

		userService.modifyPassword(password, newSupervisor);

		// Send password via email
		try {
			sessionMemberService.applyAndEmailNewPassword(session, newSupervisor);
			return "Supervisor added successfully and password sent via email";
		} catch (Exception e) {
			return "Supervisor added successfully, but email failed to send: " + e.getMessage();
		}
	}

	// Remove supervisor
	public void removeSupervisor(String supervisorId) {
		// Remove supervisor from database
		db.getSupervisors().cascadeRemoveItem(db, supervisorId);
	}
}