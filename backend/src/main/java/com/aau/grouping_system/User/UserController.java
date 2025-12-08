package com.aau.grouping_system.User;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.InputValidation.NoWhitespace;
import com.aau.grouping_system.Utils.RequestRequirementService;

import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@RestController
@Validated // enables method-level validation
@RequestMapping("/api/user")
public class UserController {

	private final Database db;
	private final RequestRequirementService requestRequirementService;
	private final UserService userService;

	public UserController(
			Database db,
			RequestRequirementService requestRequirementService,
			UserService userService) {
		this.db = db;
		this.requestRequirementService = requestRequirementService;
		this.userService = userService;
	}

	private record ModifyEmailRecord(
			@NoDangerousCharacters @NotBlank @NoWhitespace @Email String newEmail) {
	}

	@PostMapping("/modifyEmail")
	public ResponseEntity<String> modifyEmail(
			HttpServletRequest servlet,
			@Valid @RequestBody ModifyEmailRecord record) {

		User user = requestRequirementService.requireUserExists(servlet);

		requestRequirementService.requireEmailNotDuplicate(record.newEmail, user);

		userService.modifyEmail(record.newEmail, user);

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

		User user = requestRequirementService.requireUserExists(servlet);

		if (userService.isNameDuplicate(record.newName, user)) {
			throw new RequestException(HttpStatus.CONFLICT,
					"Name is already used by another " + user.getRole() + " in your session.");
		}

		userService.modifyName(record.newName, user);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Password has been changed.");
	}
}
