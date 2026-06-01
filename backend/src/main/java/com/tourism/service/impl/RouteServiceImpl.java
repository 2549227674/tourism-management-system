package com.tourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tourism.common.BusinessException;
import com.tourism.dto.RouteDTO;
import com.tourism.dto.RouteRequest;
import com.tourism.entity.TourOrder;
import com.tourism.entity.TourRoute;
import com.tourism.mapper.TourOrderMapper;
import com.tourism.mapper.TourRouteMapper;
import com.tourism.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    private static final Set<String> VALID_STATUSES = Set.of("DRAFT", "OPEN", "FULL", "CLOSED");
    private static final Set<String> PUBLIC_VISIBLE_STATUSES = Set.of("OPEN", "FULL");

    private final TourRouteMapper routeMapper;
    private final TourOrderMapper orderMapper;

    @Override
    public Page<RouteDTO> listPublic(int page, int pageSize, String keyword, String status) {
        if (status != null && !status.isBlank()) {
            if (!PUBLIC_VISIBLE_STATUSES.contains(status)) {
                throw BusinessException.badRequest("只允许查询 OPEN 或 FULL 状态的线路");
            }
        }

        LambdaQueryWrapper<TourRoute> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank()) {
            wrapper.eq(TourRoute::getStatus, status);
        } else {
            wrapper.in(TourRoute::getStatus, PUBLIC_VISIBLE_STATUSES);
        }

        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w
                    .like(TourRoute::getName, keyword)
                    .or()
                    .like(TourRoute::getItinerary, keyword)
            );
        }
        wrapper.orderByDesc(TourRoute::getCreatedAt);

        Page<TourRoute> pageResult = routeMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return convertPage(pageResult);
    }

    @Override
    public RouteDTO getPublicDetail(Long id) {
        TourRoute route = routeMapper.selectById(id);
        if (route == null || !PUBLIC_VISIBLE_STATUSES.contains(route.getStatus())) {
            throw BusinessException.notFound("线路不存在或未开放");
        }
        return RouteDTO.fromEntity(route);
    }

    @Override
    public Page<RouteDTO> listAdmin(int page, int pageSize, String keyword, String status) {
        LambdaQueryWrapper<TourRoute> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w
                    .like(TourRoute::getName, keyword)
                    .or()
                    .like(TourRoute::getItinerary, keyword)
            );
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(TourRoute::getStatus, status);
        }
        wrapper.orderByDesc(TourRoute::getCreatedAt);

        Page<TourRoute> pageResult = routeMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return convertPage(pageResult);
    }

    @Override
    public RouteDTO getAdminDetail(Long id) {
        TourRoute route = routeMapper.selectById(id);
        if (route == null) {
            throw BusinessException.notFound("线路不存在");
        }
        return RouteDTO.fromEntity(route);
    }

    @Override
    public RouteDTO create(RouteRequest request) {
        validateRequest(request);
        validatePrice(request.getPrice());
        validateQuota(request.getQuota());
        validateBookedCount(request.getBookedCount(), request.getQuota());
        if (request.getStatus() != null) {
            validateStatus(request.getStatus());
        }

        TourRoute route = new TourRoute();
        route.setName(request.getName());
        route.setItinerary(request.getItinerary());
        route.setPrice(request.getPrice());
        route.setDepartureTime(request.getDepartureTime());
        route.setQuota(request.getQuota());
        route.setBookedCount(request.getBookedCount() != null ? request.getBookedCount() : 0);
        route.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
        route.setCoverImageUrl(request.getCoverImageUrl());
        routeMapper.insert(route);

        return RouteDTO.fromEntity(route);
    }

    @Override
    public RouteDTO update(Long id, RouteRequest request) {
        TourRoute route = routeMapper.selectById(id);
        if (route == null) {
            throw BusinessException.notFound("线路不存在");
        }

        validateRequest(request);
        validatePrice(request.getPrice());
        validateQuota(request.getQuota());
        validateBookedCount(request.getBookedCount(), request.getQuota());
        if (request.getStatus() != null) {
            validateStatus(request.getStatus());
        }

        route.setName(request.getName());
        route.setItinerary(request.getItinerary());
        route.setPrice(request.getPrice());
        route.setDepartureTime(request.getDepartureTime());
        route.setQuota(request.getQuota());
        if (request.getBookedCount() != null) {
            route.setBookedCount(request.getBookedCount());
        }
        if (request.getStatus() != null) {
            route.setStatus(request.getStatus());
        }
        route.setCoverImageUrl(request.getCoverImageUrl());
        routeMapper.updateById(route);

        return RouteDTO.fromEntity(route);
    }

    @Override
    public void delete(Long id) {
        TourRoute route = routeMapper.selectById(id);
        if (route == null) {
            throw BusinessException.notFound("线路不存在");
        }

        Long orderCount = orderMapper.selectCount(
                new LambdaQueryWrapper<TourOrder>().eq(TourOrder::getRouteId, id));
        if (orderCount > 0) {
            throw BusinessException.conflict("该线路已有订单，不能删除，可改为关闭状态");
        }

        routeMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, String status) {
        validateStatus(status);

        TourRoute route = routeMapper.selectById(id);
        if (route == null) {
            throw BusinessException.notFound("线路不存在");
        }

        if ("FULL".equals(status) && route.getBookedCount() < route.getQuota()) {
            throw BusinessException.badRequest("名额未满，不能设为已满状态");
        }

        route.setStatus(status);
        routeMapper.updateById(route);
    }

    private void validateRequest(RouteRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw BusinessException.badRequest("线路名称不能为空");
        }
        if (request.getItinerary() == null || request.getItinerary().isBlank()) {
            throw BusinessException.badRequest("行程安排不能为空");
        }
        if (request.getDepartureTime() == null) {
            throw BusinessException.badRequest("出发时间不能为空");
        }
    }

    private void validatePrice(java.math.BigDecimal price) {
        if (price == null) {
            throw BusinessException.badRequest("价格不能为空");
        }
        if (price.compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw BusinessException.badRequest("价格不能小于 0");
        }
    }

    private void validateQuota(Integer quota) {
        if (quota == null) {
            throw BusinessException.badRequest("名额不能为空");
        }
        if (quota <= 0) {
            throw BusinessException.badRequest("名额必须大于 0");
        }
    }

    private void validateBookedCount(Integer bookedCount, Integer quota) {
        if (bookedCount != null && bookedCount < 0) {
            throw BusinessException.badRequest("已预订数量不能小于 0");
        }
        if (bookedCount != null && quota != null && bookedCount > quota) {
            throw BusinessException.badRequest("已预订数量不能大于名额");
        }
    }

    private void validateStatus(String status) {
        if (status == null || !VALID_STATUSES.contains(status)) {
            throw BusinessException.badRequest("状态值只能是 DRAFT、OPEN、FULL 或 CLOSED");
        }
    }

    private Page<RouteDTO> convertPage(Page<TourRoute> page) {
        Page<RouteDTO> dtoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        dtoPage.setRecords(page.getRecords().stream()
                .map(RouteDTO::fromEntity)
                .toList());
        return dtoPage;
    }
}
