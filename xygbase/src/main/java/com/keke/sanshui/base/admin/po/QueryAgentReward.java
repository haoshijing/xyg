package com.keke.sanshui.base.admin.po;

import com.keke.sanshui.base.admin.po.agent.AgentReward;
import lombok.Data;

@Data
public class QueryAgentReward extends AgentReward {
    private Integer offset;
    private Integer limit;
}
