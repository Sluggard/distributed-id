
package com.geega.bsc.captch.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * 二次校验VO
 *
 * @author Jun.An3
 * @date 2021/11/25
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyVO implements Serializable {

    /**
     * 后台二次校验参数
     */
    private String verification;

    public String getVerification() {
        return verification;
    }

    public void setVerification(String verification) {
        this.verification = verification;
    }

}
