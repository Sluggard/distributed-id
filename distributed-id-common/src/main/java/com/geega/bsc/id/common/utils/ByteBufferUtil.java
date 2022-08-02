package com.geega.bsc.id.common.utils;


import com.alibaba.fastjson.JSON;
import com.geega.bsc.id.common.network.ByteBufferSend;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Jun.An3
 * @date 2022/07/20
 */
public class ByteBufferUtil {

    private static final CharsetDecoder DECODER = StandardCharsets.UTF_8.newDecoder();

    private final static int NEED_NUM_LENGTH = 4;

    public static ByteBuffer getSend(int needNum) {
        ByteBuffer allocate = ByteBuffer.allocate(NEED_NUM_LENGTH << 1);
        allocate.mark();
        allocate.put(intToByte(NEED_NUM_LENGTH));
        allocate.put(intToByte(needNum));
        allocate.reset();
        return allocate;
    }

    public static int byteToInt(ByteBuffer data) {
        byte[] bytes = new byte[8];
        data.get(bytes);
        return (bytes[4] & 0xff) << 24 | (bytes[5] & 0xff) << 16 | (bytes[6] & 0xff) << 8 | (bytes[7] & 0xff);
    }

    public static int byteToIntV2(ByteBuffer data) {
        byte[] bytes = new byte[4];
        data.get(bytes);
        return (bytes[0] & 0xff) << 24 | (bytes[1] & 0xff) << 16 | (bytes[2] & 0xff) << 8 | (bytes[3] & 0xff);
    }

    public static ByteBufferSend getSend(String id, int needNum) {
        return new ByteBufferSend(id, getSend(needNum));
    }

    public static ByteBufferSend getSendForServer(String destination, List<Long> ids) {
        return new ByteBufferSend(destination, getBufferForServer(ids));
    }

    private static byte[] intToByte(int n) {
        byte[] b = new byte[NEED_NUM_LENGTH];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }

    public static String byteBufferToString(ByteBuffer receives) {
        String result = null;
        try {
            if (receives != null && receives.limit() > 0) {
                System.out.println("数据大小：" + receives.remaining());
                CharBuffer charBuffer = DECODER.decode(receives);
                return charBuffer.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static ByteBuffer stringToByteBuffer(String msg) {
        ByteBuffer result = null;
        if (msg != null && msg.length() > 0) {
            byte[] data = msg.getBytes();
            result = ByteBuffer.allocate(NEED_NUM_LENGTH + data.length);
            result.mark();
            result.put(intToByte(data.length));
            result.put(data);
            result.reset();
        }
        return result;
    }

    private static ByteBuffer getBufferForServer(List<Long> ids) {
        String idsJsonString = JSON.toJSONString(ids);
        return stringToByteBuffer(idsJsonString);
    }

}
