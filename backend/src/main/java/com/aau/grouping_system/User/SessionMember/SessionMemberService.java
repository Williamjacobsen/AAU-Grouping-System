package com.aau.grouping_system.User.SessionMember;

import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.EmailSystem.EmailService;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.UserService;

@Service
public class SessionMemberService {

	private final Database db;
	private final EmailService emailService;
	private final UserService userService;

	public SessionMemberService(
			Database db,
			EmailService emailService,
			UserService userService) {
		this.db = db;
		this.emailService = emailService;
		this.userService = userService;
	}

	private String generateNewPassword() {
		return UUID.randomUUID().toString();
	}

	public void applyAndEmailNewPassword(Session session, SessionMember sessionMember) {

		String newPassword = generateNewPassword();
		userService.modifyPassword(newPassword, sessionMember);
		System.out.println("-----" +
				"\nName: " + sessionMember.getName() +
				"\nID: " + sessionMember.getId() +
				"\nNew password: " + newPassword);

		String subject = "Grouping Formation System - Your Login Code";
		String body = String.format("""
				Hello %s,

				Your profile password has been updated by your group formation session coordinator.

				Your session name:
				"%s"

				Your profile ID:
				%s

				Your profile password:
				%s

				Please use your ID and password to sign in to the group formation session via the following link:
				http://localhost:3000/sign-in

				""", sessionMember.getName(), session.getName(), sessionMember.getId(), newPassword);

		try {
			emailService.builder()
					.to(sessionMember.getEmail())
					.subject(subject)
					.text(body)
					.send();
			sessionMember.setHasBeenSentPassword(true);
		} catch (Exception exception) {
			throw new RequestException(
					HttpStatus.INTERNAL_SERVER_ERROR,
					"Failed to send email to address '" + sessionMember.getEmail() + "': " + exception.getMessage());
		}
	}

	public void applyAndEmailNewPasswords(Session session, CopyOnWriteArrayList<? extends SessionMember> sessionMembers) {

		// For development purposes, we store any exception messages instead of just
		// immediately quitting out of the applyAndEmailNewPassword() loop. Without our
		// email client set up, applyAndEmailNewPassword() will always fail to send an
		// email. So, if we quit immediately, we would only be able to send a new
		// password to the first SessionMember on our list of SessionsMembers, but for
		// testing purposes we want to cycle through everyone and give them a new
		// password, despite failing to send an email.

		System.out.println("--- Emailing login codes ---");
		CopyOnWriteArrayList<String> exceptionMessages = new CopyOnWriteArrayList<>();
		for (SessionMember sessionMember : sessionMembers) {
			try {
				applyAndEmailNewPassword(session, sessionMember);
			} catch (Exception exception) {
				exceptionMessages.add(exception.getMessage());
			}
		}

		if (exceptionMessages.size() != 0) {
			String concatenatedExceptionMessages = "";
			for (String message : exceptionMessages) {
				concatenatedExceptionMessages += message + ".\n";
			}
			throw new RequestException(HttpStatus.INTERNAL_SERVER_ERROR, concatenatedExceptionMessages);
		}
	}
}
