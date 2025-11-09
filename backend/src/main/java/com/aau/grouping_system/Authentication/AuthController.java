package com.aau.grouping_system.Authentication;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

import com.aau.grouping_system.EmailSystem.EmailService;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.InputValidation.NoWhitespace;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.User.Coordinator.CoordinatorService;
import com.aau.grouping_system.Utils.RequirementService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@Validated // enables method-level validation
@RequestMapping("/auth")
public class AuthController {

	private final AuthService service;
	private final EmailService emailService;

	public AuthController(AuthService authService, EmailService emailService) {
		this.service = authService;
		this.emailService = emailService;
	}

	private record SignInRecord(
			@NoDangerousCharacters @NotBlank String emailOrId,
			@NoDangerousCharacters @NotBlank @NoWhitespace String password,
			// @NotNull is enough validation for enum types.
			@NotNull User.Role role) {
	}

	@PostMapping("/signIn")
	public ResponseEntity<String> signIn(HttpServletRequest servlet, @Valid @RequestBody SignInRecord record) {

		User user = service.findByEmailOrId(record.emailOrId, record.role);
		if (user == null || !service.isPasswordCorrect(record.password, user)) {
			throw new RequestException(HttpStatus.UNAUTHORIZED, "Invalid email/id or password");
		}

		service.invalidateOldSession(servlet);
		service.createNewSession(servlet, user);

		return ResponseEntity
				.ok("Signed in, user: " + user.getEmail());
	}

	@PostMapping("/signOut")
	public ResponseEntity<String> signOut(HttpServletRequest servlet) {

		service.invalidateOldSession(servlet);
		return ResponseEntity
				.ok("Signed out"); // 200 ok
	}

	@GetMapping("/getUser")
	public ResponseEntity<User> getUser(HttpServletRequest servlet) {

		HttpSession session = servlet.getSession(false);
		if (session == null) {
			throw new RequestException(HttpStatus.UNAUTHORIZED, "Request does not contain a login session.");
		}

		User user = (User) session.getAttribute("user");
		if (user == null) {
			throw new RequestException(HttpStatus.UNAUTHORIZED, "Login session is invalid.");
		}

		return ResponseEntity
				.ok(user); // info om user returneres som JSON obj.
	}

	private record ForgotPasswordRequest(@NotBlank String email) {
	}

	@PostMapping("/forgotPassword")
	public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest record) {
		String email = record.email();
		User user = service.findByEmailOrId(email, User.Role.Coordinator);

		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("No coordinator found with the provided email.");
		}

		// Create reset token and store temporarily
		String token = java.util.UUID.randomUUID().toString();
		PasswordResetTokens.tokens.put(token, email);

		// Build reset link and email body
		String resetLink = "http://localhost:3000/reset-password?token=" + token;
		String subject = "AAU Grouping System - Password Reset";
		String body = """
				Hello %s,

				A password reset was requested for your account on the AAU Grouping System.

				To reset your password, please click the link below:
				%s

				If you didn’t request this, you can safely ignore this email.

				Best regards,
				AAU Grouping System
				""".formatted(user.getName(), resetLink);

		// Send email safely
		try {
			emailService.builder()
					.to(email.trim())
					.subject(subject)
					.text(body)
					.send();
			return ResponseEntity.ok("Password reset link sent to " + email);
		} catch (Exception e) {
			// If email fails, still return success for security reasons
			return ResponseEntity.status(HttpStatus.OK)
					.body("Password reset requested, but email could not be sent: " + e.getMessage());
		}
	}

	// Helper function
	public class PasswordResetTokens {
		public static final ConcurrentHashMap<String, String> tokens = new ConcurrentHashMap<>();
	}

	private record ResetPasswordRecord(
			@NotBlank String token,
			@NotBlank String newPassword) {
	}

	@PostMapping("/resetPassword")
	public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRecord record) {
		try {
			String token = record.token();
			String newPassword = record.newPassword();

			// Check if token exists
			String email = PasswordResetTokens.tokens.get(token);
			if (email == null) {
				return ResponseEntity
						.status(HttpStatus.UNAUTHORIZED)
						.body("Invalid or expired token.");
			}

			// Find the coordinator
			User user = service.findByEmailOrId(email, User.Role.Coordinator);
			if (user == null) {
				return ResponseEntity
						.status(HttpStatus.NOT_FOUND)
						.body("User not found.");
			}

			// Hash and update the new password
			String hashedPassword = service.encodePassword(newPassword);
			user.setPasswordHash(hashedPassword);

			// Remove token 
			PasswordResetTokens.tokens.remove(token);

			return ResponseEntity.ok("Password reset successfully.");

		} catch (Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error: " + e.getMessage());
		}
	}

}