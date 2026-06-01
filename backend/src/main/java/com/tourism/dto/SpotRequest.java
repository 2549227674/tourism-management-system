package com.tourism.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpotRequest {

    @NotBlank(message = "景点名称不能为空")
    private String name;

    private Long categoryId;

    private String address;

    @NotNull(message = "票价不能为空")
    private BigDecimal ticketPrice;

    private String openTime;

    private String introduction;

    private String imageUrl;
}
