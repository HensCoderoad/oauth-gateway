package com.oauthgateway.gateway.routes;

import com.oauthgateway.gateway.domain.Route;
import com.oauthgateway.gateway.service.RouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.synchronizedMap;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 12:40
 */
@Slf4j
@Component
public class MemoryRouteDefinitionRepository implements RouteDefinitionRepository {
    private Map<String, RouteDefinition> routes = synchronizedMap(new LinkedHashMap<>());

    private String reg = "\\r?\\n";
    @Autowired
    private RouteService routeService;

    @PostConstruct
    public void refresh() {
        log.info("memory route refresh.");
        List<Route> srvResource = routeService.getAll();
        if (CollectionUtils.isEmpty(srvResource)) {
            log.info("route service is empty.");
            return;
        }

        Map<String, RouteDefinition> routesTemp = synchronizedMap(new LinkedHashMap<>());

        srvResource.forEach(srv -> {
            RouteDefinition definition = new RouteDefinition();
            definition.setId(srv.getRouteId());
            URI uri = UriComponentsBuilder.fromHttpUrl(srv.getUri()).build().toUri();
            definition.setUri(uri);
            definition.setOrder(srv.getOrder());

            // 断言
            List<PredicateDefinition> predicateDefinitions = new ArrayList<>();
            if (!StringUtils.isEmpty(srv.getPredicates())) {
                String[] predicates = srv.getPredicates().split(reg);
                Stream.of(predicates).forEach(predicate -> {
                    predicateDefinitions.add(new PredicateDefinition(predicate));
                });
            }

            // 过滤器
            List<FilterDefinition> filterDefinitions = new ArrayList<>();
            if (!StringUtils.isEmpty(srv.getFilters())) {
                String[] filters = srv.getFilters().split(reg);
                Stream.of(filters).forEach(filter -> {
                    filterDefinitions.add(new FilterDefinition(filter));
                });
            }

            definition.setFilters(filterDefinitions);
            definition.setPredicates(predicateDefinitions);

            routesTemp.put(srv.getRouteId(), definition);
        });

        this.routes = routesTemp;
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return route.flatMap(r -> {
            routes.put(r.getId(), r);
            return Mono.empty();
        });
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return routeId.flatMap(id -> {
            if (routes.containsKey(id)) {
                routes.remove(id);
                return Mono.empty();
            }
            return Mono.defer(() -> Mono.error(
                    new NotFoundException("RouteDefinition not found: " + routeId)));
        });
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return Flux.fromIterable(routes.values());
    }

}
