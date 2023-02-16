//package com.geega.bsc.captcha.demo.test;
//
//import com.geega.bsc.captcha.starter.service.CaptchaService;
//import com.geega.bsc.captch.common.util.RandomUtils;
//import com.geega.bsc.captch.common.vo.CaptchaVO;
//import com.geega.bsc.captcha.demo.StartApplication;
//import com.geega.bsc.captcha.client.starter.service.impl.ClickWordCaptchaServiceImpl;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = StartApplication.class)
//public class CaptchaServiceImplTest {
//
//    @SuppressWarnings("unused")
//    @Autowired
//    private CaptchaService captchaService;
//
//    @Test
//    public void get() {
//        CaptchaVO captchaVO = new CaptchaVO();
//        captchaVO.setCaptchaType("blockPuzzle");
//        CaptchaVO captchaVO1 = captchaService.get(captchaVO);
//        String token = captchaVO1.getToken();
//        System.out.println("token:" + token);
//    }
//
//    @Test
//    public void testRandom() {
//        int t = 10000;
//        int wordCount = 4;
//        List<Set<String>> ret = new ArrayList<>();
//        for (int i = 0; i < t; i++) {
//            Set<String> s = getRandomWords(wordCount);
//            ret.add(s);
//        }
//        List<Set<String>> ret1 = new ArrayList<>();
//        for (int i = 0; i < t; i++) {
//            Set<String> s = getRandomWords1(wordCount);
//            //assert s.size()==wordCount;
//            ret1.add(s);
//        }
//        System.out.println(ret.stream().filter(i -> i.size() == wordCount).count());
//        System.out.println(ret1.stream().filter(i -> i.size() == wordCount).count());
//        assert ret1.stream().filter(i -> i.size() == wordCount).count() == t;
//    }
//
//    private Set<String> getRandomWords(int wordCount) {
//        Set<String> currentWords = new HashSet<>();
//        for (int i = 0; i < wordCount; i++) {
//            String word;
//            do {
//                word = RandomUtils.getRandomHan(ClickWordCaptchaServiceImpl.HAN_ZI);
//                currentWords.add(word);
//            } while (!currentWords.contains(word));
//        }
//        return currentWords;
//    }
//
//    private Set<String> getRandomWords1(int wordCount) {
//        Set<String> words = new HashSet<>();
//        int size = ClickWordCaptchaServiceImpl.HAN_ZI.length();
//        do {
//            String t = ClickWordCaptchaServiceImpl.HAN_ZI.charAt(RandomUtils.getRandomInt(size)) + "";
//            words.add(t);
//        } while (words.size() < wordCount);
//        return words;
//    }
//}
