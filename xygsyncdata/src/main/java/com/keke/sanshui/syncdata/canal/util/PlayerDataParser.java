package com.keke.sanshui.syncdata.canal.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.keke.sanshui.base.admin.po.PlayerCouponPo;
import com.keke.sanshui.base.admin.po.PlayerPo;
import com.keke.sanshui.base.admin.po.PlayerRelationPo;
import com.keke.sanshui.base.admin.po.agent.AgentPo;
import com.keke.sanshui.base.admin.service.AgentService;
import com.keke.sanshui.syncdata.canal.protocol.Character;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
public class PlayerDataParser {
    @Autowired
    private HttpClient httpClient;

    @Autowired
    private AgentService agentService;

    @Data
    public static class PlayerInfo {
        private PlayerPo playerPo;
        private PlayerCouponPo playerCouponPo;
    }

    @Data
    public static class PlayerAndAgentData {
        private List<PlayerRelationPo> playerRelationPos;
        private List<AgentPo> agentPos;
    }

    public PlayerInfo parseFromBaseData(Map<String, Object> data) {
        Integer playerId = (Integer) data.get("guid");
        byte[] sourceData = (byte[]) data.get("base_data");
        PlayerInfo playerInfo = null;
        try {
            Character.BaseData baseData = Character.BaseData.parseFrom(sourceData);
            playerInfo = getPlayerInfo(playerId.intValue(), baseData);
            Timestamp createTime = (Timestamp) data.get("create_time");
            String otherName = (String) data.get("other_name");
            otherName = removeNonBmpUnicode(otherName);
            PlayerPo playerPo = playerInfo.getPlayerPo();
            playerPo.setOtherName(otherName);
            playerPo.setGameInsertTime(createTime.getTime());
        }catch (Exception e){
            log.error("{}",e);
        }
        return playerInfo;
    }

    public PlayerAndAgentData parseFromWorldData(byte[] sourceData) {
        byte[] deEncryptByteData = deEncrypt(sourceData);
        PlayerAndAgentData data = getPlayRelations(deEncryptByteData);
        return data;
    }

    private PlayerAndAgentData getPlayRelations(byte[] deEncryptByteData) {
        List<PlayerRelationPo> relationPos = Lists.newArrayList();
        List<AgentPo> agentPos = Lists.newArrayList();
        Map<Integer, Set<PlayerRelationPo>> relationMaps = Maps.newHashMap();
        ByteBuf byteBuf = Unpooled.buffer(deEncryptByteData.length);
        byteBuf.writeBytes(deEncryptByteData);
        byte curVersion = byteBuf.readByte();
        int count = byteBuf.readIntLE();
        PlayerAndAgentData playerAndAgentData = new PlayerAndAgentData();
        try {
            while (count-- > 0) {
                byte curPlayerVersion = byteBuf.readByte();
                Long guid = byteBuf.readLongLE();
                String name = readString(byteBuf);
                String otherName = readString(byteBuf);
                String headId = readString(byteBuf);
                byte sex = byteBuf.readByte();
                Long costMoney = byteBuf.readLongLE();
                Long invitedGuid = byteBuf.readLongLE();
                int orderCount = byteBuf.readIntLE();
                while (orderCount-- > 0) {
                    String orderId = readString(byteBuf);
                    //log.info("orderId = {}",orderId);
                }
                int childrenCount = byteBuf.readIntLE();
                while (childrenCount-- > 0) {
                    Long childrenId = byteBuf.readLongLE();
                    //log.info("childrenId = {}",childrenId);
                }
                boolean isAgent = byteBuf.readBoolean();
                int level = 0;
                if (curPlayerVersion >= 5) {
                    level = byteBuf.readIntLE();

                }
                PlayerRelationPo playerRelationPo = new PlayerRelationPo();
                playerRelationPo.setParentPlayerId(invitedGuid.intValue());
                playerRelationPo.setLastUpdateTime(System.currentTimeMillis());
                playerRelationPo.setPlayerId(guid.intValue());
                Set<PlayerRelationPo> playerRelationPos = relationMaps.get(guid.intValue());
                if (playerRelationPos == null) {
                    playerRelationPos = Sets.newHashSet();
                    relationMaps.put(guid.intValue(), playerRelationPos);
                }
                playerRelationPos.add(playerRelationPo);

                if (isAgent) {
                    AgentPo agentPo = new AgentPo();
                    agentPo.setInsertTime(System.currentTimeMillis());
                    agentPo.setLastUpdateTime(System.currentTimeMillis());
                    agentPo.setStatus(2);
                    agentPo.setLevel(3);
                    agentPo.setLevel(level);
                    agentPo.setAgentWeChartNo(name);
                    agentPo.setAgentNickName(otherName);
                    agentPo.setMemo("");
                    agentPo.setParentId(0);
                    agentPo.setPlayerId(guid.intValue());
                    agentPos.add(agentPo);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        relationMaps.forEach((inviteId, relationPoSet) -> {
            relationPos.addAll(relationPoSet);
        });
        playerAndAgentData.setAgentPos(agentPos);
        playerAndAgentData.setPlayerRelationPos(relationPos);
        return playerAndAgentData;
    }

    private PlayerInfo getPlayerInfo(Integer playerId, Character.BaseData baseData) {

        int curVersion = baseData.getCurVersion();
        String name = baseData.getName();

        Long goldCount = baseData.getGold();
        Long diamond = baseData.getDiamond();
        Long cardCount = baseData.getCard();
        PlayerCouponPo playerCouponPo = new PlayerCouponPo();
        playerCouponPo.setDiamondCount(diamond.intValue());
        playerCouponPo.setGoldCount(goldCount.intValue());
        playerCouponPo.setCardCount(cardCount.intValue());
        playerCouponPo.setPlayerId(playerId);
        playerCouponPo.setLastUpdateTime(System.currentTimeMillis());

        PlayerPo playerPo = new PlayerPo();
        playerPo.setPlayerId(playerId);
        playerPo.setStatus(1);
        playerPo.setName(name);
        playerPo.setInsertTime(System.currentTimeMillis());
        playerPo.setLastUpdateTime(System.currentTimeMillis());

        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setPlayerCouponPo(playerCouponPo);
        playerInfo.setPlayerPo(playerPo);
        return playerInfo;
    }

    private String readString(ByteBuf byteBuf) {
        int startIdx = byteBuf.readerIndex(), endIdx = startIdx;
        while (byteBuf.readByte() != 0) {
        }
        endIdx = byteBuf.readerIndex();

        String str = new String(byteBuf.array(), startIdx, endIdx - 1 - startIdx);
        return str;
    }

    private byte[] deEncrypt(byte[] sourceData) {
        byte[] responseData = sourceData;
        Request request = httpClient.POST("http://121.196.195.248:97/DataDecompressWeb/ClientQuery.aspx?method=DeCompressData");
        request.timeout(5000, TimeUnit.MILLISECONDS).content(new BytesContentProvider(sourceData));
        try {
            ContentResponse response = request.send();
            responseData = response.getContent();

        } catch (Exception e) {

        }
        return responseData;
    }

    public static String removeNonBmpUnicode(String str) {
        if (str == null) {
            return null;
        }
        str = str.replaceAll("[^\\u0000-\\uFFFF]", "");
        return str;
    }
}
