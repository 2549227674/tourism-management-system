package com.tourism.dto;

import com.tourism.entity.ScenicSpot;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpotDTO {

    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;
    private String address;
    private BigDecimal ticketPrice;
    private String openTime;
    private String introduction;
    private String imageUrl;
    private String status;

    public static SpotDTO fromEntity(ScenicSpot spot) {
        SpotDTO dto = new SpotDTO();
        dto.setId(spot.getId());
        dto.setName(spot.getName());
        dto.setCategoryId(spot.getCategoryId());
        dto.setAddress(spot.getAddress());
        dto.setTicketPrice(spot.getTicketPrice());
        dto.setOpenTime(spot.getOpenTime());
        dto.setIntroduction(spot.getIntroduction());
        dto.setImageUrl(spot.getImageUrl());
        dto.setStatus(spot.getStatus());
        return dto;
    }
}
