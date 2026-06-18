package com.sivebo.ms_portal.exception;

/**
 * Thrown when a downstream microservice cannot be reached.
 * Represents an upstream/server-side failure, not a client error.
 */
public class MicroserviceUnavailableException extends RuntimeException {

	public MicroserviceUnavailableException(String message) {
		super(message);
	}
}
