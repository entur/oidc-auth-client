package org.entur.auth.client;

import java.io.IOException;

/**
 * An AccessTokenClient is an interface that defines a set of methods for interacting with an OAuth 2.0 access token service.
 */
public interface AccessTokenClient {

    /**
     * The requestAccessToken method is used to request an access token from the service.
     *
     * @return an object holding information about the token.
     * @throws IOException when authorization server not responding
     */
    TokenHolder requestAccessToken() throws IOException;

    /**
     * The renewAccessToken method is used to request a new access token using a refresh token.

     * @param refreshToken to be used for obtaining a new access token.
     * @return an object holding information about the token.
     * @throws IOException when authorization server not responding
     */
    TokenHolder renewAccessToken(String refreshToken) throws IOException;

    /**
     * The revokeRefreshToken method is used to invalidate a refresh token.
     *
     * @param refreshToken to be invalidated.
     * @throws IOException when authorization server not responding
     */
    void revokeRefreshToken(String refreshToken) throws IOException;
}
