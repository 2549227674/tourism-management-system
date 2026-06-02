package com.tourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tourism.dto.StatisticsSummaryDTO;
import com.tourism.entity.SysUser;
import com.tourism.entity.TourOrder;
import com.tourism.mapper.ScenicSpotMapper;
import com.tourism.mapper.SysUserMapper;
import com.tourism.mapper.TourOrderMapper;
import com.tourism.mapper.TourRouteMapper;
import com.tourism.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private static final Set<String> REVENUE_STATUSES = Set.of("CONFIRMED", "COMPLETED");

    private final ScenicSpotMapper spotMapper;
    private final TourRouteMapper routeMapper;
    private final SysUserMapper userMapper;
    private final TourOrderMapper orderMapper;

    @Override
    public StatisticsSummaryDTO getSummary() {
        StatisticsSummaryDTO dto = new StatisticsSummaryDTO();

        dto.setSpotCount(spotMapper.selectCount(null));
        dto.setRouteCount(routeMapper.selectCount(null));

        dto.setUserCount(userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getRole, "USER")));

        dto.setOrderCount(orderMapper.selectCount(null));

        dto.setPendingOrderCount(orderMapper.selectCount(
                new LambdaQueryWrapper<TourOrder>().eq(TourOrder::getStatus, "PENDING")));

        dto.setConfirmedOrderCount(orderMapper.selectCount(
                new LambdaQueryWrapper<TourOrder>().eq(TourOrder::getStatus, "CONFIRMED")));

        dto.setCompletedOrderCount(orderMapper.selectCount(
                new LambdaQueryWrapper<TourOrder>().eq(TourOrder::getStatus, "COMPLETED")));

        BigDecimal totalAmount = orderMapper.selectList(
                        new LambdaQueryWrapper<TourOrder>().in(TourOrder::getStatus, REVENUE_STATUSES))
                .stream()
                .map(order -> order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        dto.setTotalOrderAmount(totalAmount);

        return dto;
    }
}
