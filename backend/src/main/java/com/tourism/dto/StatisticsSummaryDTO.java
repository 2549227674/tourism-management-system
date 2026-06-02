package com.tourism.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatisticsSummaryDTO {

    private Long spotCount;
    private Long routeCount;
    private Long userCount;
    private Long orderCount;
    private Long pendingOrderCount;
    private Long confirmedOrderCount;
    private Long completedOrderCount;
    private BigDecimal totalOrderAmount;
}
