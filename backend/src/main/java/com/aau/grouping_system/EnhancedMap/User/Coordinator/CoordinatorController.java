package com.aau.grouping_system.EnhancedMap.User.Coordinator;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.aau.grouping_system.Database.Database;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController // singleton bean
@RequestMapping("/coordinator")
public class CoordinatorController {

	private final CoordinatorService service;

	// constructors

	public CoordinatorController(CoordinatorService coordinatorService, Database db, PasswordEncoder passwordEncoder) {
		this.service = coordinatorService;
	}

	// requests

	@PostMapping("/signUp")
	public ResponseEntity<String> signUp(@RequestBody Map<String, String> request) {

		String email = request.get("email");
		String password = request.get("password");
		String name = request.get("name");

		if (service.isEmailDuplicate(email)) {
			return ResponseEntity
					.status(HttpStatus.CONFLICT)
					.body("Error: Inputted email is already used by another coordinator.");
		}

		service.addCoordinator(email, password, name);

		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body("Coordinator has been added to database.");
	}

	@PostMapping("/modifyEmail")
	public ResponseEntity<String> modifyEmail(@RequestBody Map<String, String> request) {
		// todo: Credentials validation

		Integer coordinatorId = Integer.parseInt(request.get("coordinatorId"));
		String newEmail = request.get("newEmail");

		if (service.isEmailDuplicate(newEmail)) {
			return ResponseEntity
					.status(HttpStatus.CONFLICT)
					.body("Error: Inputted email is already used by another coordinator.");
		}

		service.modifyEmail(newEmail, coordinatorId);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Email has been changed.");
	}

	@PostMapping("/modifyPassword")
	public ResponseEntity<String> modifyPassword(@RequestBody Map<String, String> request) {
		// todo: Credentials validation

		Integer coordinatorId = Integer.parseInt(request.get("coordinatorId"));
		String newPassword = request.get("newPassword");

		service.modifyPassword(newPassword, coordinatorId);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Password has been changed.");
	}

	// TODO: Remove this
	@GetMapping("/test")
	public ResponseEntity<String> test() {
		service.test();

		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Test successful.");
	}
}
