package org.entur.auth.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class MockConfigurationTest {

    @MockitoBean
    private AccessTokenFactory accessTokenFactory;

    @LocalServerPort
    private int randomServerPort;

    @Test 
    void testCreate() throws AccessTokenUnavailableException, IOException, InterruptedException {
        when(accessTokenFactory.getAccessToken()).thenReturn("Bearer ABC");
        
        assertNotNull(accessTokenFactory.getAccessToken());
    }    

}
