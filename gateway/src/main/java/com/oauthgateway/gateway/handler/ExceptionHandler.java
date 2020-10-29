package com.oauthgateway.gateway.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oauthgateway.gateway.domain.R;
import com.oauthgateway.gateway.execption.DefineException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 12:03
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@Slf4j
public class ExceptionHandler implements ErrorWebExceptionHandler {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        return Mono.defer(() -> Mono.just(exchange.getResponse()))
                .flatMap(response -> {
                    String jsonResponse = "";
                    try {
                        if (ex instanceof DefineException) {
                            DefineException de = (DefineException) ex;
                            jsonResponse = objectMapper.writeValueAsString(R.fail().code(de.getCode()).msg(de.getMsg()));
                        } else {
                            jsonResponse = objectMapper.writeValueAsString(R.fail().code(401).msg(String.format("exception: %s", ex.getMessage())));
                        }
                    } catch (JsonProcessingException exIgnore) {
                        // swallow the exception
                    }
                    response.setStatusCode(HttpStatus.OK);
                    DataBufferFactory dataBufferFactory = response.bufferFactory();
                    DataBuffer buffer = dataBufferFactory.wrap(jsonResponse.getBytes(
                            Charset.forName("UTF-8")));
                    return response.writeWith(Mono.just(buffer))
                            .doOnError(error -> DataBufferUtils.release(buffer));
                });
    }
}
