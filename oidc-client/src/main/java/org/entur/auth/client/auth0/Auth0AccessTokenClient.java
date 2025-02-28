package org.entur.auth.client.auth0;

import java.io.IOException;
import java.util.Date;

import com.auth0.exception.Auth0Exception;
import com.auth0.net.Response;
import org.entur.auth.client.AccessTokenClient;

import com.auth0.client.auth.AuthAPI;
import com.auth0.net.Request;
import org.entur.auth.client.TokenHolder;

/**
 * A client for retrieving, renewing, and revoking access tokens using Auth0.
 */
public class Auth0AccessTokenClient implements AccessTokenClient {

    private final AuthAPI authApi;
    private final String audience;

    /**
     * Deprecated constructor for Auth0AccessTokenClient.
     *
     * @param authApi  The AuthAPI instance to use for token requests.
     * @param audience The audience identifier for the token requests.
     * @deprecated This constructor is deprecated, and it's recommended to use the other constructor.
     */
    @Deprecated
    public Auth0AccessTokenClient(AuthAPI authApi, String audience) {
        this.authApi = authApi;
        this.audience = audience;
    }

    /**
     * Constructs a new Auth0AccessTokenClient.
     *
     * @param domain       The Auth0 domain.
     * @param clientId     The client ID.
     * @param clientSecret The client secret.
     * @param audience     The audience identifier for the token requests.
     */
    public Auth0AccessTokenClient(String domain, String clientId, String clientSecret, String audience) {
        this.authApi = AuthAPI.newBuilder(domain, clientId).withClientSecret(clientSecret).build();
        this.audience = audience;
    }

    /**
     * Requests a new access token from Auth0.
     *
     * @return The TokenHolder containing the access token and related information.
     * @throws IOException   If there's an I/O error during the token request.
     * @throws Auth0Exception If the token request is unsuccessful.
     */
    public TokenHolder requestAccessToken() throws IOException {
    	var requestToken = authApi.requestToken(audience);
        var response = requestToken.execute();

        if(response.getStatusCode() >= 400) {
            throw new Auth0Exception("Can not retrieve access token");
        }

    	return getTokenHolder(response);
    }

    /**
     * Renews an existing access token using a refresh token.
     *
     * @param refreshToken The refresh token used to renew the access token.
     * @return The TokenHolder containing the renewed access token and related information.
     * @throws IOException   If there's an I/O error during the token renewal request.
     * @throws Auth0Exception If the token renewal request is unsuccessful.
     */
    public TokenHolder renewAccessToken(String refreshToken) throws IOException {
    	var renewAuth = authApi.renewAuth(refreshToken);
        var response = renewAuth.execute();

        if(response.getStatusCode() >= 400) {
            throw new Auth0Exception("Can not retrieve access token");
        }

        return getTokenHolder(response);
    }

    private static TokenHolder getTokenHolder(Response<com.auth0.json.auth.TokenHolder> response) {
        final var tokenHolder = response.getBody();
        return new TokenHolder() {
            @Override
            public long getExpiresIn() {
                return tokenHolder.getExpiresIn();
            }

            @Override
            public String getAccessToken() {
                return tokenHolder.getAccessToken();
            }

            @Override
            public Date getExpiresAt() {
                return tokenHolder.getExpiresAt();
            }

            @Override
            public String getRefreshToken() {
                return tokenHolder.getRefreshToken();
            }
        };
    }

    /**
     * Revokes a refresh token, rendering it invalid for future token requests.
     *
     * @param refreshToken The refresh token to revoke.
     * @throws IOException If there's an I/O error during the token revocation request.
     */
    public void revokeRefreshToken(String refreshToken) throws IOException {
    	Request<Void> revokeToken = authApi.revokeToken(refreshToken);

    	revokeToken.execute();
    }

}
