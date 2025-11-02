package com.aau.grouping_system.User.Coordinator;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Authentication.AuthService;
import com.aau.grouping_system.Exceptions.RequestException;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

@RestController // singleton bean
@RequestMapping("/coordinator")
public class CoordinatorController {

	private final CoordinatorService coordinatorService;
	private final AuthService authService;

	public CoordinatorController(CoordinatorService coordinatorService, AuthService authService) {
		this.coordinatorService = coordinatorService;
		this.authService = authService;
	}

	public Coordinator RequireCoordinatorExists(HttpServletRequest request) {
		Coordinator coordinator = authService.getCoordinatorByUser(request);
		if (coordinator == null) {
			throw new RequestException(HttpStatus.UNAUTHORIZED, "User not authorized.");
		}
		return coordinator;
	}

	@PostMapping("/signUp")
	public ResponseEntity<String> signUp(@RequestBody Map<String, String> body) {

		String email = body.get("email");
		String password = body.get("password");
		String name = body.get("name");

		if (coordinatorService.isEmailDuplicate(email)) {
			throw new RequestException(HttpStatus.CONFLICT, "Error: Inputted email is already used by another coordinator.");
		}

		coordinatorService.addCoordinator(email, password, name);

		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body("Coordinator has been added to database.");
	}

	@PostMapping("/modifyEmail")
	public ResponseEntity<String> modifyEmail(HttpServletRequest request, @RequestBody Map<String, String> body) {

		Coordinator user = RequireCoordinatorExists(request);
		String coordinatorId = user.getId();
		String newEmail = body.get("newEmail");

		if (coordinatorService.isEmailDuplicate(newEmail)) {
			throw new RequestException(HttpStatus.CONFLICT, "Inputted email is already used by another coordinator.");
		}

		coordinatorService.modifyEmail(newEmail, coordinatorId);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Email has been changed.");
	}

	@PostMapping("/modifyPassword")
	public ResponseEntity<String> modifyPassword(HttpServletRequest request, @RequestBody Map<String, String> body) {

		Coordinator user = RequireCoordinatorExists(request);
		String coordinatorId = user.getId();
		String newPassword = body.get("newPassword");

		coordinatorService.modifyPassword(newPassword, coordinatorId);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Password has been changed.");
	}
}
