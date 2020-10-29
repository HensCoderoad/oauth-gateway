package com.oauthgateway.oauth.service;

import com.oauthgateway.oauth.domain.Client;
import org.springframework.security.oauth2.provider.ClientDetailsService;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 9:36
 */
public interface ClientService extends ClientDetailsService {

    Client add();

    void invalidate(String clientId);

}
