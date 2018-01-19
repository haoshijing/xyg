package com.keke.sanshui.base.admin.po.agent;

import lombok.Data;

@Data
public class CashPo {
    private Integer status;
    private Integer id;
    private Long insertTime;
    private Long lastUpdateTime;
    private Integer agentId;
    private Integer playerId;
    private Integer goldCount;
    private String message;
}
