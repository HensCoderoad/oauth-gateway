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
 * @create 2020-10-28 11:59
 */
@Entity
@Data
@Table(name="client")
public class Client implements Serializable,Cloneable {
    /** 客户端ID **/
    @Id
    @Column(name = "client_id")
    private String clientId;
    /** 客户端密钥 **/
    @Column(name = "client_secret")
    private String clientSecret;
    /** 可访问资源 **/
    @Column(name = "resource_ids")
    private String resourceIds;
    /** 范围 **/
    @Column(name = "scope")
    private String scope;
    /** 授权类型;authorization_code,implicit,password,client_credentials,refresh_token **/
    @Column(name = "authorized_grant_types")
    private String authorizedGrantTypes;
    /** 跳转地址 **/
    @Column(name = "web_server_redirect_uri")
    private String webServerRedirectUri;
    /** 权限列表 **/
    @Column(name = "authorities")
    private String authorities;
    /** access_token过期时间 **/
    @Column(name = "access_token_validity")
    private Integer accessTokenValidity;
    /** refresh_token过期时间 **/
    @Column(name = "refresh_token_validity")
    private Integer refreshTokenValidity;
    /** 附加信息;json格式 **/
    @Column(name = "additional_information")
    private String additionalInformation;
    /** 自动同意;自动同意：true **/
    @Column(name = "autoapprove")
    private String autoapprove;
}
