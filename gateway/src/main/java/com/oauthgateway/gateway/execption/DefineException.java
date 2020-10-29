package com.oauthgateway.gateway.execption;

import lombok.Getter;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 12:04
 */
@Getter
public class DefineException extends RuntimeException{
    private Integer code;
    private String msg;

    public DefineException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
