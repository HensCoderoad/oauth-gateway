package com.oauthgateway.gateway.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 23:21
 */
@Entity
@Data
@Table(name="route")
public class Route implements Cloneable, Serializable {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "route_id")
    private String routeId;
    @Column(name = "order")
    private Integer order;
    @Column(name = "uri")
    private String uri;
    @Column(name = "predicates")
    private String predicates;
    @Column(name = "filters")
    private String filters;
    @Column(name = "update_time")
    private Date updateTime;
    @Column(name = "create_time")
    private Date createTime;


}
