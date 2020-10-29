package com.oauthgateway.oauth.controller;

import com.oauthgateway.oauth.domain.Client;
import com.oauthgateway.oauth.domain.R;
import com.oauthgateway.oauth.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 11:21
 */
@RestController
public class ClientController {
    @Autowired
    private ClientService clientService;
    @GetMapping("add")
    public R add(){
        return R.single(clientService.add());
    }
}
