package com.oauthgateway.oauth.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.oauthgateway.oauth.domain.Client;
import com.oauthgateway.oauth.repository.ClientRespository;
import com.oauthgateway.oauth.utils.IdUtil;
import com.oauthgateway.oauth.utils.SecretUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 9:37
 */
@Slf4j
@Service
public class ClientServiceImpl implements ClientService {
    @Autowired
    private ClientRespository clientRespository;
    @Autowired
    private ObjectMapper objectMapper;

    private final static Logger LOGGER = LoggerFactory.getLogger(ClientServiceImpl.class);

    private Cache<String, ClientDetails> clientDetailsCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .concurrencyLevel(10)
            .build();

    private IdUtil idWorker = new IdUtil(1, 1);


    @Override
    public Client add() {
        String id =String.valueOf(idWorker.nextId());
        Client client = new Client();
        client.setClientId(id);
        client.setClientSecret(SecretUtil.next(id));
        clientRespository.save(client);
        return client;
    }

    @Override
    public void invalidate(String clientId) {
        Optional.ofNullable(clientId)
                .filter(x -> !StringUtils.isEmpty(clientId))
                .ifPresent(x -> {
                    if("all".equals(x)){
                        clientDetailsCache.invalidateAll();
                    }else{
                        clientDetailsCache.invalidate(x);
                    }
                });
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        try {
            ClientDetails clientDetails = clientDetailsCache.get(clientId, ()-> {
                BaseClientDetails baseClientDetails = null;
                Optional<Client> opt = clientRespository.findById(clientId);
                if (opt.isPresent()) {
                    Client client = opt.get();
                    baseClientDetails = new BaseClientDetails(
                            client.getClientId(),
                            client.getResourceIds(),
                            client.getScope(),
                            client.getAuthorizedGrantTypes(),
                            client.getAuthorities(),
                            client.getWebServerRedirectUri()
                    );
                    baseClientDetails.setClientSecret(client.getClientSecret());
                    if (client.getAccessTokenValidity() != null) {
                        baseClientDetails.setAccessTokenValiditySeconds(client.getAccessTokenValidity());
                    }
                    if (client.getRefreshTokenValidity() != null) {
                        baseClientDetails.setRefreshTokenValiditySeconds(client.getRefreshTokenValidity());
                    }
                    if (client.getAdditionalInformation() != null) {
                        try {
                            Map<String, Object> additionalInformation = objectMapper.readValue(client.getAdditionalInformation(), new TypeReference<Map<String, Object>>() {
                            });
                            baseClientDetails.setAdditionalInformation(additionalInformation);
                        } catch (Exception e) {
                            LOGGER.warn("Could not decode JSON for additional information: " + client, e);
                        }
                    }
                    String autoApprove = client.getAutoapprove();
                    if (autoApprove != null) {
                        baseClientDetails.setAutoApproveScopes(StringUtils.commaDelimitedListToSet(autoApprove));
                    }
                }else{

                }
                return baseClientDetails;
            });
            if(clientDetails==null){
                clientDetailsCache.invalidate(clientId);
            }
            return clientDetails;
        } catch (ExecutionException e) {
            LOGGER.error("load client from db error: " ,e);
        }

        return null;
    }
}