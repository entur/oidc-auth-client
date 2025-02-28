package org.entur.auth.client;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * Manages the retrieval and caching of access tokens for authentication.
 * Utilizes Caffeine caching to optimize token retrieval and refresh logic.
 */
public class AccessTokenFactory {
    public static final String ACCESS_TOKEN_KEY = "access_token";
    private static final long MUST_REFRESH_THRESHOLD = 60;  // Minimum time (seconds) before forced token refresh
    private static final long SHOULD_REFRESH_THRESHOLD = 120;  // Time (seconds) before proactive token refresh
    private static final Logger log = LoggerFactory.getLogger(AccessTokenFactory.class);
    private final AccessTokenClient client;

    private AccessTokenWrapper loadedAccessToken;  // Holds the latest successfully retrieved token

    protected Clock clock = Clock.systemUTC();  // Used for handling time-based operations
    protected LoadingCache<String, TokenHolder> cache;  // Caching mechanism for access tokens

    /**
     * Threshold (in seconds) before a token must be refreshed.
     * Requests will wait for the new token to be retrieved.
     */
    private long mustRefreshThreshold = MUST_REFRESH_THRESHOLD;

    /**
     * Threshold (in seconds) before a token should be proactively refreshed.
     * Requests arriving during refresh will use the old token.
     */
    private long shouldRefreshThreshold = SHOULD_REFRESH_THRESHOLD;

    /**
     * Minimum backoff time (in seconds) before retrying when token retrieval fails.
     */
    private long minThrottleTime = 1;       //1 second

    /**
     * Maximum backoff time (in seconds) for retries after repeated token retrieval failures.
     */
    private long maxThrottleTime = 600;   // 10 minutes

    /**
     * Current backoff time used for retrying token retrieval.
     */
    private long currentThrottleTime = 1;   // 10 minutes


    /**
     * Constructs an AccessTokenFactory with a given AccessTokenClient and initializes the token cache.
     *
     * @param client the AccessTokenClient responsible for retrieving tokens
     */
    public AccessTokenFactory(AccessTokenClient client) {
        this.client = client;
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.HOURS)   // Set default expireAfterWrite, needed so it can be changed in loadAccessToken
                .refreshAfterWrite(5, TimeUnit.HOURS)  // Set default refreshAfterWrite, needed so it can be changed in loadAccessToken
                .build(key -> loadAccessToken());

    }

    /**
     * Retrieves the current access token, ensuring it is valid.
     * If expired, it triggers a refresh.
     *
     * @return a valid access token as a String
     * @throws AccessTokenUnavailableException if no valid token is available
     */
    public String getAccessToken() throws AccessTokenUnavailableException {
        TokenHolder tokenHolder = this.cache.get(ACCESS_TOKEN_KEY);
        if (tokenHolder == null || tokenHolder.getAccessToken() == null) {
            throw new AccessTokenUnavailableException();
        }

        if(tokenHolder.getExpiresAt() != null && Instant.now().isAfter(tokenHolder.getExpiresAt().toInstant())) {
            log.debug("Access token has expired and will be reloaded.");
            this.cache.invalidateAll();
            tokenHolder = this.cache.get(ACCESS_TOKEN_KEY);
            if (tokenHolder == null || tokenHolder.getAccessToken() == null) {
                throw new AccessTokenUnavailableException();
            }
        }

        return tokenHolder.getAccessToken();
    }

    public long getMustRefreshThreshold() {
        return mustRefreshThreshold;
    }

    public void setMustRefreshThreshold(long mustRefreshThreshold) {
        this.mustRefreshThreshold = mustRefreshThreshold;
    }

    public long getShouldRefreshThreshold() {
        return shouldRefreshThreshold;
    }

    public void setShouldRefreshThreshold(long shouldRefreshThreshold) {
        this.shouldRefreshThreshold = shouldRefreshThreshold;
    }

    public AccessTokenClient getClient() {
        return client;
    }

    public long getMinThrottleTime() {
        return minThrottleTime;
    }

    public void setMinThrottleTime(long minThrottleTime) {
        this.minThrottleTime = minThrottleTime;
    }

    public long getMaxThrottleTime() {
        return maxThrottleTime;
    }

    public void setMaxThrottleTime(long maxThrottleTime) {
        this.maxThrottleTime = maxThrottleTime;
    }

    /**
     * Loads a new access token, handling retries and caching policies.
     *
     * @return a new TokenHolder containing the access token
     * @throws IOException if token retrieval fails
     */
    protected TokenHolder loadAccessToken() throws IOException {
        if (client == null) {
            throw new InternalError("AccessTokenClient is not set for AccessTokenFactory");
        }

        log.info("Creating new OpenID token");
        try {
            var tokenHolder = client.requestAccessToken();  // will not use refresh token
            this.loadedAccessToken = wrap(tokenHolder);

            cache.policy().refreshAfterWrite().ifPresent(exp -> exp.setRefreshesAfter(Duration.ofSeconds(tokenHolder.getExpiresIn() - shouldRefreshThreshold)));
            cache.policy().expireAfterWrite().ifPresent(exp -> exp.setExpiresAfter(Duration.ofSeconds(tokenHolder.getExpiresIn() - mustRefreshThreshold)));
            currentThrottleTime = minThrottleTime;

            log.info("OpenID token is valid for {} seconds", tokenHolder.getExpiresIn());

            return tokenHolder;

        } catch (RuntimeException ex) {
            return handleTokenFailure();
        }
    }

    /**
     * Handles token retrieval failures by applying exponential backoff and adjusting expiration policies.
     */
    private TokenHolder handleTokenFailure() {
        long mustRefreshIn = this.loadedAccessToken == null ? 0 : this.loadedAccessToken.mustRefreshIn(clock.millis());

        if (mustRefreshIn > 0) {
            log.info("OpenID token can not be refreshed. Will retry after {} seconds", currentThrottleTime);
            cache.policy().refreshAfterWrite().ifPresent(exp -> exp.setRefreshesAfter(Duration.ofSeconds(Math.min(currentThrottleTime, mustRefreshIn))));
            cache.policy().expireAfterAccess().ifPresent(exp -> exp.setExpiresAfter(Duration.ofSeconds(mustRefreshIn)));
            increaseNextThrottleTime();
            return loadedAccessToken.getTokenHolder();
        } else {
            log.info("OpenID token can not be fetched. Will retry after {} seconds", currentThrottleTime);
            cache.policy().refreshAfterWrite().ifPresent(exp -> exp.setRefreshesAfter(Duration.ofSeconds(currentThrottleTime)));
            cache.policy().expireAfterAccess().ifPresent(exp -> exp.setExpiresAfter(Duration.ofSeconds(currentThrottleTime)));
            increaseNextThrottleTime();
            return new TokenHolder() {
            };
        }
    }

    /**
     * Wraps a TokenHolder with additional metadata for refresh logic.
     *
     * @param accessTokenResponse the retrieved access token
     * @return a wrapped AccessTokenWrapper instance
     */
    private AccessTokenWrapper wrap(TokenHolder accessTokenResponse) {
        return new AccessTokenWrapper(accessTokenResponse, clock.millis(), accessTokenResponse.getExpiresIn())
                .setMustRefreshThreshold(mustRefreshThreshold);
    }

    /**
     * Increases the throttle time for retries, using an exponential backoff strategy.
     */
    private void increaseNextThrottleTime() {
        long newNextThrottleTime = currentThrottleTime * 2;
        if (newNextThrottleTime > maxThrottleTime) {
            newNextThrottleTime = maxThrottleTime;
        }
        currentThrottleTime = newNextThrottleTime;
    }
}
