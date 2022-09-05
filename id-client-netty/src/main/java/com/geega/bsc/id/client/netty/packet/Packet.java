package com.geega.bsc.id.client.netty.packet;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import lombok.Builder;
import lombok.Data;
import java.util.List;

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
        final int length = byteBuf.readInt();
        byte[] bodyBytes = new byte[length];
        byteBuf.readBytes(bodyBytes);
        return Packet.builder().body(bodyBytes).build();
    }

    public List<Long> getIds() {
        assert body != null;
        return JSON.parseArray(new String(body), Long.class);
    }

    public void write(ByteBuf out) {
        assert body != null && body.length > 0;
        out.writeBytes(body);
    }

}