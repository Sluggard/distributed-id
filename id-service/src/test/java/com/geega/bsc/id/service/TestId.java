package com.geega.bsc.id.service;

import com.geega.bsc.id.client.IdClient;
import com.geega.bsc.id.common.utils.TimeUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * @author Jun.An3
 * @date 2022/08/02
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {IdApplication.class})
public class TestId {

    @Autowired
    private IdClient idClient;

    @Test
    public void test() {
        long nullNum = 0;
        long idNum = 0;
        final long now = TimeUtil.now();
        for (int i = 0; i < 10000; i++) {
            final Long id = idClient.id();
            if (id == null) {
                System.out.println("为空数：" + ++nullNum);
            } else {
                System.out.println("ID数：" + ++idNum);
            }
        }
        System.out.println((TimeUtil.now() - now) / 1000);
    }

}
