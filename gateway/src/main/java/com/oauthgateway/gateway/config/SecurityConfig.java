package com.oauthgateway.gateway.config;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 11:57
 */

import com.oauthgateway.gateway.handler.AccessDeniedHandler;
import com.oauthgateway.gateway.handler.AuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private AccessDeniedHandler accessDeniedHandler;
    private AuthenticationEntryPoint authenticationEntryPoint;
    private GatewayCustomProperties gatewayCustomProperties;
    public SecurityConfig(AccessDeniedHandler accessDeniedHandler, AuthenticationEntryPoint authenticationEntryPoint, GatewayCustomProperties gatewayCustomProperties) {
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.gatewayCustomProperties = gatewayCustomProperties;
    }
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.csrf().disable();

        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

        http.authorizeExchange()
                .pathMatchers(
                        gatewayCustomProperties.getIgnoredPath().toArray(new String[0])
                ).permitAll()
                .anyExchange().authenticated();

        http.oauth2ResourceServer()
                .bearerTokenConverter(bearerTokenConverter())
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
                .jwt().jwtDecoder(jwtDecoder());

        return http.build();
    }

    @Bean
    public ServerAuthenticationConverter bearerTokenConverter() {
        ServerBearerTokenAuthenticationConverter authenticationConverter = new ServerBearerTokenAuthenticationConverter();
        authenticationConverter.setAllowUriQueryParameter(true);

        return authenticationConverter;
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        SecretKeySpec key = new SecretKeySpec("himarking@mdmhimarking@mdmhimarking@mdm".getBytes(), "HMacSHA256");
        return NimbusReactiveJwtDecoder.withSecretKey(key).build();
    }
}
