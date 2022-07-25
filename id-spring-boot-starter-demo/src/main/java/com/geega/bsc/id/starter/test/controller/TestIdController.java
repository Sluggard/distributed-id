package com.geega.bsc.id.starter.test.controller;

import com.geega.bsc.id.client.IdClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller
 *
 * @author Jun.An3
 * @date 2022/05/11
 */
@RestController
@RequestMapping("/api/v1/id")
public class TestIdController {

    @Autowired
    private IdClient idClient;

    @GetMapping(value = "/one")
    public Long id() {
        return idClient.id();
    }

}
