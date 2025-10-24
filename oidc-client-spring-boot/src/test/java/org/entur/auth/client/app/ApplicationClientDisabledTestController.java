package org.entur.auth.client.app;

import org.entur.auth.client.AccessTokenFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("client_disabled")
@Service
public class ApplicationClientDisabledTestController {
    @Autowired private AccessTokenFactory accessTokenFactory;
}
