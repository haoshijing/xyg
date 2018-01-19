package com.keke.sanshui.base.admin.dao;


import com.keke.sanshui.base.admin.po.QueryAgentReward;
import com.keke.sanshui.base.admin.po.agent.AgentReward;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AgentRewardDAO {

    int insert(AgentReward agentReward);

    List<AgentReward> selectList(@Param("param") QueryAgentReward reward);

    Long selectCount();

    void deleteData();
}
