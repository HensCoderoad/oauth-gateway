package com.oauthgateway.oauth.handler;

import com.oauthgateway.oauth.domain.R;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 10:07
 */
@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public @ResponseBody
    R exp(HttpServletRequest request, Exception ex) {
        return R.fail().code(401).msg(String.format("exception: %s", ex.getMessage()));
    }
}
