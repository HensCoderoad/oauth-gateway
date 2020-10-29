package com.oauthgateway.gateway.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oauthgateway.gateway.domain.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 12:02
 */
@Slf4j
@Component
public class AuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        return Mono.defer(() -> {

            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.OK);

            String jsonResponse = "";
            try {
                jsonResponse = objectMapper.writeValueAsString(R.fail().code(401).msg(String.format("authentication exception: %s", e.getMessage())));
            } catch (JsonProcessingException ex) {
                // swallow the exception
            }

            DataBufferFactory dataBufferFactory = response.bufferFactory();
            DataBuffer buffer = dataBufferFactory.wrap(jsonResponse.getBytes(
                    Charset.forName("UTF-8")));

            return response.writeWith(Mono.just(buffer))
                    .doOnError(error -> DataBufferUtils.release(buffer));
        });
    }
}
