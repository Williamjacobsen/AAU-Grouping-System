package com.aau.grouping_system.User;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.InputValidation.NoWhitespace;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Coordinator.CoordinatorController;
import com.aau.grouping_system.User.Coordinator.CoordinatorService;
import com.aau.grouping_system.Utils.RequestRequirementService;

import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@RestController
@Validated // enables method-level validation
@RequestMapping("/user")
public class UserController {

	private final RequestRequirementService requestRequirementService;
	private final UserService userService;

	public UserController(
			RequestRequirementService requestRequirementService,
			UserService userService,
			CoordinatorController coordinatorController) {
		this.requestRequirementService = requestRequirementService;
		this.userService = userService;
	}

	private record ModifyPasswordRecord(
			@NoDangerousCharacters @NotBlank @NoWhitespace String newPassword) {
	}

	@PostMapping("/modifyPassword")
	public ResponseEntity<String> modifyPassword(HttpServletRequest servlet,
			@Valid @RequestBody ModifyPasswordRecord record) {

		User user = requestRequirementService.requireUserExists(servlet);

		userService.modifyPassword(record.newPassword, user);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Password has been changed.");
	}

	private record ModifyEmailRecord(
			User.Role userRole,
			@NoDangerousCharacters @NotBlank @NoWhitespace @Email String newEmail) {
	}

	@PostMapping("/modifyEmail")
	public ResponseEntity<String> modifyEmail(HttpServletRequest servlet,
			@Valid @RequestBody ModifyEmailRecord record) {

		User user = requestRequirementService.requireUserExists(servlet);

		requestRequirementService.requireEmailNotDuplicate(record.newEmail, user);

		userService.modifyEmail(record.newEmail, user);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Email has been changed.");
	}
}
