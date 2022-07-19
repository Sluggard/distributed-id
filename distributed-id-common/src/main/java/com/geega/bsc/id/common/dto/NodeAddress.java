package com.geega.bsc.id.common.dto;

import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Jun.An3
 * @date 2022/07/11
 */
@Builder
@Data
public class NodeAddress implements Serializable {

    private String ip;

    private Integer port;

    private Long lastUpdateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NodeAddress that = (NodeAddress) o;
        if (!Objects.equals(ip, that.getIp())) {
            return false;
        }
        if (!Objects.equals(port, that.getPort())) {
            return false;
        }
        return true;
    }

}
