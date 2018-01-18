package com.keke.sanshui.base.admin.dao;

import com.keke.sanshui.base.admin.po.agent.AgentExtPo;
import org.apache.ibatis.annotations.Param;

/**
 * @author haoshijing
 * @version 2018年01月03日 16:51
 **/
public interface AgentExtDAO {

    AgentExtPo selectByAgentId(@Param("agentId") Integer agentId,@Param("week") Integer week);

    int insertAgentExtPo(@Param("agentExt") AgentExtPo agentExtPo);

    int updateAgentExtPo(@Param("param")AgentExtPo updateExtPo);

    AgentExtPo selectByPlayerId(@Param("playerId")Integer playerId,@Param("week") Integer week);

}
