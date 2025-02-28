package org.entur.auth.client;

/**
 * Wrapper for an access token that tracks its creation time, expiration, and refresh threshold.
 */
public class AccessTokenWrapper {
    private final TokenHolder tokenHolder;
    private final long tokenCrateTimestamp;   // Timestamp when the token was created
    private final long expiresIn;  // Token validity duration in seconds
    private long mustRefreshThreshold;   // Threshold before expiration to trigger refresh

    /**
     * Constructs an AccessTokenWrapper.
     *
     * @param tokenHolder          The holder object containing the access token.
     * @param tokenCrateTimestamp  The timestamp when the token was created.
     * @param expiresIn            The duration in seconds before the token expires.
     */
    public AccessTokenWrapper(TokenHolder tokenHolder, long tokenCrateTimestamp, long expiresIn) {
        this.tokenHolder = tokenHolder;
        this.tokenCrateTimestamp = tokenCrateTimestamp;
        this.expiresIn = expiresIn;
    }

    /**
     * Gets the token holder.
     *
     * @return The TokenHolder containing the access token.
     */
    public TokenHolder getTokenHolder() {
        return tokenHolder;
    }

    /**
     * Sets the threshold (in seconds) before expiration when the token must be refreshed.
     *
     * @param mustRefreshThreshold The threshold before expiration to trigger refresh.
     * @return The updated AccessTokenWrapper instance.
     */
    public AccessTokenWrapper setMustRefreshThreshold(long mustRefreshThreshold) {
        this.mustRefreshThreshold = mustRefreshThreshold;
        return this;
    }

    /**
     * Calculates the remaining time before the token must be refreshed.
     *
     * @param now The current timestamp in milliseconds.
     * @return The remaining time (in seconds) before the token must be refreshed.
     */
    public long mustRefreshIn(long now) {
        return expiresIn - mustRefreshThreshold - ageInSeconds(now);
    }

    /**
     * Calculates the age of the token in seconds.
     *
     * @param now The current timestamp in milliseconds.
     * @return The token age in seconds.
     */
    private long ageInSeconds(long now) {
        return (now - tokenCrateTimestamp) / 1000;
    }

}
