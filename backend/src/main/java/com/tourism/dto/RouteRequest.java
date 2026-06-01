package com.tourism.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RouteRequest {

    @NotBlank(message = "线路名称不能为空")
    private String name;

    @NotBlank(message = "行程安排不能为空")
    private String itinerary;

    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    @NotNull(message = "出发时间不能为空")
    private LocalDateTime departureTime;

    @NotNull(message = "名额不能为空")
    private Integer quota;

    private Integer bookedCount;

    private String status;

    private String coverImageUrl;
}
