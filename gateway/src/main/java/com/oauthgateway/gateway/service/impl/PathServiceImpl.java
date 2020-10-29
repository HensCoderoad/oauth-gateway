package com.oauthgateway.gateway.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.oauthgateway.gateway.domain.ClientRoute;
import com.oauthgateway.gateway.domain.Route;
import com.oauthgateway.gateway.execption.DefineException;
import com.oauthgateway.gateway.service.ClientRouteService;
import com.oauthgateway.gateway.service.PathService;
import com.oauthgateway.gateway.service.RouteService;
import net.bytebuddy.asm.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 12:35
 */
@Service
public class PathServiceImpl implements PathService {
    /*路径匹配器*/
    private AntPathMatcher pathMatcher = new AntPathMatcher();
    private String reg = "\\r?\\n";
    private Map<String, Set<String>> gwClientResource = new ConcurrentHashMap<>();
    private Map<String, String> gwResource = new ConcurrentHashMap<>();

    private Cache<String, Boolean> requestCache = CacheBuilder.newBuilder()
            .maximumSize(50000)
            .expireAfterWrite(Duration.ofMinutes(5))
            .build();
    @Autowired
    private RouteService routeService;
    @Autowired
    private ClientRouteService clientRouteService;

    private final static Logger LOGGER = LoggerFactory.getLogger(PathServiceImpl.class);

    private String normalPath(String text) {
        int eqIdx = text.indexOf('=');
        if (eqIdx <= 0) {
            return null;
        }

        int endIdx = text.indexOf(',');
        if (endIdx <= 0){
            return text.substring(eqIdx + 1);
        }
        else{
            return text.substring(endIdx + 1, endIdx);
        }
    }

    @PostConstruct
    @Override
    public void refresh() {
        // 刷新资源
        List<Route> routes = routeService.getAll();
        Map<String, String> resourceTemp = new ConcurrentHashMap<>();
        routes.forEach(x -> {
            if(!StringUtils.isEmpty(x.getPredicates())){
                Stream.of(x.getPredicates().split(reg)).forEach( p ->{
                    String normalPath = normalPath(p);
                    if(!StringUtils.isEmpty(normalPath)){
                        resourceTemp.put(x.getRouteId(),normalPath);
                    }
                });
            }
        });
        System.out.println(resourceTemp);
        gwResource = resourceTemp;

        // 刷新权限
        List<ClientRoute> clientRoutes = clientRouteService.getAll();
        clientRoutes.forEach(System.out::println);
        Map<String,Set<String>> clientRoutesTemp = new ConcurrentHashMap<>();
        clientRoutes.forEach(c ->{
            Set<String> strings = clientRoutesTemp.computeIfAbsent(c.getClientId(), r -> new HashSet<>());
            strings.add(c.getRouteId());
        });
        gwClientResource = clientRoutesTemp;
        System.out.println(gwClientResource);
    }

    @Override
    public Map<String, Object> cacheMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("resource", this.gwResource);
        map.put("client_resource", this.gwClientResource);
        return map;
    }

    @Override
    public void clearRequestCache() {
        requestCache.invalidateAll();
    }

    @Override
    public String lookup(String clientId, String requestPath) {
        Boolean cached = requestCache.getIfPresent(String.format("%s:%s", clientId, requestPath));
        if (cached == null || !cached) {
            // 第一步，检查客户端分组
            Set<String> resources = gwClientResource.get(clientId);
            if (CollectionUtils.isEmpty(resources)) {
                LOGGER.info("client_id not matched. client_id: {}", clientId);
                throw new DefineException(20000, "client_id unauthorized.");
            }
            boolean matched = false;
            for (String resource : resources) {

                // *表示拥有所有接口权限
                if (!StringUtils.isEmpty(resource) && resource.equals("*")) {
                    LOGGER.info("global (*) matched. client_id: {}", clientId);
                    matched = true;
                    break;
                }

                String pattern = gwResource.get(resource);
                if (!StringUtils.isEmpty(pattern)) {

                    // 第三步，匹配路径
                    matched = pathMatcher.match(pattern, requestPath);
                    if (matched) {
                        LOGGER.info("client_id '{}' with request '{}' match pattern '{}'", clientId, requestPath, pattern);
                        break;
                    }
                }
                if (matched) {
                    break;
                }
            }

            if (!matched) {
                LOGGER.info("request not matched. client_id: {}, request: {}", clientId, requestPath);
                throw new DefineException(20000, "resource unauthorized.");
            } else {
                requestCache.put(String.format("%s:%s", clientId, requestPath), true);
            }
        } else {
            LOGGER.info("request hit cache. client_id: {}, request: {}", clientId, requestPath);
        }
        return requestPath;
    }
}
