//package com.geega.bsc.captcha.demo.test;
//
//import com.geega.bsc.captch.common.enumeration.CaptchaTypeEnum;
//import com.geega.bsc.captcha.starter.service.CaptchaService;
//import com.geega.bsc.captch.common.vo.CaptchaVO;
//import com.geega.bsc.captcha.demo.StartApplication;
//import org.junit.Before;
//import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
///**
// * 针对同一个客户端组件的请求，做如下限制:
// * get
// * 1分钟内失败5次，锁定5分钟
// * 1分钟内不能超过120次。
// * check:
// * 1分钟内不超过600次
// * verify:
// * 1分钟内不超过600次
// *
// * @author WongBin
// * @date 2021/1/21
// */
//@SuppressWarnings("unused")
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = StartApplication.class)
//public class FrequencyLimitTest {
//
//    @Autowired
//    private CaptchaService captchaService;
//
//    private final CaptchaVO req = new CaptchaVO();
//    private final Logger logger = LoggerFactory.getLogger(getClass());
//    int cnt = 100;
//    private final String clientUid = "login-" + UUID.randomUUID();
//
//    @Before
//    public void init() {
//        req.setCaptchaType(CaptchaTypeEnum.BLOCK_PUZZLE.getCodeValue());
//        //req.setClientUid(clientUid);
//        req.setBrowserInfo("sssssssssssssssssss");
//    }
//
//    @Test
//    public void testGet() throws Exception {
//        int i = 0;
//        while (i++ < cnt) {
//            CaptchaVO res = captchaService.get(req);
//            logger.info(i + "=" + res);
//            TimeUnit.SECONDS.sleep(1);
//        }
//    }
//
//    @Test
//    public void testCheck() throws Exception {
//        int i = 0;
//        while (i++ < cnt) {
//            req.setToken("xddfdf" + i);
//            captchaService.check(req);
//            TimeUnit.SECONDS.sleep(1);
//        }
//    }
//
//    @Test
//    public void testVerify() {
//        int i = 0;
//        while (i++ < cnt) {
//            req.setToken("xddfdf" + i);
//            req.setCaptchaVerification("sdfddfdd");
//            captchaService.verify(null, null);
//        }
//    }
//
//}
