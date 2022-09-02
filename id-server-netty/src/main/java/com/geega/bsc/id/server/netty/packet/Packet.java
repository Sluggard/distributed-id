package com.geega.bsc.id.server.netty.packet;

import lombok.Data;

/**
 * body长度(4byte) + crc32(4byte) + body数据(不定长度)
 *
 * @author Jun.An3
 * @date 2022/09/02
 */
@Data
public class Packet {

    private int num;

}