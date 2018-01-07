package com.keke.sanshui.base.admin.po.agent;

import lombok.Data;

/**
 * @author haoshijing
 * @version 2018年01月03日 16:49
 **/
@Data
public class AgentExtPo {
    private Integer id;
    private Integer agentId;
    private Integer playerId;
    private Integer week;
    private Integer status;
    private Integer isAward;
    private Integer addCount;
    private Long lastUpdateTime;
    private Long insertTime;
}
