package org.entur.auth.client;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.testing.FakeTicker;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class ConfigurableClockAccessTokenFactory extends AccessTokenFactory {
    private final FakeTicker fakeTicker = new FakeTicker();

    public ConfigurableClockAccessTokenFactory(AccessTokenClient client) {
        super(client);
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.HOURS)   // Set default expireAfterWrite, needed so it can be changed in loadAccessToken
                .refreshAfterWrite(5, TimeUnit.HOURS)  // Set default refreshAfterWrite, needed so it can be changed in loadAccessToken
                .ticker(fakeTicker::read)
                .build(key -> loadAccessToken());
    }

    public void incrementTime(long duration) {
        setCurrentTimeMillis(clock.millis() + duration);
        fakeTicker.advance(duration, TimeUnit.MILLISECONDS);
    }

    private void setCurrentTimeMillis(long currentTimeMillis) {
        this.clock = Clock.fixed(Instant.ofEpochMilli(currentTimeMillis), clock.getZone());
    }
}
