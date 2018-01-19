package com.keke.sanshui.admin.controller;

import com.keke.sanshui.admin.AbstractController;
import com.keke.sanshui.admin.auth.AdminAuthInfo;
import com.keke.sanshui.admin.response.ApiResponse;
import com.keke.sanshui.admin.service.CashService;
import com.keke.sanshui.admin.vo.cash.SubmitCashResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/cash")
public class CashController extends AbstractController {

    @Autowired
    private CashService cashService;

    @RequestMapping("/submitCash")
    public ApiResponse<SubmitCashResponse> submitCash(HttpServletRequest request, Integer gold) {
        AdminAuthInfo adminAuthInfo = getToken(request);
        Integer guid = Integer.valueOf(adminAuthInfo.getUserName());
        SubmitCashResponse response = cashService.submitCash(gold, guid);
        return new ApiResponse<>(response);
    }

    @RequestMapping("/dealCash")
    public ApiResponse<Boolean> dealCash(Integer id, Integer status) {
        Boolean ret = cashService.dealCash(id, status);
        return  new ApiResponse<>(ret);
    }
}
