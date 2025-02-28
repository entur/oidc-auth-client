package org.entur.auth.client;

/**
 * Exception thrown when an access token is unavailable.
 */
public class AccessTokenUnavailableException extends RuntimeException {

	/**
	 * Constructs a new {@code AccessTokenUnavailableException} with no detail message.
	 */
	public AccessTokenUnavailableException() {
		super();
	}

}
