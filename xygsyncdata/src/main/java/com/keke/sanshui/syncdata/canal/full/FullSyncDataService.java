package com.keke.sanshui.syncdata.canal.full;

import com.alibaba.druid.pool.DruidDataSource;
import com.keke.sanshui.base.admin.dao.AgentDAO;
import com.keke.sanshui.base.admin.dao.AgentRewardDAO;
import com.keke.sanshui.base.admin.dao.PlayerRelationDAO;
import com.keke.sanshui.base.admin.po.PlayerCouponPo;
import com.keke.sanshui.base.admin.po.PlayerRelationPo;
import com.keke.sanshui.base.admin.po.agent.AgentPo;
import com.keke.sanshui.base.admin.po.agent.AgentReward;
import com.keke.sanshui.base.admin.service.PlayerService;
import com.keke.sanshui.syncdata.canal.util.PlayerDataParser;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
public class FullSyncDataService {

    JdbcTemplate jdbcTemplate = new JdbcTemplate();

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PlayerRelationDAO playerRelationDAO;

    @Autowired
    private PlayerDataParser parser;

    @Autowired
    private AgentDAO agentDAO;

    @Autowired
    AgentRewardDAO agentRewardDAO;

    private ScheduledExecutorService scheduledExecutorService;

    private ScheduledExecutorService executorService;

    @Value("${sync.db.ip}")
    private String syncDbIp;

    @Value("${sync.db.name}")
    private String syncDbName;

    @Value("${sync.db.password}")
    private String syncDbPassword;

    private static final String PLAYER_ID = "guid";

    public FullSyncDataService() {

    }

    @PostConstruct
    public void init() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUsername(syncDbName);
        druidDataSource.setPassword(syncDbPassword);
        druidDataSource.setUrl("jdbc:mysql://" + syncDbIp + ":3306/xianyugouqipai?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull");
        jdbcTemplate.setDataSource(druidDataSource);
    }

    @EventListener
    public void startWork(EmbeddedServletContainerInitializedEvent event) {
        scheduledExecutorService = Executors.newScheduledThreadPool(1, new DefaultThreadFactory("SyncDataThread"));
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    syncCharacterData();
                    syncRelation();
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }, 10, 30, TimeUnit.SECONDS);

        executorService  = Executors.newScheduledThreadPool(1, new DefaultThreadFactory("SyncDataThread1"));
        executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    syncReward();
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }, 2, 3600*24, TimeUnit.SECONDS);

    }

    private void syncReward() {
        log.info("");
        String sql = " select guid,reward,finishTime from daili_reward_record ";
        List<Map<String, Object>> datas = jdbcTemplate.queryForList(sql);
        agentRewardDAO.deleteData();
        datas.forEach(data -> {
            AgentReward reward = new AgentReward();
            Long guid = (Long)data.get("guid");
            Integer rewardData = (Integer)data.get("reward");
            Timestamp timestamp = (Timestamp)data.get("finishTime");

            reward.setCreateTime(timestamp.getTime());
            reward.setGuid(guid.intValue());
            reward.setReward(rewardData);

            agentRewardDAO.insert(reward);
        });
    }


    public void syncRelation() {
        log.info("关系开始进行同步");
        String sql = " select data from world_records where type =1 ";
        List<Map<String, Object>> datas = jdbcTemplate.queryForList(sql);
        datas.forEach(data -> {
            byte[] bytes = (byte[]) data.get("data");
            PlayerDataParser.PlayerAndAgentData playerAndAgentData = parser.parseFromWorldData(bytes);
            playerAndAgentData.getPlayerRelationPos().forEach(playerRelationPo -> {
                Integer parentId = playerRelationPo.getParentPlayerId().intValue();
                Integer playerId = playerRelationPo.getPlayerId().intValue();
                PlayerRelationPo queryPlayRelation = playerRelationDAO.selectByPlayerId(playerId);
                if (queryPlayRelation != null) {
                    PlayerRelationPo updatePlayerRelationPo = new PlayerRelationPo();
                    updatePlayerRelationPo.setPlayerId(playerId);
                    updatePlayerRelationPo.setId(queryPlayRelation.getId());
                    updatePlayerRelationPo.setParentPlayerId(parentId);
                    playerRelationDAO.updatePlayerRelation(updatePlayerRelationPo);
                } else {
                    playerRelationDAO.insertRelation(playerRelationPo);
                }
            });
            playerAndAgentData.getAgentPos().forEach(agentPo -> {
                //去找这个人的上级
                Integer playerId = agentPo.getPlayerId();
                AgentPo queryPo = agentDAO.selectByPlayerId(playerId);
                if (queryPo == null) {
                    try {
                        agentDAO.insert(agentPo);
                    } catch (Exception e) {
                        log.error("{}", e);
                    }
                }else{
                    AgentPo updatePo = new AgentPo();
                    updatePo.setId(queryPo.getId());
                    updatePo.setLevel(agentPo.getLevel());
                    agentDAO.updateAgent(agentPo);
                }
            });
        });
    }

    public void syncCharacterData() {
        String sql = " select * from characters";
        List<Map<String, Object>> datas = jdbcTemplate.queryForList(sql);
        log.info("会员信息进行数据同步");
        datas.forEach(data -> {
            BigInteger playerId = (BigInteger) data.get(PLAYER_ID);
            data.put(PLAYER_ID, playerId.intValue());
            try {
                PlayerDataParser.PlayerInfo playerInfo = parser.parseFromBaseData(data);
                boolean exist = playerService.checkPlayerExsist(playerId.intValue());
                if (playerInfo != null) {
                    if (!exist) {
                        try {
                            playerService.insertPlayer(playerInfo.getPlayerPo());
                            playerService.insertPlayerCoupon(playerInfo.getPlayerCouponPo());
                        } catch (Exception e) {
                            log.error("", e);
                        }
                    } else {
                        PlayerCouponPo updatePlayerCouponPo = new PlayerCouponPo();
                        updatePlayerCouponPo.setPlayerId(playerInfo.getPlayerCouponPo().getPlayerId());
                        updatePlayerCouponPo.setDiamondCount(playerInfo.getPlayerCouponPo().getDiamondCount());
                        updatePlayerCouponPo.setGoldCount(playerInfo.getPlayerCouponPo().getGoldCount());
                        playerService.updatePlayerCoupon(updatePlayerCouponPo);
                    }
                }
            } catch (Exception e) {
                log.error("", e);
                e.printStackTrace();
            }
        });
    }
}
