package com.keke.sanshui.admin.vo;

import com.keke.sanshui.base.admin.po.agent.CashPo;
import lombok.Data;

@Data
public class AgentVo {
    private Integer agentId;
    private Integer gameId;
    private String name;
    private String weChartNo;
    private String nickName;
    private Integer goldCount;
    private Integer diamondCount;
    private Integer cardCount;
    private Integer type;
    private String memo;
    private String week;
    private Integer parentAgentId;
    /**
     * 会员个数
     */
    private Integer memberCount;
    /**
     * 代理下级总充值
     */
    private Long agentUnderTotalPickUp;
    /**
     * 代理自身总充值
     */
    private Long agentTotalPickUp;

    /**
     * 当前周新增人数
     */
    private Integer addCount;

    /*
    本周是否领取过奖励
     */
    private String isAward;
    CashPo cashPo;

    private String alipayAccout;
    private String wechartAccout;
}
