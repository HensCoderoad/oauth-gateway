package com.oauthgateway.oauth.config;

import com.oauthgateway.oauth.domain.R;
import com.oauthgateway.oauth.handler.AccessDeniedHandler;
import com.oauthgateway.oauth.handler.AuthenticationEntryPoint;
import com.oauthgateway.oauth.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 9:33
 */
@Configuration
public class OauthServerConfig extends AuthorizationServerConfigurerAdapter {

    private ClientService clientService;
    private AuthenticationEntryPoint authenticationEntryPoint;
    private AccessDeniedHandler accessDeniedHandler;

    public OauthServerConfig(ClientService clientService,
                             AuthenticationEntryPoint authenticationEntryPoint,
                             AccessDeniedHandler accessDeniedHandler) {
        this.clientService = clientService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients();
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientService);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.exceptionTranslator(jsonExceptionTranslator())
                .accessTokenConverter(accessTokenConverter())
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
    }

    @Bean
    public AccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        // RSA 签名
        // jwtAccessTokenConverter.setKeyPair(keyPair());

        // MAC 签名
        jwtAccessTokenConverter.setSigningKey("himarking@mdmhimarking@mdmhimarking@mdm");
        return jwtAccessTokenConverter;
    }

    @Bean
    public KeyPair keyPair() {
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("mdm-jwt-rsa.jks"), "himarking@mdm".toCharArray());
        return keyStoreKeyFactory.getKeyPair("jwt-rsa", "himarking@mdm".toCharArray());
    }

    @Bean
    public WebResponseExceptionTranslator jsonExceptionTranslator() {
        return ex -> ResponseEntity.ok().body(R.fail().code(401).msg(String.format("token exception: %s", ex.getMessage())));
    }
}
