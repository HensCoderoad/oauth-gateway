package com.oauthgateway.gateway.domain;

import lombok.Data;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 10:08
 */
@Data
public class S<T> extends R {

    /**
     * 返回数据
     */
    private T data;

    public S() {
        super();
    }

    public R data(T data) {
        this.data = data;
        return this;
    }
}
