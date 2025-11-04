package com.aau.grouping_system.Authentication;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

import com.aau.grouping_system.User.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthService service;

	public AuthController(AuthService authService) {
		this.service = authService;
	}

	@PostMapping("/signIn")
	public ResponseEntity<String> login(@RequestBody Map<String, String> body, HttpServletRequest request) {

		String email = body.get("emailOrId");
		String password = body.get("password");
		User.Role role = User.Role.valueOf(body.get("role"));

		User user = service.findByEmailOrId(email, role);

		if (user == null || !service.isPasswordCorrect(password, user)) {
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED) // 401 error
					.body("Invalid email/id or password");
		}

		service.invalidateOldSession(request);
		service.createNewSession(request, user);

		return ResponseEntity
				.ok("Signed in, user: " + user.getEmail());
	}

	@PostMapping("/signOut")
	public ResponseEntity<String> logout(HttpServletRequest request) {

		service.invalidateOldSession(request);
		return ResponseEntity
				.ok("Signed out"); // 200 ok
	}

	@GetMapping("/getUser")
	public ResponseEntity<User> getUser(HttpServletRequest request) {

		HttpSession session = request.getSession(false);
		if (session == null) {
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED) // 401 error
					.body(null);
		}

		User user = (User) session.getAttribute("user");

		if (user == null) {
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED) // 401 error
					.body(null);
		}

		return ResponseEntity
				.ok(user); // info om user returneres som JSON obj.
	}

	@PostMapping("/forgotPassword")
	public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> body) {
		
		String email = body.get("email");

		User user = service.findByEmailOrId(email, User.Role.Coordinator);

		if (user == null)
			return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(null);

		return ResponseEntity
				.ok("An email has been send to you"); 
	}

	// @GetMapping("/resetPassword")
	// public ResponseEntity<String> resetPassword(HttpServletRequest request) {
		
	// }
	

}