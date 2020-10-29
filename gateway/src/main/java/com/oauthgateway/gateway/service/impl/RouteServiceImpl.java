package com.oauthgateway.gateway.service.impl;

import com.oauthgateway.gateway.domain.Route;
import com.oauthgateway.gateway.repository.RouteResponsitory;
import com.oauthgateway.gateway.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 23:35
 */
@Service
public class RouteServiceImpl implements RouteService {
    @Autowired
    private RouteResponsitory routeResponsitory;

    @Override
    public List<Route> getAll() {
        return routeResponsitory.findAll();
    }
}
