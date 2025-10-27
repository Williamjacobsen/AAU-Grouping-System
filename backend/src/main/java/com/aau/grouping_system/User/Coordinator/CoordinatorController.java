package com.aau.grouping_system.User.Coordinator;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

@RestController // singleton bean
@RequestMapping("/coordinator")
public class CoordinatorController {

	private final CoordinatorService service;

	public CoordinatorController(CoordinatorService coordinatorService) {
		this.service = coordinatorService;
	}

	@PostMapping("/signUp")
	public ResponseEntity<String> signUp(@RequestBody Map<String, String> body) {

		String email = body.get("email");
		String password = body.get("password");
		String name = body.get("name");

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
	public ResponseEntity<String> modifyEmail(HttpServletRequest request, @RequestBody Map<String, String> body) {

		Coordinator user = (Coordinator) request.getSession().getAttribute("user");
		if (user == null) {
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED)
					.body("Not logged in");
		}
		String coordinatorId = user.getId();
		String newEmail = body.get("newEmail");

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
	public ResponseEntity<String> modifyPassword(HttpServletRequest request, @RequestBody Map<String, String> body) {

		Coordinator user = (Coordinator) request.getSession().getAttribute("user");
		if (user == null) {
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED)
					.body("Not logged in");
		}
		String coordinatorId = user.getId();
		String newPassword = body.get("newPassword");

		service.modifyPassword(newPassword, coordinatorId);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Password has been changed.");
	}
}
