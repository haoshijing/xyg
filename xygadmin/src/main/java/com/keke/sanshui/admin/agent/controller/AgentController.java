package com.keke.sanshui.admin.agent.controller;

import com.google.common.collect.Lists;
import com.keke.sanshui.admin.AbstractController;
import com.keke.sanshui.admin.agent.response.UnderAgentResponseVo;
import com.keke.sanshui.admin.request.player.PlayerQueryVo;
import com.keke.sanshui.admin.response.ApiResponse;
import com.keke.sanshui.admin.response.RetCode;
import com.keke.sanshui.admin.service.AdminAgentReadService;
import com.keke.sanshui.admin.vo.AgentMyInfo;
import com.keke.sanshui.admin.vo.AgentOrderVo;
import com.keke.sanshui.admin.vo.AgentRewardVo;
import com.keke.sanshui.base.admin.po.agent.AgentReward;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/agentbiz/")
public class AgentController extends AbstractController {
    @Autowired
    private AdminAgentReadService adminAgentReadService;

    @RequestMapping("/queryUnderAgentList")
    public ApiResponse<UnderAgentResponseVo> queryUnderAgentList(HttpServletRequest request, @RequestBody PlayerQueryVo playerQueryVo) {
        try {
            Integer areaAgentGuid = Integer.parseInt(getToken(request).getUserName());
            UnderAgentResponseVo underAgentResponseVo = adminAgentReadService.queryUnderAgentList(areaAgentGuid, playerQueryVo);
            return new ApiResponse<>(underAgentResponseVo);
        } catch (Exception e) {
            log.error("", e);
            return new ApiResponse<>(RetCode.SERVER_ERROR, e.getMessage(), null);
        }
    }

    @RequestMapping("/queryUnderAgentCount")
    public ApiResponse<Long> queryUnderAgentCount(HttpServletRequest request, @RequestBody PlayerQueryVo playerQueryVo) {
        try {
            Integer areaAgentGuid = Integer.parseInt(getToken(request).getUserName());
            Long count = adminAgentReadService.queryUnderAgentCount(areaAgentGuid, playerQueryVo);
            return new ApiResponse<>(count);
        } catch (Exception e) {
            log.error("", e);
            return new ApiResponse<>(RetCode.SERVER_ERROR, e.getMessage(), 0L);
        }
    }

    @RequestMapping("/queryMy")
    public ApiResponse<AgentMyInfo> queryMy(HttpServletRequest request) {
        try {
            Integer areaAgentGuid = Integer.parseInt(getToken(request).getUserName());
            AgentMyInfo agentMyInfo = adminAgentReadService.queryMyInfo(areaAgentGuid);
            return new ApiResponse<>(agentMyInfo);
        } catch (Exception e) {
            log.error("", e);
            return new ApiResponse<>(RetCode.SERVER_ERROR, e.getMessage(), new AgentMyInfo());
        }
    }

    @RequestMapping("/queryMyOrderList")
    public ApiResponse<List<AgentOrderVo>> queryMyOrderList(HttpServletRequest request, Long maxId) {
        try {
            Integer areaAgentGuid = Integer.parseInt(getToken(request).getUserName());
            List<AgentOrderVo> agentOrderVos = adminAgentReadService.queryMyOrderList(areaAgentGuid, maxId);
            return new ApiResponse<>(agentOrderVos);
        } catch (Exception e) {
            log.error("", e);
            return new ApiResponse<>(RetCode.SERVER_ERROR, e.getMessage(), Lists.newArrayList());
        }
    }

    @RequestMapping("/updateAgent")
    public ApiResponse<Boolean> updateAgent(HttpServletRequest request, String alipayAccout, String wecharAccount) {
        try {
            Integer areaAgentGuid = Integer.parseInt(getToken(request).getUserName());
            adminAgentReadService.updateAgent(areaAgentGuid, alipayAccout, wecharAccount);
        } catch (Exception e) {
            log.error("", e);
            return new ApiResponse<>(RetCode.SERVER_ERROR, e.getMessage(), false);
        }
        return new ApiResponse<>(true);
    }

    @RequestMapping("/queryRewardList")
    public ApiResponse<List<AgentRewardVo>> queryRewardList(HttpServletRequest request) {
        try {
            Integer areaAgentGuid = Integer.parseInt(getToken(request).getUserName());
            List<AgentReward> agentRewards = adminAgentReadService.queryRewardList(areaAgentGuid);
            List<AgentRewardVo> agentRewardVos = agentRewards.stream().map(agentReward -> {
                AgentRewardVo rewardVo = new AgentRewardVo();
                rewardVo.setReward(agentReward.getReward());
                rewardVo.setRewardTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(agentReward.getCreateTime()));
                return rewardVo;
            }).collect(Collectors.toList());
            return new ApiResponse<>(agentRewardVos);
        } catch (Exception e) {
            log.error("", e);
            return new ApiResponse<>(RetCode.SERVER_ERROR, e.getMessage(), Lists.newArrayList());
        }
    }
}
