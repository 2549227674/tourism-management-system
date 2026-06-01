package com.tourism.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentCreateRequest {

    @NotBlank(message = "评论类型不能为空")
    private String targetType;

    @NotNull(message = "评论目标 ID 不能为空")
    private Long targetId;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最小为 1")
    @Max(value = 5, message = "评分最大为 5")
    private Integer rating;

    @NotBlank(message = "评论内容不能为空")
    private String content;
}
