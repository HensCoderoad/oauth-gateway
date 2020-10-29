package com.oauthgateway.gateway.service.impl;

import com.oauthgateway.gateway.domain.ClientRoute;
import com.oauthgateway.gateway.repository.ClientRouteResponsitory;
import com.oauthgateway.gateway.service.ClientRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 23:31
 */
@Service
public class ClientRouteServiceImpl implements ClientRouteService {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private ClientRouteResponsitory clientRouteResponsitory;

    @Override
    public List<ClientRoute> getAll() {
        return clientRouteResponsitory.findAll();
    }
}
