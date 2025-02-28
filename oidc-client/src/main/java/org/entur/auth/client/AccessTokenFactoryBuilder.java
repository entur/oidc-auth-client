package org.entur.auth.client;

import org.entur.auth.client.auth0.Auth0AccessTokenClient;

/**
 * Builder class for constructing an instance of {@link AccessTokenFactory}.
 * This builder allows configuring various parameters related to token refresh thresholds,
 * throttle times, authentication credentials, and domain settings before constructing the factory.
 */
public class AccessTokenFactoryBuilder {

	// Token refresh thresholds
    protected Long mustRefreshThreshold;
    protected Long shouldRefreshThreshold;

	// Throttling settings
	private Long minThrottleTime;
	private Long maxThrottleTime;

    protected String domain;
    protected String realm;
    protected String clientId;
    protected String clientSecret;
    protected String audience;
    
    public long getMustRefreshThreshold() {
        return mustRefreshThreshold;
    }

    public AccessTokenFactoryBuilder withMustRefreshThreshold(Long mustRefreshThreshold) {
        this.mustRefreshThreshold = mustRefreshThreshold;
        return this;
    }

    public Long getShouldRefreshThreshold() {
        return shouldRefreshThreshold;
    }

    public AccessTokenFactoryBuilder withShouldRefreshThreshold(Long shouldRefreshThreshold) {
        this.shouldRefreshThreshold = shouldRefreshThreshold;
        return this;
    }

	public Long getMinThrottleTime() {
		return minThrottleTime;
	}

	public AccessTokenFactoryBuilder withMinThrottleTime(Long minThrottleTime) {
		this.minThrottleTime = minThrottleTime;
		return this;
	}

	public Long getMaxThrottleTime() {
		return maxThrottleTime;
	}

	public AccessTokenFactoryBuilder withMaxThrottleTime(Long maxThrottleTime) {
		this.maxThrottleTime = maxThrottleTime;
		return this;
	}

	protected void validateBuild() {
		if(mustRefreshThreshold != null || shouldRefreshThreshold != null) {
			if(mustRefreshThreshold == null ) {
				throw new IllegalArgumentException("Please specify blocking refresh threshold");
			}

			if(shouldRefreshThreshold == null) {
				throw new IllegalArgumentException("Please specify non-blocking refresh threshold");
			}

			if(mustRefreshThreshold >= shouldRefreshThreshold) {
				throw new IllegalArgumentException("Please specify blocking threshold as a lower value than non-blocking threshold");
			}
		}

		if(minThrottleTime != null || maxThrottleTime != null) {
			if (minThrottleTime == null) {
				throw new IllegalArgumentException("Please specify minimum throttle time");
			}
			if (maxThrottleTime == null) {
				throw new IllegalArgumentException("Please specify maximum throttle time");
			}

			if(minThrottleTime >= maxThrottleTime) {
				throw new IllegalArgumentException("Please specify minimum throttle time as a lower value than maximum throttle time");
			}
		}

		if(domain == null) {
    		throw new IllegalArgumentException("Please specify domain");
    	}
    	if(clientId == null) {
    		throw new IllegalArgumentException("Please specify client id");
    	}
    	if(clientSecret == null) {
    		throw new IllegalArgumentException("Please specify client secret");
    	}
    	if(audience == null) {
    		throw new IllegalArgumentException("Please specify audience");
    	}
    }
    
    public AccessTokenFactory buildAuth0() {
    	validateBuild();
    	AccessTokenClient client = new Auth0AccessTokenClient(domain, clientId, clientSecret, audience);
    	
    	return build(client);
    }
    
	protected AccessTokenFactory build(AccessTokenClient client) {
		AccessTokenFactory accessTokenFactory = new AccessTokenFactory(client);

		if(mustRefreshThreshold != null) {
			accessTokenFactory.setMustRefreshThreshold(mustRefreshThreshold);
		}
		if(shouldRefreshThreshold != null) {
			accessTokenFactory.setShouldRefreshThreshold(shouldRefreshThreshold);
		}
		if(minThrottleTime != null) {
			accessTokenFactory.setMinThrottleTime(minThrottleTime);
		}
		if(maxThrottleTime != null) {
			accessTokenFactory.setMaxThrottleTime(maxThrottleTime);
		}

		return accessTokenFactory;
	}

	public String getDomain() {
		return domain;
	}

	public AccessTokenFactoryBuilder withDomain(String domain) {
		this.domain = domain;
		return this;
	}

	public String getRealm() {
		return realm;
	}

	public AccessTokenFactoryBuilder withRealm(String realm) {
		this.realm = realm;
		return this;
	}

	public String getClientId() {
		return clientId;
	}

	public AccessTokenFactoryBuilder withClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public AccessTokenFactoryBuilder withClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
		return this;
	}

	public String getAudience() {
		return audience;
	}

	public AccessTokenFactoryBuilder withAudience(String audience) {
		this.audience = audience;
		
		return this;
	}

}
