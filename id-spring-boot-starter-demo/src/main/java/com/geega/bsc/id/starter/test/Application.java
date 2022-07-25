package com.geega.bsc.id.starter.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Jun.An3
 * @date 2022/07/20
 */
@SpringBootApplication
@ComponentScan(value = {"com.geega"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
