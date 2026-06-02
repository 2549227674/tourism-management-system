package com.tourism.controller;

import com.tourism.common.Result;
import com.tourism.dto.StatisticsSummaryDTO;
import com.tourism.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/statistics")
@RequiredArgsConstructor
public class AdminStatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/summary")
    public Result<StatisticsSummaryDTO> summary() {
        return Result.success(statisticsService.getSummary());
    }
}
