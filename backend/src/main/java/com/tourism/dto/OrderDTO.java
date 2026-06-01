package com.tourism.dto;

import com.tourism.entity.TourOrder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDTO {

    private Long id;
    private String orderNo;
    private Long userId;
    private Long routeId;
    private String routeName;
    private Integer peopleCount;
    private String contactName;
    private String contactPhone;
    private BigDecimal totalAmount;
    private String status;
    private String remark;
    private LocalDateTime createdAt;

    public static OrderDTO fromEntity(TourOrder order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setUserId(order.getUserId());
        dto.setRouteId(order.getRouteId());
        dto.setPeopleCount(order.getPeopleCount());
        dto.setContactName(order.getContactName());
        dto.setContactPhone(order.getContactPhone());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setRemark(order.getRemark());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }
}
