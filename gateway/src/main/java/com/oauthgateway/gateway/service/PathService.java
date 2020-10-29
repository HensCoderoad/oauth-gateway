package com.oauthgateway.gateway.service;

import java.util.Map;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 12:10
 */
public interface PathService {
    void refresh();
    Map<String, Object> cacheMap();
    void clearRequestCache();
    String lookup(String clientId, String requestPath);
}
