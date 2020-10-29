package com.oauthgateway.gateway.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 23:27
 */
@Entity
@Data
@Table(name = "client_route")
public class ClientRoute implements Serializable,Cloneable {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "client_id")
    private String clientId;
    @Column(name = "route_id")
    private String routeId;

}
