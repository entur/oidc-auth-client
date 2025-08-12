package org.entur.auth.client;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;

/**
 * Utility class responsible for processing beans annotated with {@link AccessToken} and injecting
 * customized {@link RestTemplate} instances that automatically include bearer tokens.
 */
class AccessTokenProcessor {
    /**
     * Processes the given bean before initialization by injecting a custom {@link RestTemplate}
     * if the bean contains fields annotated with {@link AccessToken}.
     *
     * @param applicationContext the Spring application context
     * @param bean the bean instance being initialized
     * @param beanName the name of the bean
     * @return the processed bean
     */
    public static Object postProcessBeforeInitialization(ApplicationContext applicationContext, Object bean, @NotNull String beanName) {
        Class<?> clazz = bean.getClass();
        ReflectionUtils.doWithFields(clazz, field -> {
            if (field.isAnnotationPresent(AccessToken.class)) {
                if (AccessTokenFactory.class.isAssignableFrom(field.getType())) {
                    AccessTokenAnnotationAccessTokenFactoryProcessor.inject(applicationContext, bean, field);
                } else if (isAssignableFrom(applicationContext.getClassLoader(), field.getType())) {
                    AccessTokenAnnotationRestTemplateProcessor.inject(applicationContext, bean, field);
                }
            }
        });

        return bean;
    }

    private static boolean isAssignableFrom(ClassLoader classLoader,
                                            Class<?> targetType) {

        var candidateClassName = "org.springframework.web.client.RestTemplate";
        try {
            ClassLoader ccl = Thread.currentThread().getContextClassLoader();
            Class<?> candidate = Class.forName(candidateClassName, false, ccl == null ? classLoader : ccl);
            return candidate.isAssignableFrom(targetType);
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }

    /**
     * Internal processor responsible for injecting {@link AccessTokenFactory} instances into fields annotated with {@link AccessToken}.
     */
    static class AccessTokenAnnotationAccessTokenFactoryProcessor {
        /**
         * Injects a {@link AccessTokenFactory} into the specified field of the given bean.
         *
         * @param applicationContext the Spring application context
         * @param bean the bean instance containing the annotated field
         * @param field the field to inject with a customized {@link AccessTokenFactory}
         */
        public static void inject(ApplicationContext applicationContext, Object bean, Field field) {
            ReflectionUtils.makeAccessible(field);
            AccessToken annotation = field.getAnnotation(AccessToken.class);
            AccessTokenFactory accessTokenFactory = AccessTokenAnnotationRestTemplateProcessor.getAccessTokenFactory(applicationContext, annotation.value());
            ReflectionUtils.setField(field, bean, accessTokenFactory);
        }

    }

    /**
     * Internal processor responsible for injecting {@link RestTemplate} instances into fields annotated with {@link AccessToken}.
     */
    static class AccessTokenAnnotationRestTemplateProcessor {
        /**
         * Injects a {@link RestTemplate} into the specified field of the given bean.
         *
         * @param applicationContext the Spring application context
         * @param bean the bean instance containing the annotated field
         * @param field the field to inject with a customized {@link RestTemplate}
         */
        public static void inject(ApplicationContext applicationContext, Object bean, Field field) {
            ReflectionUtils.makeAccessible(field);
            AccessToken annotation = field.getAnnotation(AccessToken.class);
            AccessTokenFactory accessTokenFactory = getAccessTokenFactory(applicationContext, annotation.value());

            RestTemplate restTemplate = new RestTemplateBuilder()
                    .interceptors(Collections.singletonList(new AccessTokenAnnotationRestTemplateProcessor.BearerTokenInterceptor(accessTokenFactory)))
                    .build();
            ReflectionUtils.setField(field, bean, restTemplate);
        }

        /**
         * Retrieves the appropriate {@link AccessTokenFactory} bean from the application context.
         *
         * @param applicationContext the Spring application context
         * @param name an optional qualifier for selecting a specific {@link AccessTokenFactory} bean
         * @return the resolved {@link AccessTokenFactory} bean
         * @throws IllegalStateException if no suitable {@link AccessTokenFactory} bean is found
         */
        public static AccessTokenFactory getAccessTokenFactory(ApplicationContext applicationContext, String name) {
            try {
                if (name == null || name.isBlank()) {
                    return applicationContext.getBean(AccessTokenFactory.class);
                } else {
                    return applicationContext.getBean(name, AccessTokenFactory.class);
                }
            } catch (BeansException e) {
                throw new IllegalStateException("No AccessTokenFactory bean found for qualifier: " + name, e);
            }
        }

        /**
         * Custom interceptor that appends an Authorization header with a bearer token to each HTTP request.
         */
        private record BearerTokenInterceptor(
                AccessTokenFactory accessTokenFactory
        ) implements ClientHttpRequestInterceptor {

            /**
             * Intercepts an HTTP request to inject a bearer token into the Authorization header.
             *
             * @param request the HTTP request
             * @param body the request body
             * @param execution the request execution chain
             * @return the HTTP response
             * @throws IOException if an I/O error occurs
             */
            @NotNull
            @Override
            public ClientHttpResponse intercept(HttpRequest request, @NotNull byte[] body, ClientHttpRequestExecution execution) throws IOException {
                request.getHeaders().setBearerAuth(accessTokenFactory.getAccessToken());
                return execution.execute(request, body);
            }
        }
    }
}
