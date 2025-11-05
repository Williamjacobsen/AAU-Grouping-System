package com.aau.grouping_system.Exceptions;

import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	private ResponseEntity<String> createErrorMesssageResponse(HttpStatus status, String message) {
		return ResponseEntity.status(status).body(message);
	}

	/// Sends back a ResponseEntity<String> with an error message and a HttpStatus.
	@ExceptionHandler(RequestException.class)
	public ResponseEntity<String> handleRequestException(RequestException requestException) {
		return createErrorMesssageResponse(requestException.getStatus(), requestException.getMessage());
	}

	/// Sends back an error message detailing where the validation failed.
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> handleValidationException(
			MethodArgumentNotValidException ex) {

		CopyOnWriteArrayList<String> validationErrors = new CopyOnWriteArrayList<>();
		ex.getBindingResult().getFieldErrors().forEach(error -> {
			String seperatorText = validationErrors.size() != 0 ? " +" : "";
			validationErrors.add(seperatorText + " '" + error.getField() + "': " + error.getDefaultMessage());
		});

		String errorMessage = "Error: Input validation failed at:";
		for (String validationError : validationErrors) {
			errorMessage += validationError;
		}

		return createErrorMesssageResponse(HttpStatus.BAD_REQUEST, errorMessage);
	}
}