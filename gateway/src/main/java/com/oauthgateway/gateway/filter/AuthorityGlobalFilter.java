package com.oauthgateway.gateway.filter;

import com.oauthgateway.gateway.config.GatewayCustomProperties;
import com.oauthgateway.gateway.execption.DefineException;
import com.oauthgateway.gateway.service.PathService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 12:08
 */
@Component
@Slf4j
public class AuthorityGlobalFilter implements GlobalFilter, Ordered {
    private static final String DIFF = "=====>";

    private final PathService pathService;

    private final GatewayCustomProperties gatewayPathProperties;

    private AntPathMatcher pathMatcher = new AntPathMatcher();

    public AuthorityGlobalFilter(GatewayCustomProperties gatewayCustomProperties, PathService pathService) {
        this.gatewayPathProperties = gatewayCustomProperties;
        this.pathService = pathService;
    }
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        boolean pass = false; // 白名单
        for (String ignored : gatewayPathProperties.getIgnoredPath()) {
            if (pathMatcher.match(ignored, exchange.getRequest().getPath().value())) {
                pass = true; break;
            }
        }
        if (pass) {
            return chain.filter(exchange);
        }

        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(new DefineException(10003, "security context error.")))
                .map(context -> {
                    Authentication authentication = context.getAuthentication();
                    if (!authentication.isAuthenticated()) {
                        throw new DefineException(10001, "authenticate error.");
                    }

                    Object principal = authentication.getPrincipal();
                    if (!(principal instanceof Jwt))
                    {
                        throw new DefineException(10002, "principal error.");
                    }
                    Jwt jwt = (Jwt) principal;

                    String clientId = (String) jwt.getClaims().get("client_id");
                    log.info("{} client id: {}", DIFF, clientId);

                    String originPath = exchange.getRequest().getPath().value();
                    String rewritePath = pathService.lookup(clientId, originPath);

                    if (StringUtils.isEmpty(rewritePath)) {
                        throw new DefineException(10000, "unauthorized api");
                    }

                    log.info("{} rewritePath: {} -> {}", DIFF, originPath, rewritePath);

                    ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
                    // 重写路径
                    builder.path(rewritePath);
                    // 应用ID头
                    builder.header("X-Client-Id", clientId);
                    ServerHttpRequest request = builder.build();

                    return exchange.mutate().request(request).build();
                }).flatMap(chain::filter);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
