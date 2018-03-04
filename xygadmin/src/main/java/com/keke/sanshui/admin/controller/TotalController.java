package com.keke.sanshui.admin.controller;

import com.keke.sanshui.admin.response.ApiResponse;
import com.keke.sanshui.admin.response.index.PickDataResponse;
import com.keke.sanshui.admin.vo.PickTotalVo;
import com.keke.sanshui.base.admin.po.order.Order;
import com.keke.sanshui.base.admin.po.order.QueryOrderPo;
import com.keke.sanshui.base.admin.service.OrderService;
import com.keke.sanshui.base.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/total")
public class TotalController {


    @Autowired
    private OrderService orderService;

    @RequestMapping("/obtainPickTotal")
    public ApiResponse<PickTotalVo> obtainPickTotal(Long start, Long end){

        PickTotalVo pickTotalVo = new PickTotalVo();

        QueryOrderPo queryOrderPo = new QueryOrderPo();
        queryOrderPo.setOrderStatus(2);
        queryOrderPo.setStartTimestamp(start);
        queryOrderPo.setEndTimestamp(end);
        queryOrderPo.setLimit(10000);
        queryOrderPo.setOffset(0);
        List<Order> orderList = orderService.selectList(queryOrderPo);
        int sum = orderList.stream().mapToInt(order->{
            return Integer.valueOf(order.getPrice());
        }).sum();
        pickTotalVo.setPickCount(orderList.size());
        pickTotalVo.setPickMoney(Long.valueOf(sum/100));
        return new ApiResponse<>(new PickTotalVo());
    }
}
