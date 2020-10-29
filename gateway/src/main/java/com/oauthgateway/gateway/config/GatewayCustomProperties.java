package com.oauthgateway.gateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 11:57
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties("gateway.custom")
public class GatewayCustomProperties {
    private List<String> ignoredPath;

    private String password;
}
