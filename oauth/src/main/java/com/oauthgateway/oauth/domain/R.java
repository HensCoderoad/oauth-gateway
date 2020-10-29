package com.oauthgateway.oauth.domain;

import lombok.Data;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 10:07
 */
@Data
public class R {

    public static final int SUCCESS = 0;
    public static final int FAIL = 1;

    /**
     * 状态码，0:正常，非0:异常
     */
    private Integer code = SUCCESS;

    /**
     * 消息
     */
    private String msg = "success";

    public R code(Integer code) {
        this.code = code;
        return this;
    }

    public R msg(String msg) {
        this.msg = msg;
        return this;
    }

    // 通用快速构造方法
    /**
     * 普通状态
     */
    public static R success() {
        return new R().code(SUCCESS);
    }

    /**
     * 错误状态
     */
    public static R fail() {
        return new R().code(FAIL);
    }

    /**
     * 单个对象
     */
    public static <T> S single(T data) {
        return (S) new S<>().data(data);
    }

    /**
     * 列表对象
     */
    public static <T> L list(T data) {
        return (L) new L<>().data(data);
    }

}
