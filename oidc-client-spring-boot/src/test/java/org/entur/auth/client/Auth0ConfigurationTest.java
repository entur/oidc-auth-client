package org.entur.auth.client;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.entur.auth.client.auth0.Auth0AccessTokenClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@TestPropertySource(properties = {
        "entur.client.auth0.clientId=xxx",
        "entur.client.auth0.secret=yyy",
        "entur.client.auth0.domain=internal-entur-dev.eu.auth0.com",
        "entur.client.auth0.audience=https://dev.devstage.entur.io",
        "entur.client.auth0.mustRefreshThreshold=15",
        "entur.client.auth0.shouldRefreshThreshold=30"
})

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class Auth0ConfigurationTest {

    @Autowired
    private AccessTokenFactory accessTokenFactory;

    @LocalServerPort
    private int randomServerPort;

    @Test 
    public void testCreate() {
        assertTrue(accessTokenFactory.getClient() instanceof Auth0AccessTokenClient);
    }    

}