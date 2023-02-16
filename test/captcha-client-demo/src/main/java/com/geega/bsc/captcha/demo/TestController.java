package com.geega.bsc.captcha.demo;/*
 * Copyright (c) 2019, ABB and/or its affiliates. All rights reserved.
 * ABB PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import com.geega.bsc.captcha.client.starter.service.CaptchaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * com.geega.bsc.captcha.demo.TestController
 *
 * @author Jun.An3
 * @date 2021/12/08
 */
@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @Autowired
    private CaptchaClient captchaClient;

    @GetMapping("/get")
    public void get(@RequestParam(value = "verification") String verification) {
        Boolean verify = captchaClient.verify(verification);
        System.out.println("verify=" + verify);
    }

}
