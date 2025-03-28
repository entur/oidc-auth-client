package org.entur.auth.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
class RestTemplateTest {

    @AccessToken
    RestTemplate restTemplate;

    @LocalServerPort
    private int randomServerPort;

    @Test 
    void testCreate() throws AccessTokenUnavailableException {
        assertNotNull(restTemplate);
    }

}
