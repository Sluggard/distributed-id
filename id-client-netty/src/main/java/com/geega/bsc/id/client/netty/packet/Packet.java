package com.geega.bsc.id.client.netty.packet;

import io.netty.buffer.ByteBuf;
import lombok.Builder;
import lombok.Data;

/**
 * body长度(4byte) + body数据(不定长度)
 *
 * @author Jun.An3
 * @date 2022/09/02
 */
@Builder
@Data
public class Packet {

    private byte[] body;

    public static Packet read(ByteBuf byteBuf) {
        byte[] bodyBytes = new byte[4];
        byteBuf.readBytes(bodyBytes);
        //再次读取num数据
        return Packet.builder().body(bodyBytes).build();
    }

    public void write(ByteBuf out) {
        assert body != null && body.length > 0;
        out.writeBytes(body);
    }

}