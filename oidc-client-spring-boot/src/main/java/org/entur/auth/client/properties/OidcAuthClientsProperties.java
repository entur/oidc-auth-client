package org.entur.auth.client.properties;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "entur.clients")
public class OidcAuthClientsProperties {

	// default values
    protected Long mustRefreshThreshold;
    protected Long shouldRefreshThreshold;

	protected Long minThrottleTime = 1L;
	protected Long maxThrottleTime = 600L;

	private Map<String, OidcAuthClientAuth0Properties> auth0 = new HashMap<>();
	
	public void setAuth0(Map<String, OidcAuthClientAuth0Properties> auth0) {
		this.auth0 = auth0;
	}
	
	public Map<String, OidcAuthClientAuth0Properties> getAuth0() {
		return auth0;
	}
	
	public void setMustRefreshThreshold(Long mustRefreshThreshold) {
		this.mustRefreshThreshold = mustRefreshThreshold;
	}
	
	public void setShouldRefreshThreshold(Long shouldRefreshThreshold) {
		this.shouldRefreshThreshold = shouldRefreshThreshold;
	}

	public void setMinThrottleTime(Long minThrottleTime) {
		this.minThrottleTime = minThrottleTime;
	}

	public void setMaxThrottleTime(Long maxThrottleTime) {
		this.maxThrottleTime = maxThrottleTime;
	}

	public Long getMustRefreshThreshold() {
		return mustRefreshThreshold;
	}
	
	public Long getShouldRefreshThreshold() {
		return shouldRefreshThreshold;
	}

	public Long getMinThrottleTime() {
		return minThrottleTime;
	}

	public Long getMaxThrottleTime() {
		return maxThrottleTime;
	}
}
