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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@Validated // enables method-level validation
@RequestMapping("/auth")
public class AuthController {

	private final AuthService service;

	public AuthController(AuthService authService) {
		this.service = authService;
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

	@PostMapping("/forgotPassword")
	public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> body) {

		try { 
		String email = body.get("email");
		if (email == null || email.isBlank()) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body("Email cannot be empty.");
		}

		User user = service.findByEmailOrId(email, User.Role.Coordinator);
		if (user == null)
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(null);

		// Sending a email
	  String resetLink = "http://localhost:3000/reset-password?email=" + email;
    String message = "Click this link to reset your password: " + resetLink;
        
        EmailService.sendEmail(
            email,
            "Password Reset Request",
            message
        );
        return ResponseEntity.ok("Reset link sent to " + email);

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("Error: " + e.getMessage());
			}
}

	// @PostMapping("/resetPassword")
	// public ResponseEntity<String> resetPassword(HttpServletRequest request) {
	// }

}