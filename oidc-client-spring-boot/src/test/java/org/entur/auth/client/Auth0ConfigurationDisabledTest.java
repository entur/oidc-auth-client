package org.entur.auth.client;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("client_disabled")
@TestPropertySource(
        properties = {
            "entur.client.auth0.enabled=false",
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
public class Auth0ConfigurationDisabledTest {

    @MockitoBean private AccessTokenFactory accessTokenFactory;

    @Test
    public void testDisabled() {
        assertNull(accessTokenFactory.getClient());
    }
}
