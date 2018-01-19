package com.keke.sanshui.base.admin.dao;


import com.keke.sanshui.base.admin.po.agent.AgentReward;

import java.util.List;

public interface AgentRewardDAO {

    int insert(AgentReward agentReward);

    List<AgentReward> selectList();

    Long seletCount();
}
