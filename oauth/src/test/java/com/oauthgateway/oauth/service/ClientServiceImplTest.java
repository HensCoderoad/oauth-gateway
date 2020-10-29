package com.oauthgateway.oauth.service;

import com.oauthgateway.oauth.domain.Client;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author hens
 * @Description
 * @create 2020-10-28 10:23
 */
@SpringBootTest
@RunWith(SpringRunner.class)
class ClientServiceImplTest {
    @Autowired
    private ClientService clientService;

    @Test
    void add() {
        Client add = clientService.add();
        System.out.println(add);
    }
}