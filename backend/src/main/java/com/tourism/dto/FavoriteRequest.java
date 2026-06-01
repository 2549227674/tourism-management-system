package com.tourism.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavoriteRequest {

    @NotBlank(message = "收藏类型不能为空")
    private String targetType;

    @NotNull(message = "收藏目标 ID 不能为空")
    private Long targetId;
}
