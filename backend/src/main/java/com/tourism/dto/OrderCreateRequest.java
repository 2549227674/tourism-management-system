package com.tourism.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderCreateRequest {

    @NotNull(message = "线路 ID 不能为空")
    private Long routeId;

    @NotNull(message = "预订人数不能为空")
    private Integer peopleCount;

    @NotBlank(message = "联系人不能为空")
    private String contactName;

    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;
}
