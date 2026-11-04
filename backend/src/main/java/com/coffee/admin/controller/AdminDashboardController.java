package com.coffee.admin.controller;

import com.coffee.admin.service.AdminDashboardService;
import com.coffee.admin.vo.DashboardVO;
import com.coffee.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台数据看板接口
 */
@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping
    public Result<DashboardVO> getDashboard() {
        return Result.success(adminDashboardService.getDashboardData());
    }
}