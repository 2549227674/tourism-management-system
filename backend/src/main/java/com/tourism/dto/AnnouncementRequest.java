package com.tourism.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AnnouncementRequest {

    @NotBlank(message = "公告标题不能为空")
    @Size(max = 100, message = "公告标题不能超过 100 字")
    private String title;

    @NotBlank(message = "公告内容不能为空")
    private String content;
}
