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
import com.aau.grouping_system.EmailSystem.EmailService;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.InputValidation.NoWhitespace;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Supervisor.Supervisor;
import com.aau.grouping_system.User.UserService;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Service
public class SupervisorsPageService {

	private final Database db;
	private final EmailService emailService;
	private final UserService userService;

	public SupervisorsPageService(
			Database db,
			EmailService emailService,
			UserService userService) {
		this.db = db;
		this.emailService = emailService;
		this.userService = userService;
	}

	// Get all supervisors for session
	@SuppressWarnings("unchecked")
	public CopyOnWriteArrayList<Supervisor> getSessionSupervisors(Session session) {
		return (CopyOnWriteArrayList<Supervisor>) session.getSupervisors().getItems(db);
	}

	// Get supervisor data
	public List<Map<String, Object>> getFormattedSupervisors(Session session) {
		CopyOnWriteArrayList<Supervisor> supervisors = getSessionSupervisors(session);

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
	public String addSupervisor(Session session, AddSupervisorRequest request) {
		CopyOnWriteArrayList<Supervisor> existingSupervisors = getSessionSupervisors(session);

		// Check if supervisor with email is already in session
		boolean supervisorExists = existingSupervisors.stream()
				.anyMatch(supervisor -> supervisor.getEmail().equals(request.email.trim()));

		if (supervisorExists) {
			throw new RequestException(HttpStatus.CONFLICT,
					"Supervisor with this email already exists in this session");
		}

		// Generate password and create supervisor
		String password = UUID.randomUUID().toString();

		Supervisor newSupervisor = new Supervisor(
				db,
				session.getSupervisors(),
				request.email.trim(),
				"placeholder",
				request.email.trim().split("@")[0], // Use email as default name
				session);

		userService.modifyPassword(password, newSupervisor);

		// Send password via email
		try {
			sendCredentialsEmail(request.email.trim(), session.getName(), newSupervisor.getId(), password, false);
			return "Supervisor added successfully and password sent via email";
		} catch (Exception e) {
			return "Supervisor added successfully, but email failed to send: " + e.getMessage();
		}
	}

	// Remove supervisor
	public void removeSupervisor(String supervisorId) {
		// Remove supervisor from database
		db.getSupervisors().cascadeRemove(db, supervisorId);
	}

	// Send email to supervisor
	private void sendCredentialsEmail(String email, String sessionName, String supervisorId, String password,
			boolean isNewPassword) throws Exception {
		String subject = isNewPassword ? "AAU Grouping System - New Password" : "AAU Grouping System - Supervisor Access";
		String actionText = isNewPassword ? "Your password for the AAU Grouping System has been reset for"
				: "You have been added as a supervisor for the";

		String body = """
				Hello,

				%s session: %s

				Your login credentials are:
				ID: %s
				Password: %s

				Please use your ID and password to access the AAU Grouping System.

				Best regards,
				AAU Grouping System""".formatted(actionText, sessionName, supervisorId, password);

		emailService.builder()
				.to(email)
				.subject(subject)
				.text(body)
				.send();
	}
}