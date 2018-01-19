package com.keke.sanshui.admin.service;

import com.keke.sanshui.admin.vo.cash.SubmitCashResponse;
import com.keke.sanshui.base.admin.dao.CashDAO;
import com.keke.sanshui.base.admin.dao.PlayerCouponDAO;
import com.keke.sanshui.base.admin.po.PlayerCouponPo;
import com.keke.sanshui.base.admin.po.agent.AgentPo;
import com.keke.sanshui.base.admin.po.agent.CashPo;
import com.keke.sanshui.base.admin.po.agent.CashQueryPo;
import com.keke.sanshui.base.admin.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CashService {

    @Autowired
    CashDAO cashDAO;

    @Autowired
    PlayerCouponDAO playerCouponDAO;

    @Autowired
    AgentService agentService;

    @Autowired
    GateWayService gateWayService;

    public SubmitCashResponse submitCash(Integer gold, Integer guid) {
        SubmitCashResponse response = new SubmitCashResponse();
        AgentPo agentPo = agentService.findByGuid(guid);
        if(agentPo == null){
            response.setMessage("无效数据");
            return response;
        }

        PlayerCouponPo playerCouponPo  = playerCouponDAO.selectByPlayerId(guid);
        if(playerCouponPo == null  || playerCouponPo.getGoldCount() < gold){
            response.setMessage("金币数不足");
            return response;
        }
        CashQueryPo cashQueryPo = new CashQueryPo();
        cashQueryPo.setStatus(1);
        cashQueryPo.setAgentId(agentPo.getId());
        Long count = cashDAO.selectCount(cashQueryPo);
        if(count != null && count > 0){
            response.setMessage("你有一个在未处理的提款,请等候处理后在申请");
            return response;
        }
        CashPo cashPo = new CashPo();
        cashPo.setStatus(1);
        cashPo.setAgentId(agentPo.getId());
        cashPo.setGoldCount(gold);
        cashPo.setInsertTime(System.currentTimeMillis());
        cashPo.setLastUpdateTime(System.currentTimeMillis());
        cashDAO.insertCash(cashPo);
        if(cashPo.getId() > 0) {
            response.setMessage("提现成功");
            response.setSucc(true);
        }else{
            response.setMessage("提现失败,服务器错误,请联系管理员查看");
        }
        return response;
    }

    public Boolean dealCash(Integer id, Integer status) {
        if(status == 2){
            CashPo cashPo = cashDAO.findById(id);
            if(cashPo != null){
                gateWayService.sendToGameServer(cashPo.getPlayerId(),cashPo.getGoldCount().toString());
            }
        }
        CashPo cashPo = new CashPo();
        cashPo.setStatus(status);
        cashPo.setId(id);
        cashPo.setLastUpdateTime(System.currentTimeMillis());
        cashDAO.updatePo(cashPo);
        return true;
    }
}
