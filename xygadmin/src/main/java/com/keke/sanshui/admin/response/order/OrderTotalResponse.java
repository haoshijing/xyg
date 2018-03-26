package com.keke.sanshui.admin.response.order;

import lombok.Data;

import java.util.List;

@Data
public class OrderTotalResponse {

    private List<OrderTotalItem> orderTotalItems;

    @Data
    public static class OrderTotalItem{
        private Integer type;
        private Long total;
    }
}
