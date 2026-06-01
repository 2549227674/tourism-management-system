package com.tourism.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentAuditRequest {

    @NotBlank(message = "审核状态不能为空")
    private String status;

    private String auditRemark;
}
