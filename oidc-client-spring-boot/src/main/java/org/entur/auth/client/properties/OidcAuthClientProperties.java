package org.entur.auth.client.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "entur.client")
public class OidcAuthClientProperties {

	private OidcAuthClientAuth0Properties auth0 = new OidcAuthClientAuth0Properties();

	public void setAuth(OidcAuthClientAuth0Properties auth0) {
		this.auth0 = auth0;
	}
	
	public OidcAuthClientAuth0Properties getAuth0() {
		return auth0;
	}
}
