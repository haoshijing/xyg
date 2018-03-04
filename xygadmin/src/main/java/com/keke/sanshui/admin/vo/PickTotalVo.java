package com.keke.sanshui.admin.vo;

import lombok.Data;

/**
 * 充值总数vo
 */
@Data
public class PickTotalVo {

    /**
     * 充值笔数
     */
    private Integer pickCount;

    /**
     * 充值总金额
     */
    private Long pickMoney;
}
