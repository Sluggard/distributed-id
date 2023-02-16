/*
 * Copyright (c) 2019, ABB and/or its affiliates. All rights reserved.
 * ABB PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * DemoApplication
 *
 * @author Jun.An3
 * @date 2021/12/08
 */
@SpringBootApplication
@ComponentScan(value = "com.geega.bsc.captcha.demo")
//@EnableFeignClients(basePackages = "com.geega.bsc")
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
