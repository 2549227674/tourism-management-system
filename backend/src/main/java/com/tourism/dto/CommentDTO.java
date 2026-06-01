package com.tourism.dto;

import com.tourism.entity.TravelComment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {

    private Long id;
    private Long userId;
    private String username;
    private String targetType;
    private Long targetId;
    private String targetName;
    private Integer rating;
    private String content;
    private String status;
    private String auditRemark;
    private LocalDateTime createdAt;

    public static CommentDTO fromEntity(TravelComment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setUserId(comment.getUserId());
        dto.setTargetType(comment.getTargetType());
        dto.setTargetId(comment.getTargetId());
        dto.setRating(comment.getRating());
        dto.setContent(comment.getContent());
        dto.setStatus(comment.getStatus());
        dto.setAuditRemark(comment.getAuditRemark());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}
