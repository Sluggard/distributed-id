package com.geega.bsc.captch.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * 二次校验结果
 *
 * @author Jun.An3
 * @date 2021/12/02
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyResultVO implements Serializable {

    private Boolean result;

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

}
