package com.aau.grouping_system.User.Coordinator;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.InputValidation.NoWhitespace;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.User.UserService;
import com.aau.grouping_system.Utils.RequestRequirementService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@RestController // singleton bean
@Validated // enables method-level validation
@RequestMapping("/api/coordinator")
public class CoordinatorController {

	private final CoordinatorService coordinatorService;
	private final RequestRequirementService requestRequirementService;
	private final UserService userService;

	public CoordinatorController(
			CoordinatorService coordinatorService,
			RequestRequirementService requestRequirementService,
			UserService userService) {
		this.coordinatorService = coordinatorService;
		this.requestRequirementService = requestRequirementService;
		this.userService = userService;
	}

	private record SignUpRecord(
			@NoDangerousCharacters @NotBlank @NoWhitespace @Email String email,
			@NoDangerousCharacters @NotBlank @NoWhitespace String password,
			@NoDangerousCharacters @NotBlank String name) {
	}

	@PostMapping("/signUp")
	public ResponseEntity<String> signUp(@Valid @RequestBody SignUpRecord record) {

		requestRequirementService.requireEmailNotDuplicate(record.email, null);

		coordinatorService.addCoordinator(record.email, record.password, record.name);

		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body("Coordinator has been added to database.");
	}

	private record ModifyPasswordRecord(
			@NoDangerousCharacters @NotBlank @NoWhitespace String newPassword) {
	}

	@PostMapping("/modifyPassword")
	public ResponseEntity<String> modifyPassword(
			HttpServletRequest servlet,
			@Valid @RequestBody ModifyPasswordRecord record) {

		Coordinator coordinatorUser = requestRequirementService.requireUserCoordinatorExists(servlet);

		userService.modifyPassword(record.newPassword, coordinatorUser);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Password has been changed.");
	}

	private record ModifyEmailRecord(
			@NoDangerousCharacters @NotBlank @NoWhitespace @Email String newEmail) {
	}

	@PostMapping("/modifyEmail")
	public ResponseEntity<String> modifyEmail(
			HttpServletRequest servlet,
			@Valid @RequestBody ModifyEmailRecord record) {

		Coordinator coordinatorUser = requestRequirementService.requireUserCoordinatorExists(servlet);

		requestRequirementService.requireEmailNotDuplicate(record.newEmail, coordinatorUser);

		userService.modifyEmail(record.newEmail, coordinatorUser);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Email has been changed.");
	}

	private record ModifyNameRecord(
			@NoDangerousCharacters @NotBlank String newName) {
	}

	@PostMapping("/modifyName")
	public ResponseEntity<String> modifyName(
			HttpServletRequest servlet,
			@Valid @RequestBody ModifyNameRecord record) {

		Coordinator coordinatorUser = requestRequirementService.requireUserCoordinatorExists(servlet);

		userService.modifyName(record.newName, coordinatorUser);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Password has been changed.");
	}

}
