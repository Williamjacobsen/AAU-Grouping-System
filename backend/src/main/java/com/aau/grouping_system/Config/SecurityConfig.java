package com.aau.grouping_system.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	// TODO: filterChain should be deleted at some point. We have it right now
	// because else CSRF does not allow us to send requests to the backend because
	// of the we have Spring Boot Security as a project dependency.
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf
						.ignoringRequestMatchers("/ws/**") // Ignore CSRF for WebSocket endpoints
				)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/ws/**").permitAll() // Allow WebSocket connections
						.anyRequest().permitAll()
				);
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
