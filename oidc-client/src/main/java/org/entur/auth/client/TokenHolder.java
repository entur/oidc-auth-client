package org.entur.auth.client;

import java.util.Date;

/**
 * Represents a holder for authentication tokens, providing methods to retrieve
 * access tokens, expiration information, and refresh tokens.
 */
public interface TokenHolder {
	/**
	 * Gets the remaining time (in seconds) before the access token expires.
	 *
	 * @return the number of seconds until expiration, or 0 if not available.
	 */
	default long getExpiresIn() {
		return 0;
	}

	/**
	 * Retrieves the access token used for authentication.
	 *
	 * @return the access token as a string, or null if not available.
	 */
	default String getAccessToken() {
		return null;
	}

	/**
	 * Gets the exact expiration date and time of the access token.
	 *
	 * @return a {@link Date} representing the expiration time, or null if not available.
	 */
	default Date getExpiresAt() {
		return null;
	}

	/**
	 * Retrieves the refresh token, which can be used to obtain a new access token.
	 *
	 * @return the refresh token as a string, or null if not available.
	 */
	default String getRefreshToken() {
		return null;
	}
}