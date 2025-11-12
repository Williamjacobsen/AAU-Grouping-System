package com.aau.grouping_system.User;

import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.EmailSystem.EmailService;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.Session.Session;

@Service
public class UserService {

	private final Database db;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;

	public UserService(
			Database db,
			PasswordEncoder passwordEncoder,
			EmailService emailService) {
		this.db = db;
		this.passwordEncoder = passwordEncoder;
		this.emailService = emailService;
	}

	public User getUser(User.Role role, String id) {
		switch (role) {
			case User.Role.Coordinator:
				return db.getCoordinators().getItem(id);
			case User.Role.Supervisor:
				return db.getSupervisors().getItem(id);
			case User.Role.Student:
				return db.getStudents().getItem(id);
			default:
				return null;
		}
	}

	public void modifyEmail(String newEmail, User user) {
		user.setEmail(newEmail);
	}

	/// Note: Deactivates login code upon being invoked.
	public void modifyPassword(String newPassword, User user) {
		String passwordHash = passwordEncoder.encode(newPassword);
		user.setPasswordHash(passwordHash);
		user.setLoginCode(null); // Ensure login code is not regarded as activated
	}

	public String ensureLoginCodeIsActivated(User user) {
		String loginCode = user.getLoginCode();

		if (loginCode == null) {
			loginCode = generateLoginCode();
			modifyPassword(loginCode, user);
			user.setLoginCode(loginCode);
		}

		return loginCode;
	}

	private String generateLoginCode() {
		return UUID.randomUUID().toString();
	}

	public void applyAndEmailLoginCodes(Session session, CopyOnWriteArrayList<User> users) {

		System.out.println("--- Emailing login codes ---");
		for (User user : users) {

			String loginCode = ensureLoginCodeIsActivated(user);
			System.out.println("User id:" + user.getId() + "\nLogin code: " + loginCode);

			String subject = "Grouping Formation System - Your Login Code";
			String body = String.format("""
					Hello %s,

					Your login code for the session "%s" is:
					%s

					Use this code to log in to the group formation session.
					""", user.getName(), session.getName(), loginCode);

			try {
				emailService.builder()
						.to(user.getEmail())
						.subject(subject)
						.text(body)
						.send();
			} catch (Exception exception) {
				throw new RequestException(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"Failed to send email to address '" + user.getEmail() + "': " + exception.getMessage());
			}
		}
	}

}
