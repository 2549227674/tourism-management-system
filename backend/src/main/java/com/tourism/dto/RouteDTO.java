package com.tourism.dto;

import com.tourism.entity.TourRoute;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RouteDTO {

    private Long id;
    private String name;
    private String itinerary;
    private BigDecimal price;
    private LocalDateTime departureTime;
    private Integer quota;
    private Integer bookedCount;
    private String status;
    private String coverImageUrl;
    private LocalDateTime createdAt;

    public static RouteDTO fromEntity(TourRoute route) {
        RouteDTO dto = new RouteDTO();
        dto.setId(route.getId());
        dto.setName(route.getName());
        dto.setItinerary(route.getItinerary());
        dto.setPrice(route.getPrice());
        dto.setDepartureTime(route.getDepartureTime());
        dto.setQuota(route.getQuota());
        dto.setBookedCount(route.getBookedCount());
        dto.setStatus(route.getStatus());
        dto.setCoverImageUrl(route.getCoverImageUrl());
        dto.setCreatedAt(route.getCreatedAt());
        return dto;
    }
}
