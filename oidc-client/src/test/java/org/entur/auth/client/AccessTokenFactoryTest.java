package org.entur.auth.client;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class AccessTokenFactoryTest {

    @Test
    void testExceptionFromRequestAccessToken() throws IOException, AccessTokenUnavailableException  {

        AccessTokenClient client = mock(AccessTokenClient.class);

        TokenHolder first = getResponseWithoutRefreshToken("first");
        when(client.requestAccessToken())
                .thenReturn(first)
                .thenThrow(new RuntimeException("Can not get access token"))
                .thenThrow(new RuntimeException("Can not get access token"))
                .thenThrow(new RuntimeException("Can not get access token"));

        ConfigurableClockAccessTokenFactory factory = getConfigurableClockFactory(client);
        String accessToken = factory.getAccessToken();
        assertThat(accessToken).isEqualTo(first.getAccessToken());

        factory.incrementTime((300 - 20) * 1000);

        accessToken = factory.getAccessToken();
        assertThat(accessToken).isEqualTo(first.getAccessToken());

        factory.incrementTime(50 * 1000);

        assertThrows(AccessTokenUnavailableException.class, factory::getAccessToken);

        assertThrows(AccessTokenUnavailableException.class, factory::getAccessToken);

        verify(client, times(3)).requestAccessToken();
        verify(client, never()).renewAccessToken(any(String.class));
    }

    @Test
    void testCreateAndReuseSession() throws IOException, AccessTokenUnavailableException {

        AccessTokenClient client = mock(AccessTokenClient.class);

        TokenHolder response = getResponseWithRefreshToken("first");

        when(client.requestAccessToken()).thenReturn(response);

        AccessTokenFactory factory = getFactory(client);

        String accessToken = factory.getAccessToken();
        assertThat(accessToken).isEqualTo(response.getAccessToken());

        // check that existing session is reused
        accessToken = factory.getAccessToken();
        assertThat(accessToken).isEqualTo(response.getAccessToken());

        verify(client, times(1)).requestAccessToken();
        verify(client, never()).renewAccessToken(any(String.class));
    }

    @Test
    void testCreateAndSoftRenewSession() throws IOException, AccessTokenUnavailableException, InterruptedException { // i.e. no refresh token

        AccessTokenClient client = mock(AccessTokenClient.class);

        TokenHolder first = getResponseWithoutRefreshToken("first");
        TokenHolder second = getResponseWithoutRefreshToken("second");

        when(client.requestAccessToken()).thenReturn(first).thenReturn(second);

        ConfigurableClockAccessTokenFactory factory = getConfigurableClockFactory(client);

        String accessToken = factory.getAccessToken();
        assertThat(accessToken).isEqualTo(first.getAccessToken());

        // simulate leap in time
        factory.incrementTime((300 - 20) * 1000);

        // check that existing session is refreshed
        accessToken = factory.getAccessToken();
        Thread.currentThread().sleep(200);
        accessToken = factory.getAccessToken();
        assertThat(accessToken).isNotEqualTo(first.getAccessToken());
        assertThat(accessToken).isEqualTo(second.getAccessToken());

        verify(client, times(2)).requestAccessToken();
        verify(client, never()).renewAccessToken(any(String.class));
    }


    @Test
    void testCreateAndLeaveSoftRefreshSessionToAnotherThread() throws IOException, AccessTokenUnavailableException {

        AccessTokenClient client = mock(AccessTokenClient.class);

        TokenHolder first = getResponseWithRefreshToken("first");

        when(client.requestAccessToken()).thenReturn(first);

        AccessTokenFactory factory = getFactory(client);

        String accessToken = factory.getAccessToken();
        assertThat(accessToken).isEqualTo(first.getAccessToken());

        accessToken = factory.getAccessToken();
        assertThat(accessToken).isEqualTo(first.getAccessToken());

        verify(client, times(1)).requestAccessToken();
        verify(client, never()).renewAccessToken(any(String.class));
    }

    @Test
    void testCreateAndHardRenewSession() throws IOException, AccessTokenUnavailableException {

        AccessTokenClient client = mock(AccessTokenClient.class);

        TokenHolder first = getResponseWithoutRefreshToken("first");
        TokenHolder second = getResponseWithoutRefreshToken("second");

        when(client.requestAccessToken()).thenReturn(first).thenReturn(second);

        ConfigurableClockAccessTokenFactory factory = getConfigurableClockFactory(client);

        String accessToken = factory.getAccessToken();
        assertThat(accessToken).isEqualTo(first.getAccessToken());

        // simulate leap in time
        factory.incrementTime((300 - 9) * 1000);

        // check that existing session is refreshed
        accessToken = factory.getAccessToken();
        assertThat(accessToken).isEqualTo(second.getAccessToken());

        verify(client, times(2)).requestAccessToken();
        verify(client, never()).renewAccessToken(any(String.class));
    }


    private TokenHolder getResponseWithRefreshToken(String token) {
    	TokenHolder holder = mock(TokenHolder.class);
    	
    	when(holder.getAccessToken()).thenReturn(token);
    	when(holder.getExpiresIn()).thenReturn(300L);
    	when(holder.getRefreshToken()).thenReturn("refreshToken");
    	
        return holder;
    }
    
    private TokenHolder getResponseWithoutRefreshToken(String token) {
        TokenHolder holder = mock(TokenHolder.class);
        
        when(holder.getAccessToken()).thenReturn(token);
        when(holder.getExpiresIn()).thenReturn(300L);
        
        return holder;
    }

    private ConfigurableClockAccessTokenFactory getConfigurableClockFactory(AccessTokenClient client) {
        ConfigurableClockAccessTokenFactory factory = new ConfigurableClockAccessTokenFactory(client);
        configure(factory);
        return factory;
    }

    private AccessTokenFactory getFactory(AccessTokenClient client) {
    	AccessTokenFactory factory = new AccessTokenFactory(client);
        configure(factory);
        return factory;
    }

    private void configure(AccessTokenFactory factory) {
        factory.setShouldRefreshThreshold(30);
        factory.setMustRefreshThreshold(10);

        factory.setMinThrottleTime(1L);
        factory.setMaxThrottleTime(600L);
    }

}
