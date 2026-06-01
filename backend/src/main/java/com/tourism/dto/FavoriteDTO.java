package com.tourism.dto;

import com.tourism.entity.Favorite;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FavoriteDTO {

    private Long id;
    private Long userId;
    private String targetType;
    private Long targetId;
    private String targetName;
    private LocalDateTime createdAt;

    public static FavoriteDTO fromEntity(Favorite fav) {
        FavoriteDTO dto = new FavoriteDTO();
        dto.setId(fav.getId());
        dto.setUserId(fav.getUserId());
        dto.setTargetType(fav.getTargetType());
        dto.setTargetId(fav.getTargetId());
        dto.setCreatedAt(fav.getCreatedAt());
        return dto;
    }
}
