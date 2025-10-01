package com.aau.grouping_system.Authentication;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.User.Coordinator.Coordinator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final Database db;
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	// Constructer injection
	// Til at bruge Databasen når vi senere tjekker igennem listen af Coordinators.
	public AuthController(Database db) {
		this.db = db;
	}

	// Metoden behandler en POST request, når coordinatoren prøver at logge ind
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody Map<String, String> body, HttpServletRequest request) {

		// De forskellige værdier fra coordinatoren bliver stored i variabler
		String email = body.get("email");
		String password = body.get("password");

		// Hvis coordinatoren findes i databasen, så bliver værdierne lageret i
		// variablen
		// ellers bliver emailen sat til null

		Coordinator user = null;
		for (Coordinator existingCoordinator : db.getAllCoordinators().values()) {
			if (existingCoordinator.getEmail().equals(email)) {
				user = existingCoordinator;
				break; 
			}
		}
	
		// Hvis emailen ikke eksistere eller adg.koden er forkert, så sendes der en 401 error response
		if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
		}
	
		// Hvis der findes en session ved login, så skal den slettes og der skal laves en ny
		//getSession tjekker om der eksistere en session i HttpSession objektet
		HttpSession oldSession = request.getSession(false);
		if (oldSession != null)
			oldSession.invalidate();
		HttpSession session = request.getSession(true);
		session.setMaxInactiveInterval(86400); //1 dag
		// Gemmer nøglen "user" i session objektet.
		// Til fremtide kald under samme session, skal credentials ikke tjekkes igen,
		// men derimod kan session.getAttribute("user") tjekkes.
		session.setAttribute("user", user);

		return ResponseEntity.ok("Logged in, user: " + user.getName());
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(HttpServletRequest request) {

		HttpSession session = request.getSession(false);
		if (session != null)
			session.invalidate();
		return ResponseEntity.ok("Logged out");
	}

	// Mangler? - @GetMapping("/me") så frontenden kan tjekke hvem der er logget ind
	// Mangler for studerne og supervisors
	// Benytter sig af bruteforce metoden,
	// når den skal tjekke db for coordinatore - kan det optimeres?

}