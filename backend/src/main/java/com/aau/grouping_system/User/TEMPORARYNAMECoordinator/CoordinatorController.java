package com.aau.grouping_system.User.Coordinator;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Database.Database;

@RestController // singleton bean
@RequestMapping("/coordinator")
public class CoordinatorController {

	private final Database db;

	public CoordinatorController(Database db) {
		this.db = db;
	}

	// requests

	private record SignUpRequest(String email, String password, String name) { }
	@PostMapping("/signUp")
	public void signUp(@RequestBody SignUpRequest request) {
		// todo: check that no other coordinator has the same email
		// todo: hash password
		String passwordHash = request.password();
		Coordinator newCoordinator = new Coordinator(request.email(), passwordHash, request.name());
		db.saveCoordinator(newCoordinator);
	}

	private record ModifyEmailRequest(Coordinator coordinator, String newEmail) { }
	@PostMapping("/modifyEmail")
	public void modifyEmail(@RequestBody ModifyEmailRequest request) {
		// todo: Credentials validation
		request.coordinator().setEmail(request.newEmail());
	}

	private record ModifyPasswordRequest(Coordinator coordinator, String newPassword) { }
	@PostMapping("/modifyPassword")
	public void modifyPassword(@RequestBody ModifyPasswordRequest request) {
		// todo: Credentials validation
		// todo: Hash password
		String passwordHash = request.newPassword();
		request.coordinator().setPasswordHash(passwordHash);
	}
}
