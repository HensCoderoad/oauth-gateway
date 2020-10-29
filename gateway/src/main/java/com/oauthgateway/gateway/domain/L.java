package com.oauthgateway.gateway.domain;

import lombok.Data;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 10:09
 */
@Data
public class L<T> extends S<T> {

    public L() {
        super();
    }

    /**
     * 列表总数量
     */
    private long total = 0;

    /**
     * 列表数量
     */
    private long count = 0;

    /**
     * 列表页码
     */
    private long page = 1;

    /**
     * 列表页大小
     */
    private long pageSize = 20;

    public L total(long total) {
        this.total = total;
        return this;
    }

    public L count(long count) {
        this.count = count;
        return this;
    }

    public L page(long page) {
        this.page = page;
        return this;
    }

    public L pageSize(long pageSize) {
        this.pageSize = pageSize;
        return this;
    }

}
