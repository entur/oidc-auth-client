package org.entur.auth.client.properties;

public final class OidcAuthClientAuth0Properties {

    private String domain;
    private String clientId;
    private String secret;
    private String audience;

    private Long mustRefreshThreshold;
    private Long shouldRefreshThreshold;

    private Long minThrottleTime;
    private Long maxThrottleTime;
    
    public String getDomain() {
        return domain;
    }
    public void setDomain(String domain) {
        this.domain = domain;
    }
    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public String getSecret() {
        return secret;
    }
    public void setSecret(String secret) {
        this.secret = secret;
    }
    public String getAudience() {
        return audience;
    }
    public void setAudience(String audience) {
        this.audience = audience;
    }
    public Long getMustRefreshThreshold() {
        return mustRefreshThreshold;
    }
    public void setMustRefreshThreshold(Long mustRefreshThreshold) {
        this.mustRefreshThreshold = mustRefreshThreshold;
    }
    public Long getShouldRefreshThreshold() {
        return shouldRefreshThreshold;
    }
    public void setShouldRefreshThreshold(Long shouldRefreshThreshold) {
        this.shouldRefreshThreshold = shouldRefreshThreshold;
    }
    public Long getMinThrottleTime() {
        return minThrottleTime;
    }
    public void setMinThrottleTime(Long minThrottleTime) {
        this.minThrottleTime = minThrottleTime;
    }
    public Long getMaxThrottleTime() {
        return maxThrottleTime;
    }
    public void setMaxThrottleTime(Long maxThrottleTime) {
        this.maxThrottleTime = maxThrottleTime;
    }

}