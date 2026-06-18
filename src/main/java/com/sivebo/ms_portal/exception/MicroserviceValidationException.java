package com.sivebo.ms_portal.exception;

/**
 * Thrown when a referenced resource does not exist in a downstream microservice.
 * Represents a client error (the request referenced something that is not valid).
 */
public class MicroserviceValidationException extends RuntimeException {

	public MicroserviceValidationException(String message) {
		super(message);
	}
}
