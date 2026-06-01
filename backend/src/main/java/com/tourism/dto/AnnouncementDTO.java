package com.tourism.dto;

import com.tourism.entity.Announcement;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementDTO {

    private Long id;
    private String title;
    private String content;
    private String status;
    private LocalDateTime publishTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AnnouncementDTO fromEntity(Announcement announcement) {
        AnnouncementDTO dto = new AnnouncementDTO();
        dto.setId(announcement.getId());
        dto.setTitle(announcement.getTitle());
        dto.setContent(announcement.getContent());
        dto.setStatus(announcement.getStatus());
        dto.setPublishTime(announcement.getPublishTime());
        dto.setCreatedAt(announcement.getCreatedAt());
        dto.setUpdatedAt(announcement.getUpdatedAt());
        return dto;
    }
}
