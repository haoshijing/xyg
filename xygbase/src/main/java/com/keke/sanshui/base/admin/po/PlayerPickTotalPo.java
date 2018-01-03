package com.keke.sanshui.base.admin.po;

import lombok.Data;

/**
 * 会员充值汇总表
 */
@Data
public class PlayerPickTotalPo {
    private Integer id;
    private Integer week;
    private Integer playerId;
    private Long totalMoney; /*单位分*/
    private Long lastUpdateTime;
}
