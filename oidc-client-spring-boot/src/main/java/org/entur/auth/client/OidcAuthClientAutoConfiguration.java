package org.entur.auth.client;


import org.entur.auth.client.properties.OidcAuthClientAuth0Properties;
import org.entur.auth.client.properties.OidcAuthClientProperties;
import org.entur.auth.client.properties.OidcAuthClientsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Map.Entry;

@Configuration
@EnableConfigurationProperties({OidcAuthClientsProperties.class, OidcAuthClientProperties.class})
public class OidcAuthClientAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(OidcAuthClientAutoConfiguration.class);

    /*
     * Creates a BeanDefinitionRegistryPostProcessor for configuring clients.
     */
    @Bean
    public static BeanDefinitionRegistryPostProcessor authClients() {
        return new Auth0DynamicBeanDefinition();
    }

    /*
     * Logs the configuration details of a client.
     */
    private static void logClientConfiguration(String name, OidcAuthClientAuth0Properties oidcAuthProperties, Long mustRefreshThreshold, Long shouldRefreshThreshold, Long minThrottleTime, Long maxThrottleTime) {
        log.info("Starting Client configuration: {}", name);
        log.info("Client client ID: {}", oidcAuthProperties.getClientId());
        if (oidcAuthProperties.getSecret() == null || oidcAuthProperties.getSecret().isBlank()) {
            log.warn("Client secret is missing, please check your configuration.");
        }
        log.info("Client domain: {}", oidcAuthProperties.getDomain());
        log.info("Client audience: {}", oidcAuthProperties.getAudience());
        log.info("Client must refresh threshold: {}", oidcAuthProperties.getMustRefreshThreshold() == null ? mustRefreshThreshold : oidcAuthProperties.getMustRefreshThreshold());
        log.info("Client should refresh threshold: {}", oidcAuthProperties.getShouldRefreshThreshold() == null ? shouldRefreshThreshold : oidcAuthProperties.getShouldRefreshThreshold());
        log.info("Client min throttle time: {}", oidcAuthProperties.getMinThrottleTime() == null ? minThrottleTime : oidcAuthProperties.getMinThrottleTime());
        log.info("Client max throttle time: {}", oidcAuthProperties.getMaxThrottleTime() == null ? maxThrottleTime : oidcAuthProperties.getMaxThrottleTime());
    }

    /*
     * Configures and returns an AccessTokenFactory bean from client configuration.
     */
    @Bean("auth0")
    @ConditionalOnProperty(name = {"entur.client.auth0.clientId"})
    @ConditionalOnMissingBean(AccessTokenFactory.class)
    public AccessTokenFactory auth0(OidcAuthClientProperties properties) {
        OidcAuthClientAuth0Properties oidcAuthProperties = properties.getAuth0();

        logClientConfiguration("auth0", oidcAuthProperties, null, null, null, null);
        return new AccessTokenFactoryBuilder()
                .withDomain(oidcAuthProperties.getDomain())
                .withClientSecret(oidcAuthProperties.getSecret())
                .withClientId(oidcAuthProperties.getClientId())
                .withAudience(oidcAuthProperties.getAudience())
                .withMustRefreshThreshold(oidcAuthProperties.getMustRefreshThreshold())
                .withShouldRefreshThreshold(oidcAuthProperties.getShouldRefreshThreshold())
                .withMinThrottleTime(oidcAuthProperties.getMinThrottleTime())
                .withMaxThrottleTime(oidcAuthProperties.getMaxThrottleTime())
                .buildAuth0();
    }

    /*
     * BeanDefinitionRegistryPostProcessor for dynamically registering beans based on clients configurations.
     */
    public static class Auth0DynamicBeanDefinition implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {
        private OidcAuthClientsProperties properties;

        @Override
        public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory factory) throws BeansException {/* Do nothing */}

        @Override
        public void setEnvironment(@Nullable Environment environment) {
            if (environment != null) {
                this.properties = Binder
                        .get(environment)
                        .bind("entur.clients", OidcAuthClientsProperties.class)
                        .orElse(new OidcAuthClientsProperties());
            }
        }

        @Override
        public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry registry) throws BeansException {
            Map<String, OidcAuthClientAuth0Properties> clients = properties.getAuth0();

            for (Entry<String, OidcAuthClientAuth0Properties> entry : clients.entrySet()) {
                registry.registerBeanDefinition(entry.getKey(), BeanDefinitionBuilder
                        .rootBeanDefinition(AccessTokenFactory.class)
                        .setFactoryMethodOnBean("createInstance", "dynamicBeanAccessTokenFactory")
                        .addConstructorArgValue(entry.getKey())
                        .getBeanDefinition());
            }
        }
    }

    /*
     * Factory for creating AccessTokenFactory beans dynamically.
     */
    @Component("dynamicBeanAccessTokenFactory")
    static class DynamicBeanAccessTokenFactory {
        private final OidcAuthClientsProperties clientsProperties;

        DynamicBeanAccessTokenFactory(OidcAuthClientsProperties clientsProperties) {
            this.clientsProperties = clientsProperties;
        }

        AccessTokenFactory createInstance(String beanId) {
            var beanProperties = clientsProperties.getAuth0().get(beanId);
            logClientConfiguration(beanId, beanProperties, clientsProperties.getMustRefreshThreshold(), clientsProperties.getShouldRefreshThreshold(), clientsProperties.getMinThrottleTime(), clientsProperties.getMaxThrottleTime());

            return new AccessTokenFactoryBuilder()
                    .withDomain(beanProperties.getDomain())
                    .withClientSecret(beanProperties.getSecret())
                    .withClientId(beanProperties.getClientId())
                    .withAudience(beanProperties.getAudience())
                    .withMustRefreshThreshold(beanProperties.getMustRefreshThreshold() != null ? beanProperties.getMustRefreshThreshold() : clientsProperties.getMustRefreshThreshold())
                    .withShouldRefreshThreshold(beanProperties.getShouldRefreshThreshold() != null ? beanProperties.getShouldRefreshThreshold() : clientsProperties.getShouldRefreshThreshold())
                    .withMinThrottleTime(beanProperties.getMinThrottleTime() != null ? beanProperties.getMinThrottleTime() : clientsProperties.getMinThrottleTime())
                    .withMaxThrottleTime(beanProperties.getMaxThrottleTime() != null ? beanProperties.getMaxThrottleTime() : clientsProperties.getMaxThrottleTime())
                    .buildAuth0();
        }
    }
}
