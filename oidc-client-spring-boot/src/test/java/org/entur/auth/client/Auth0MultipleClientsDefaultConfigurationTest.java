package org.entur.auth.client;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * 
 * Test using default values
 * 
 */

@TestPropertySource(properties = {
        "entur.clients.auth0.one.clientId=xxx",
        "entur.clients.auth0.one.secret=yyy",
        "entur.clients.auth0.one.domain=internal-entur-dev.eu.auth0.com",
        "entur.clients.auth0.one.audience=https://dev.devstage.entur.io",
        
        "entur.clients.auth0.two.clientId=zzz",
        "entur.clients.auth0.two.secret=aaa",
        "entur.clients.auth0.two.domain=partner-entur-dev.eu.auth0.com",
        "entur.clients.auth0.two.audience=https://dev.devstage.entur.io",
        
        "entur.clients.mustRefreshThreshold=60",
        "entur.clients.shouldRefreshThreshold=120",

        "entur.clients.minThrottleTime=2",
        "entur.clients.maxThrottleTime=300"
})

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class Auth0MultipleClientsDefaultConfigurationTest {

    @Autowired
    @Qualifier("one")
    private AccessTokenFactory accessTokenFactory1;

    @Autowired
    @Qualifier("two")
    private AccessTokenFactory accessTokenFactory2;

    @LocalServerPort
    private int randomServerPort;

    @Test 
    public void testCreate() {
    	assertThat(accessTokenFactory1.getMustRefreshThreshold()).isEqualTo(60);
    	assertThat(accessTokenFactory1.getShouldRefreshThreshold()).isEqualTo(120);
        assertThat(accessTokenFactory1.getMinThrottleTime()).isEqualTo(2);
        assertThat(accessTokenFactory1.getMaxThrottleTime()).isEqualTo(300);

    	assertThat(accessTokenFactory2.getMustRefreshThreshold()).isEqualTo(60);
    	assertThat(accessTokenFactory2.getShouldRefreshThreshold()).isEqualTo(120);
        assertThat(accessTokenFactory2.getMinThrottleTime()).isEqualTo(2);
        assertThat(accessTokenFactory2.getMaxThrottleTime()).isEqualTo(300);

    }    

}