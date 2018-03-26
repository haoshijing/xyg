package com.keke.sanshui.admin.request.order;


import lombok.Data;

@Data
public class OrderTotalQueryVo {
    /**
     * 开始时间
     */
    private Long start;
    /**
     * 结束时间
     */
    private Long end;
    /**
     * 支付类型
     */
    private Integer type;
}
