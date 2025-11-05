package com.aau.grouping_system.User.Coordinator;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Authentication.AuthService;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.InputValidation.NoWhitespace;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

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

	public Coordinator RequireCoordinatorExists(HttpServletRequest request) {
		Coordinator coordinator = authService.getCoordinatorByUser(request);
		if (coordinator == null) {
			throw new RequestException(HttpStatus.UNAUTHORIZED, "User not authorized.");
		}
		return coordinator;
	}

	private record SignUpRequest(
			@NoDangerousCharacters @NotBlank @NoWhitespace @Email String email,
			@NoDangerousCharacters @NotBlank @NoWhitespace String password,
			@NoDangerousCharacters @NotBlank String name) {
	}

	@PostMapping("/signUp")
	public ResponseEntity<String> signUp(@Valid @RequestBody SignUpRequest body) {

		if (coordinatorService.isEmailDuplicate(body.email)) {
			throw new RequestException(HttpStatus.CONFLICT, "Error: Inputted email is already used by another coordinator.");
		}

		coordinatorService.addCoordinator(body.email, body.password, body.name);

		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body("Coordinator has been added to database.");
	}

	private record ModifyEmailRequest(
			@NoDangerousCharacters @NotBlank @NoWhitespace @Email String newEmail) {
	}

	@PostMapping("/modifyEmail")
	public ResponseEntity<String> modifyEmail(HttpServletRequest request, @Valid @RequestBody ModifyEmailRequest body) {

		Coordinator user = RequireCoordinatorExists(request);
		String coordinatorId = user.getId();

		if (coordinatorService.isEmailDuplicate(body.newEmail)) {
			throw new RequestException(HttpStatus.CONFLICT, "Inputted email is already used by another coordinator.");
		}

		coordinatorService.modifyEmail(body.newEmail, coordinatorId);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Email has been changed.");
	}

	private record ModifyPasswordRequest(
			@NoDangerousCharacters @NotBlank @NoWhitespace String newPassword) {
	}

	@PostMapping("/modifyPassword")
	public ResponseEntity<String> modifyPassword(HttpServletRequest request,
			@Valid @RequestBody ModifyPasswordRequest body) {

		Coordinator user = RequireCoordinatorExists(request);
		String coordinatorId = user.getId();

		coordinatorService.modifyPassword(body.newPassword, coordinatorId);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Password has been changed.");
	}
}
