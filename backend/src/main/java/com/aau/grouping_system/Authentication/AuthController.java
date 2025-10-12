package com.aau.grouping_system.Authentication;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.User.Coordinator.Coordinator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthService service;

	public AuthController(AuthService authService, Database db, PasswordEncoder passwordEncoder) {
		this.service = authService;
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody Map<String, String> body, HttpServletRequest request) {

		String email = body.get("email");
		String password = body.get("password");

		Coordinator user = service.findByEmail(email);

		if (user == null || !service.isPasswordCorrect(password, user)) {
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED) // 401 error
					.body("Invalid email or password");
		}

		service.invalidateOldSession(request);
		service.createNewSession(request, user);

		return ResponseEntity
				.ok("Logged in, user: " + user.getName());
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(HttpServletRequest request) {

		service.invalidateOldSession(request);
		return ResponseEntity
				.ok("Logged out"); // 200 ok
	}

	@GetMapping("/me")
	public ResponseEntity<Coordinator> me(HttpServletRequest request) {

		HttpSession session = request.getSession(false);
		if (session == null) {
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED) // 401 error
					.body(null);
		}

		Coordinator user = (Coordinator) session.getAttribute("user");

		if (user != null) {
			return ResponseEntity
					.ok(user); // info om user returneres som JSON obj.
		}
		return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED) // 401 error
				.body(null);
	}

}