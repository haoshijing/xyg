package com.keke.sanshui.job.service;

import com.keke.sanshui.base.admin.dao.AgentExtDAO;
import com.keke.sanshui.base.admin.dao.PlayerRelationDAO;
import com.keke.sanshui.base.admin.po.QueryPlayerRelationPo;
import com.keke.sanshui.base.admin.po.agent.AgentExtPo;
import com.keke.sanshui.base.admin.po.agent.AgentPo;
import com.keke.sanshui.base.admin.service.AgentService;
import com.keke.sanshui.base.util.WeekUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author haoshijing
 * @version 2018年01月03日 18:13
 **/
@Service
@Slf4j
public class AgentCountService {

    @Autowired
    AgentService agentService;

    @Autowired
    PlayerRelationDAO playerRelationDAO;

    @Autowired
    AgentExtDAO agentExtDAO;

    public void work() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("开始进行下级玩家数统计:{}", format.format(new Date()));
        int week = WeekUtil.getCurrentWeek();
        List<AgentPo> agentPoList = agentService.selectAll();
        try {
            agentPoList.forEach(agentPo -> {
                staticsAgentAddCount(agentPo);
            });
        } catch (Exception e) {
            log.error("{}", e);
        }
        log.info("结束进行下级玩家数统计:{}", format.format(new Date()));
    }

    private void staticsAgentAddCount(AgentPo agentPo) {
        Integer playerId = agentPo.getPlayerId();
        QueryPlayerRelationPo queryPlayerRelationPo = new QueryPlayerRelationPo();
        queryPlayerRelationPo.setParentPlayerId(playerId);
        Long start = WeekUtil.getWeekStartTimestamp();
        queryPlayerRelationPo.setStart(start);
        Long end = WeekUtil.getWeekEndTimestamp();
        queryPlayerRelationPo.setEnd(end);
        Long count = playerRelationDAO.queryCount(queryPlayerRelationPo);
        if (count == null || count == 0) {
            return;
        }
        Integer week = WeekUtil.getCurrentWeek();
        AgentExtPo agentExtPo = agentExtDAO.selectByAgentId(agentPo.getId(), week);
        if (agentExtPo != null) {
            AgentExtPo updatePo = new AgentExtPo();
            updatePo.setAddCount(count.intValue());
            updatePo.setLastUpdateTime(System.currentTimeMillis());
            updatePo.setId(agentExtPo.getId());
        } else {
            AgentExtPo newPo = new AgentExtPo();
            newPo.setAddCount(count.intValue());
            newPo.setAgentId(agentPo.getId());
            newPo.setInsertTime(System.currentTimeMillis());
            newPo.setLastUpdateTime(System.currentTimeMillis());
            newPo.setPlayerId(playerId);
            newPo.setIsAward(0);
            agentExtDAO.insertAgentExtPo(newPo);
        }
    }
}
