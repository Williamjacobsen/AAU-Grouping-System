package com.aau.grouping_system.Authentication;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Exceptions.RequestException;
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
			throw new RequestException(HttpStatus.UNAUTHORIZED, "Invalid email/id or password");
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
			throw new RequestException(HttpStatus.UNAUTHORIZED, "Request does not contain a login session.");
		}

		User user = (User) session.getAttribute("user");
		if (user == null) {
			throw new RequestException(HttpStatus.UNAUTHORIZED, "Login session is invalid.");
		}

		return ResponseEntity
				.ok(user); // info om user returneres som JSON obj.
	}

}