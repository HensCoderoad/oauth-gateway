package com.oauthgateway.oauth.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oauthgateway.oauth.domain.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 10:06
 */
@Slf4j
@Component
public class AuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {
   @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        String jsonResponse = "";
        try {
            jsonResponse = objectMapper.writeValueAsString(R.fail().code(401).msg(String.format("authentication exception: %s", e.getMessage())));
        } catch (JsonProcessingException exIgnore) {
            // swallow the exception
        }

        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        httpServletResponse.getWriter().write(jsonResponse);
        httpServletResponse.getWriter().flush();

        httpServletResponse.getWriter().close();
    }
}
