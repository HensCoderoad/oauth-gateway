package com.oauthgateway.gateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 9:26
 */
@NoRepositoryBean
public interface BaseRespository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
}
