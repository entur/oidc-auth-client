# oidc-auth-client
oidc-auth-client is a Java and Spring Boot library for retrieving and caching access tokens from an OpenID Connect (OIDC) provider.
It simplifies authentication by handling token acquisition, caching, and renewal, making secure API calls easier.

## Features
* Retrieve access tokens from an OIDC provider
* Automatically cache and refresh tokens
* Simple integration with Spring Boot
* Includes AccessTokenClient for [Auth0](https://auth0.com)

## Installation
Add the dependency to your project:
```
<dependency>
    <groupId>org.entur.auth.client</groupId>
    <artifactId>oidc-client-spring-boot</artifactId>
    <version>1.0.0</version>
</dependency>
```

Alternatively if Spring Boot integration is not needed:
```
<dependency>
    <groupId>org.entur.auth.client</groupId>
    <artifactId>oidc-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Configuration
In Application.yaml, oidc-auth can be configured for one or more oidc clients.

### Single client

Syntax for configuration of a single client of AccessTokenFactory.
For single client configurations, the name will always be "auth0".

```yaml
entur:
  client:
    shouldRefreshThreshold: 120 # Default=120. Time (seconds) before proactive token refresh. 
    mustRefreshThreshold: 60 # Default=60. Minimum time (seconds) before forced token refresh.
    minThrottleTime: 1  # Default 1. Throttle time will increase exponentially from min to max throttle time. 
    maxThrottleTime: 600 # Default 600 (10 minutes).
    auth0:
      clientId: <clientId>
      secret: <secret>
      domain: <your.domain>
      audience: <your audience>
```

### Multiple clients
Syntax for configuration multiple clients of AccessTokenFactory.
The example below will set up clients multiple with the names "myFirstClient" and "mySecondClient".

```yaml
entur:
  clients:
    shouldRefreshThreshold: 120 # Default=120. Time (seconds) before proactive token refresh. 
    mustRefreshThreshold: 60    # Default=60. Minimum time (seconds) before forced token refresh.
    minThrottleTime: 1          # Default 1. Throttle time will increase exponentially from min to max throttle time. 
    maxThrottleTime: 600        # Default 600 (10 minutes).
    auth0:
      myFirstClient:
        clientId: <clientId>
        secret: <secret>
        domain: <your.domain>
        audience: <your audience>
      mySecondClient:
        clientId: <clientId>
        secret: <secret>
        domain: <your.domain>
        audience: <your audience>
        shouldRefreshThreshold: 60  # Override default for this client
        mustRefreshThreshold: 30    # Override default for this client
        minThrottleTime: 5          # Override default for this client
        maxThrottleTime: 300        # Override default for this client
```

## Usage

### Spring Boot
In a java Spring Boot application AccessTokenFactory can be auto wired:

```java
@Autowired
@Qualifier("myFirstClient") // Use Qualifier if more when one client are configured. 
private AccessTokenFactory accessTokenFactory;
```

A valid access token can then be retrieved from accessTokenFactory by doing the following:
```java
var accessToken = accessTokenFactory.getAccessToken();
```

### Manually
An accessTokenFactory can also be configured directly outside of Spring Boot in-code:

```java
var accessTokenFactory = new AccessTokenFactoryBuilder()
        .withDomain(oidcAuthProperties.getDomain())
        .withClientSecret(oidcAuthProperties.getSecret())
        .withClientId(oidcAuthProperties.getClientId())
        .withAudience(oidcAuthProperties.getAudience())
        .withMustRefreshThreshold(oidcAuthProperties.getMustRefreshThreshold())
        .withShouldRefreshThreshold(oidcAuthProperties.getShouldRefreshThreshold())
        .withMinThrottleTime(oidcAuthProperties.getMinThrottleTime())
        .withMaxThrottleTime(oidcAuthProperties.getMaxThrottleTime())
        .buildAuth0();

var accessToken = accessTokenFactory.getAccessToken();
```

## Testing
The `AccessTokenFactory` bean can be mocked as a normal bean (for the call `getAccessToken()`).

```java
@MockitoBean
private AccessTokenFactory accessTokenFactory;
```

and then mock using

```java
when(accessTokenFactory.getAccessToken()).thenReturn("Bearer ABC");
```

The starter will detect whether an existing AccessTokenFactory exists (in the above case, the mock). See [example](src/test/java/org/entur/auth/client/MockConfigurationTest.java).

## Development
Clone the repository:
```shell
git clone https://github.com/entur/oidc-auth-client.git
cd oidc-auth-client
```

Build with Gradle:
```shell
./gradlew build
```

Run tests:
```shell
./gradlew test
```

## Contributing
Contributions are welcome! See [CONTRIBUTING](/CONTRIBUTING.md) for details.

## License
This project is licensed under the EUPL-1.2 license. See [LICENSE](/README.md) for details.