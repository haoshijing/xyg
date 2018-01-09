package com.keke.sanshui.admin.response.player;

import lombok.Data;

@Data
public class PlayerResponseVo {
    private Integer guid;
    private String otherName;
    private Long pickTotal;
    private Integer goldCount;
    private Integer  diamondCount;
    private Integer agentGuidId;
    private Long insertTime;
    private String week;
}
