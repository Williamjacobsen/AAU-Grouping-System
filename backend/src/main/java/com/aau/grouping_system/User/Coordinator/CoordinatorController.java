package com.aau.grouping_system.User.Coordinator;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Authentication.AuthService;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.InputValidation.NoWhitespace;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@RestController // singleton bean
@Validated // enables method-level validation
@RequestMapping("/coordinator")
public class CoordinatorController {

	private final CoordinatorService coordinatorService;
	private final AuthService authService;

	public CoordinatorController(CoordinatorService coordinatorService, AuthService authService) {
		this.coordinatorService = coordinatorService;
		this.authService = authService;
	}

	public Coordinator RequireCoordinatorExists(HttpServletRequest servlet) {
		Coordinator coordinator = authService.getCoordinatorByUser(servlet);
		if (coordinator == null) {
			throw new RequestException(HttpStatus.UNAUTHORIZED, "User not authorized.");
		}
		return coordinator;
	}

	private record SignUpRecord(
			@NoDangerousCharacters @NotBlank @NoWhitespace @Email String email,
			@NoDangerousCharacters @NotBlank @NoWhitespace String password,
			@NoDangerousCharacters @NotBlank String name) {
	}

	@PostMapping("/signUp")
	public ResponseEntity<String> signUp(@Valid @RequestBody SignUpRecord record) {

		if (coordinatorService.isEmailDuplicate(record.email)) {
			throw new RequestException(HttpStatus.CONFLICT, "Error: Inputted email is already used by another coordinator.");
		}

		coordinatorService.addCoordinator(record.email, record.password, record.name);

		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body("Coordinator has been added to database.");
	}

	private record ModifyEmailRecord(
			@NoDangerousCharacters @NotBlank @NoWhitespace @Email String newEmail) {
	}

	@PostMapping("/modifyEmail")
	public ResponseEntity<String> modifyEmail(HttpServletRequest servlet,
			@Valid @RequestBody ModifyEmailRecord record) {

		Coordinator user = RequireCoordinatorExists(servlet);
		String coordinatorId = user.getId();

		if (coordinatorService.isEmailDuplicate(record.newEmail)) {
			throw new RequestException(HttpStatus.CONFLICT, "Inputted email is already used by another coordinator.");
		}

		coordinatorService.modifyEmail(record.newEmail, coordinatorId);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Email has been changed.");
	}

	private record ModifyPasswordRecord(
			@NoDangerousCharacters @NotBlank @NoWhitespace String newPassword) {
	}

	@PostMapping("/modifyPassword")
	public ResponseEntity<String> modifyPassword(HttpServletRequest servlet,
			@Valid @RequestBody ModifyPasswordRecord record) {

		Coordinator user = RequireCoordinatorExists(servlet);
		String coordinatorId = user.getId();

		coordinatorService.modifyPassword(record.newPassword, coordinatorId);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Password has been changed.");
	}
}
