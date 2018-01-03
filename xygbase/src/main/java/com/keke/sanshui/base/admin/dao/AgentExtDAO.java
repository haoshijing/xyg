package com.keke.sanshui.base.admin.dao;

import com.keke.sanshui.base.admin.po.agent.AgentExtPo;

/**
 * @author haoshijing
 * @version 2018年01月03日 16:51
 **/
public interface AgentExtDAO {

    AgentExtPo selectByAgentId(Integer agentId,Integer week);

    int insertAgentExtPo(AgentExtPo agentExtPo);

}
