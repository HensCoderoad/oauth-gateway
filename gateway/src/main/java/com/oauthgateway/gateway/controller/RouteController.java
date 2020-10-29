package com.oauthgateway.gateway.controller;

import com.oauthgateway.gateway.config.GatewayCustomProperties;
import com.oauthgateway.gateway.routes.MemoryRouteDefinitionRepository;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 12:42
 */
@RequestMapping("/gw/route")
@RestController
public class RouteController implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher publisher;
    private RouteLocator routeLocator;
    private GatewayCustomProperties gatewayCustomProperties;
    private MemoryRouteDefinitionRepository memoryRouteDefinitionRepository;

    public RouteController(
            RouteLocator routeLocator,
            GatewayCustomProperties gatewayCustomProperties,
            MemoryRouteDefinitionRepository memoryRouteDefinitionRepository
    ) {
        this.routeLocator = routeLocator;
        this.gatewayCustomProperties = gatewayCustomProperties;
        this.memoryRouteDefinitionRepository = memoryRouteDefinitionRepository;
    }

    @RequestMapping("")
    public Flux<Map<String, Object>> index(String password) {

        if (StringUtils.isEmpty(password) || !password.equals(gatewayCustomProperties.getPassword())) {
            return Flux.empty();
        }

        return this.routeLocator.getRoutes().map(this::serialize);
    }

    @GetMapping("/refresh")
    public Mono<Void> refresh(String password) {
        if (StringUtils.isEmpty(password) || !password.equals(gatewayCustomProperties.getPassword())) {
            return Mono.empty();
        }

        memoryRouteDefinitionRepository.refresh();

        this.publisher.publishEvent(new RefreshRoutesEvent(this));
        return Mono.empty();
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher =applicationEventPublisher;
    }

    Map<String, Object> serialize(Route route) {
        HashMap<String, Object> r = new HashMap<>();
        r.put("route_id", route.getId());
        r.put("uri", route.getUri().toString());
        r.put("order", route.getOrder());
        r.put("predicate", route.getPredicate().toString());
        if (!CollectionUtils.isEmpty(route.getMetadata())) {
            r.put("metadata", route.getMetadata());
        }

        ArrayList<String> filters = new ArrayList<>();

        for (int i = 0; i < route.getFilters().size(); i++) {
            GatewayFilter gatewayFilter = route.getFilters().get(i);
            filters.add(gatewayFilter.toString());
        }

        r.put("filters", filters);
        return r;
    }
}
