package org.entur.auth.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a field can handle an access token.
 * <p>
 * This annotation is typically used to indicate that a field can handle an access token,
 * which may be required for authentication or authorization in an application.
 * </p>
 *
 * <p>
 * The optional {@code value} parameter can be used to specify accessor for AccessTokenFactory to be used for handling the access token.
 * </p>
 *
 * <p><b>Usage:</b></p>
 * <pre>
 * public class AuthData {
 *     {@literal @}AccessToken("auth0")
 *     private RestTemplate restTemplate;
 * }
 * </pre>
 *
 * @see ElementType#FIELD
 * @see RetentionPolicy#RUNTIME
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessToken {

    /**
     * Optional metadata associated with the access token.
     * <p>
     * This can be used to specify accessor for AccessTokenFactory.
     * </p>
     *
     * @return a descriptive string (default is an empty string).
     */
    String value() default ""; // Optional parameter with a default empty string
}