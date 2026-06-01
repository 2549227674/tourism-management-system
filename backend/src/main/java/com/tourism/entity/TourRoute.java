package com.tourism.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tour_route")
public class TourRoute {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String itinerary;

    private BigDecimal price;

    private LocalDateTime departureTime;

    private Integer quota;

    private Integer bookedCount;

    private String status;

    private String coverImageUrl;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
