package org.entur.auth.client;

import java.util.function.Supplier;

import org.entur.auth.client.properties.OidcAuthClientAuth0Properties;

public class Auth0AccessTokenFactory implements Supplier<AccessTokenFactory>{

	private final OidcAuthClientAuth0Properties properties;
	
	// default values
    protected Long mustRefreshThreshold;
    protected Long shouldRefreshThreshold;
    protected Long minThrottleTime;
    protected Long maxThrottleTime;

	public Auth0AccessTokenFactory(OidcAuthClientAuth0Properties properties, Long mustRefreshThreshold,
			Long shouldRefreshThreshold, Long minThrottleTime, Long maxThrottleTime) {
		super();
		this.properties = properties;
		this.mustRefreshThreshold = mustRefreshThreshold;
		this.shouldRefreshThreshold = shouldRefreshThreshold;
		this.minThrottleTime = minThrottleTime;
		this.maxThrottleTime = maxThrottleTime;
	}

	@Override
	public AccessTokenFactory get() {
        return new AccessTokenFactoryBuilder()
                .withDomain(properties.getDomain())
                .withClientSecret(properties.getSecret())
                .withClientId(properties.getClientId())
                .withAudience(properties.getAudience())
                .withMustRefreshThreshold(properties.getMustRefreshThreshold() != null ? properties.getMustRefreshThreshold() : mustRefreshThreshold)
                .withShouldRefreshThreshold(properties.getShouldRefreshThreshold() != null ? properties.getShouldRefreshThreshold() : shouldRefreshThreshold)
                .withMinThrottleTime(properties.getMinThrottleTime() != null ? properties.getMinThrottleTime() : minThrottleTime)
                .withMaxThrottleTime(properties.getMaxThrottleTime() != null ? properties.getMaxThrottleTime() : maxThrottleTime)
                .buildAuth0();
	}
}
