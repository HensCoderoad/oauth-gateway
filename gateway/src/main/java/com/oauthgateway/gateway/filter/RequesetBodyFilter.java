package com.oauthgateway.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 12:21
 */
@Component
@Slf4j
public class RequesetBodyFilter implements GlobalFilter, Ordered {
    public static final String CACHE_REQUEST_BODY_OBJECT_KEY = "cachedRequestBodyObject";

    private static final List<HttpMessageReader<?>> messageReaders = HandlerStrategies
            .withDefaults().messageReaders();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ServerWebExchangeUtils.cacheRequestBodyAndRequest(exchange,
                (serverHttpRequest) -> ServerRequest
                        .create(exchange.mutate().request(serverHttpRequest)
                                .build(), messageReaders)
                        .bodyToMono(String.class)
                        .doOnNext(objectValue -> {
                                    exchange.getAttributes().put(CACHE_REQUEST_BODY_OBJECT_KEY, objectValue);
                                }
                        ).then(chain.filter(exchange)));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
