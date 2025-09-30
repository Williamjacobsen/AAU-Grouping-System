package com.aau.grouping_system.User.Coordinator;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.aau.grouping_system.Database.Database;

@RestController // singleton bean
@RequestMapping("/coordinator")
public class CoordinatorController {

	private final Database db;
	private final PasswordEncoder passwordEncoder;

	public CoordinatorController(Database db, PasswordEncoder passwordEncoder) {
		this.db = db;
		this.passwordEncoder = passwordEncoder;
	}

	// requests

	private record SignUpRequest(String email, String password, String name) { }
	@PostMapping("/signUp")
	public ResponseEntity<String> signUp(@RequestBody SignUpRequest request) {
		if (isEmailDuplicate(request.email())) {
			return ResponseEntity
					.status(HttpStatus.CONFLICT)
      		.body("Error: Inputted email is already used by another coordinator.");
		}
		String passwordHash = passwordEncoder.encode(request.password());
		Coordinator newCoordinator = new Coordinator(request.email(), passwordHash, request.name());
		db.saveCoordinator(newCoordinator);
		System.out.println("Added profile: " + "Email: " + request.email() + ", Password hash:" + passwordHash);
		return ResponseEntity
			.status(HttpStatus.CREATED)
      .body("Coordinator has been added to database.");
	}

	private record ModifyEmailRequest(Coordinator coordinator, String newEmail) { }
	@PostMapping("/modifyEmail")
	public ResponseEntity<String> modifyEmail(@RequestBody ModifyEmailRequest request) {
		// todo: Credentials validation
		if (isEmailDuplicate(request.newEmail())) {
			return ResponseEntity
					.status(HttpStatus.CONFLICT)
      		.body("Error: Inputted email is already used by another coordinator.");
		}
		request.coordinator().setEmail(request.newEmail());
		return ResponseEntity
			.status(HttpStatus.OK)
      .body("Coordinator has been added to database.");
	}

	private record ModifyPasswordRequest(Coordinator coordinator, String newPassword) { }
	@PostMapping("/modifyPassword")
	public void modifyPassword(@RequestBody ModifyPasswordRequest request) {
		// todo: Credentials validation
		String passwordHash = passwordEncoder.encode(request.newPassword());
		request.coordinator().setPasswordHash(passwordHash);
	}

	// helpers

	private boolean isEmailDuplicate(String email) {
		for (Coordinator existingCoordinator : db.getAllCoordinators().values()) {
			if (existingCoordinator.getEmail().equals(email)) {
				return true;
			}
		}
		return false;
	}
}
