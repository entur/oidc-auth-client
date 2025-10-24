package org.entur.auth.client.app;

import org.entur.auth.client.AccessTokenFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("clients_disabled")
@Service
public class ApplicationClientsDisabledTestController {
    @Autowired
    @Qualifier("one")
    private AccessTokenFactory accessTokenFactory;
}
