# oidc-client
oidc-client is a Java library for retrieving and caching access tokens from an OpenID Connect (OIDC) provider.
It simplifies authentication by handling token acquisition, caching, and renewal, making secure API calls easier.

## Features
* Retrieve access tokens from an OIDC provider
* Automatically cache and refresh tokens
* Includes AccessTokenClient for [Auth0](https://auth0.com)

## Installation
Add the dependency to your project:
```
<dependency>
    <groupId>org.entur.auth.client</groupId>
    <artifactId>oidc-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage

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

## Contributing
Contributions are welcome! See [CONTRIBUTING](/CONTRIBUTING.md) for details.

## License
This project is licensed under the EUPL-1.2 license. See [LICENSE](/README.md) for details.