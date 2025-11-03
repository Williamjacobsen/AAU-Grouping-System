package com.aau.grouping_system.Exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(RequestException.class)
	public ResponseEntity<String> handleRequestException(RequestException requestException) {
		return ResponseEntity.status(requestException.getStatus()).body(requestException.getMessage());
	}
}