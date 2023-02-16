
package com.geega.bsc.captcha.starter.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES加解密工具类
 *
 * @author Jun.An3
 * @date 2021/11/22
 */
public class AESUtil {

    /**
     * 算法
     */
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

    /**
     * 获取随机key
     */
    public static String getKey() {
        return RandomUtils.getRandomString(16);
    }

    /**
     * 将byte[]转为各种进制的字符串
     *
     * @param bytes byte[]
     * @param radix 可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
     * @return 转换后的字符串
     */
    @SuppressWarnings("unused")
    public static String binary(byte[] bytes, int radix) {
        /*
         * 这里的1代表正数
         */
        return new BigInteger(1, bytes).toString(radix);
    }

    /**
     * base 64 encode
     *
     * @param bytes 待编码的byte[]
     * @return 编码后的base 64 code
     */
    public static String base64Encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * base 64 decode
     *
     * @param base64Code 待解码的base 64 code
     * @return 解码后的byte[]
     */
    public static byte[] base64Decode(String base64Code) {
        Base64.Decoder decoder = Base64.getDecoder();
        return StringUtils.isEmpty(base64Code) ? null : decoder.decode(base64Code);
    }

    /**
     * AES加密
     *
     * @param content    待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的byte[]
     */
    public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
        KeyGenerator ken = KeyGenerator.getInstance("AES");
        ken.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));

        return cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * AES加密为base 64 code
     *
     * @param content    待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的base 64 code
     */
    public static String aesEncrypt(String content, String encryptKey) throws Exception {
        if (StringUtils.isBlank(encryptKey)) {
            return content;
        }
        return base64Encode(aesEncryptToBytes(content, encryptKey));
    }

    /**
     * AES解密
     *
     * @param encryptBytes 待解密的byte[]
     * @param decryptKey   解密密钥
     * @return 解密后的String
     */
    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);

        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        return new String(decryptBytes);
    }

    /**
     * 将base 64 code AES解密
     *
     * @param encryptStr 待解密的base 64 code
     * @param decryptKey 解密密钥
     * @return 解密后的string
     */
    public static String aesDecrypt(String encryptStr, String decryptKey) throws Exception {
        if (StringUtils.isBlank(decryptKey)) {
            return encryptStr;
        }
        return StringUtils.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey);
    }

    /**
     * 测试
     */
    public static void main(String[] args) throws Exception {
        String randomString = RandomUtils.getRandomString(16);
        randomString = "jm43XlSsVdZcqHdo";
        String content = "{\"x\":141,\"y\":5,\"secretKey\":\"jm43XlSsVdZcqHdo\"}";
        System.out.println("加密前：" + content);
        System.out.println("加密密钥和解密密钥：" + randomString);
        String encrypt = aesEncrypt(content, randomString);
        System.out.println("加密后：" + encrypt);
        String decrypt = aesDecrypt(encrypt, randomString);
        System.out.println("解密后：" + decrypt);
    }

}
