package com.geega.bsc.id.server.netty.packet;

import io.netty.buffer.ByteBuf;
import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * body长度(4byte) + body数据(不定长度)
 *
 * @author Jun.An3
 * @date 2022/09/02
 */
@Builder
@Data
public class Packet {

    private static final Logger logger = LoggerFactory.getLogger(Packet.class);

    private byte[] body;

    public static Packet parse(ByteBuf byteBuf) {
        final int length = byteBuf.readInt();
        byte[] bodyBytes = new byte[length];
        byteBuf.readBytes(bodyBytes);
        //再次读取num数据
        return Packet.builder().body(bodyBytes).build();
    }

    public void write(ByteBuf out) {
        out.writeBytes(body);
    }

    public int getNum() {
        assert body != null && body.length > 0;
        return (body[0] & 0xff) << 24 | (body[1] & 0xff) << 16 | (body[2] & 0xff) << 8 | (body[3] & 0xff);
    }

    public String getId() {
        assert body != null && body.length > 0;
        return new String(body);
    }

}