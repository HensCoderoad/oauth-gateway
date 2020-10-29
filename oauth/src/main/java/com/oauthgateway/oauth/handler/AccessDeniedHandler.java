package com.oauthgateway.oauth.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oauthgateway.oauth.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
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
@Component
public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {
    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        String jsonResponse = "";
        try {
            jsonResponse = objectMapper.writeValueAsString(R.fail().code(401).msg(String.format("access denied. exception: %s", e.getMessage())));
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
