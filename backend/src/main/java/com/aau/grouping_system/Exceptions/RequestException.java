package com.aau.grouping_system.Exceptions;

import org.springframework.http.HttpStatus;

/// Handled by the GlobalExceptionhandler, which sends back a ResponseEntity of
/// type String with the message and HttpStatus of this. Handling request
/// exceptions by throwing this is smart, because it allows us to send back a
/// ResponseEntity of type String with an error message despite the route having
/// a differnent return type (for example, it could be ResponseEntity of type
/// Coordinator). It also allows us to throw these exceptions inside outside
/// functions that a request may use.
public class RequestException extends RuntimeException {

	private HttpStatus status;

	public RequestException(HttpStatus status, String message) {
		super(message);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}
}
