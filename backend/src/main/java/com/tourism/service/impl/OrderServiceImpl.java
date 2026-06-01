package com.tourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tourism.common.BusinessException;
import com.tourism.dto.*;
import com.tourism.entity.TourOrder;
import com.tourism.entity.TourRoute;
import com.tourism.mapper.TourOrderMapper;
import com.tourism.mapper.TourRouteMapper;
import com.tourism.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Set<String> VALID_STATUSES = Set.of("PENDING", "CONFIRMED", "CANCELLED", "REJECTED", "COMPLETED");

    private final TourOrderMapper orderMapper;
    private final TourRouteMapper routeMapper;

    @Override
    @Transactional
    public OrderDTO create(Long userId, OrderCreateRequest request) {
        if (request.getPeopleCount() == null || request.getPeopleCount() <= 0) {
            throw BusinessException.badRequest("预订人数必须大于 0");
        }

        TourRoute route = routeMapper.selectById(request.getRouteId());
        if (route == null) {
            throw BusinessException.badRequest("线路不存在");
        }
        if (!"OPEN".equals(route.getStatus())) {
            throw BusinessException.badRequest("线路未开放预订");
        }

        int remaining = route.getQuota() - route.getBookedCount();
        if (remaining < request.getPeopleCount()) {
            throw BusinessException.conflict("名额不足，剩余 " + remaining + " 个名额");
        }

        BigDecimal totalAmount = route.getPrice().multiply(BigDecimal.valueOf(request.getPeopleCount()));

        TourOrder order = new TourOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setRouteId(request.getRouteId());
        order.setPeopleCount(request.getPeopleCount());
        order.setContactName(request.getContactName());
        order.setContactPhone(request.getContactPhone());
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");
        orderMapper.insert(order);

        route.setBookedCount(route.getBookedCount() + request.getPeopleCount());
        if (route.getBookedCount() >= route.getQuota()) {
            route.setStatus("FULL");
        }
        routeMapper.updateById(route);

        return toDTO(order, route.getName());
    }

    @Override
    public Page<OrderDTO> listMyOrders(Long userId, int page, int pageSize, String status) {
        if (status != null && !status.isBlank()) {
            validateStatus(status);
        }

        LambdaQueryWrapper<TourOrder> wrapper = new LambdaQueryWrapper<TourOrder>()
                .eq(TourOrder::getUserId, userId);
        if (status != null && !status.isBlank()) {
            wrapper.eq(TourOrder::getStatus, status);
        }
        wrapper.orderByDesc(TourOrder::getCreatedAt);

        Page<TourOrder> pageResult = orderMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return convertPage(pageResult);
    }

    @Override
    public OrderDTO getMyOrderDetail(Long userId, Long orderId) {
        TourOrder order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw BusinessException.notFound("订单不存在");
        }
        return toDTO(order, getRouteName(order.getRouteId()));
    }

    @Override
    @Transactional
    public OrderDTO cancelMyOrder(Long userId, Long orderId) {
        TourOrder order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw BusinessException.notFound("订单不存在");
        }

        if (!"PENDING".equals(order.getStatus()) && !"CONFIRMED".equals(order.getStatus())) {
            throw BusinessException.badRequest("当前状态不允许取消");
        }

        order.setStatus("CANCELLED");
        orderMapper.updateById(order);

        releaseQuota(order.getRouteId(), order.getPeopleCount());

        TourRoute route = routeMapper.selectById(order.getRouteId());
        return toDTO(order, route != null ? route.getName() : null);
    }

    @Override
    public Page<OrderDTO> listAdmin(int page, int pageSize, String keyword, String status) {
        if (status != null && !status.isBlank()) {
            validateStatus(status);
        }

        LambdaQueryWrapper<TourOrder> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w
                    .like(TourOrder::getOrderNo, keyword)
                    .or()
                    .like(TourOrder::getContactName, keyword)
                    .or()
                    .like(TourOrder::getContactPhone, keyword)
            );
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(TourOrder::getStatus, status);
        }
        wrapper.orderByDesc(TourOrder::getCreatedAt);

        Page<TourOrder> pageResult = orderMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return convertPage(pageResult);
    }

    @Override
    public OrderDTO getAdminDetail(Long orderId) {
        TourOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw BusinessException.notFound("订单不存在");
        }
        return toDTO(order, getRouteName(order.getRouteId()));
    }

    @Override
    public OrderDTO confirm(Long orderId) {
        TourOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw BusinessException.notFound("订单不存在");
        }
        if (!"PENDING".equals(order.getStatus())) {
            throw BusinessException.badRequest("只有待处理订单才能确认");
        }

        order.setStatus("CONFIRMED");
        orderMapper.updateById(order);

        return toDTO(order, getRouteName(order.getRouteId()));
    }

    @Override
    @Transactional
    public OrderDTO reject(Long orderId, OrderRejectRequest request) {
        TourOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw BusinessException.notFound("订单不存在");
        }
        if (!"PENDING".equals(order.getStatus())) {
            throw BusinessException.badRequest("只有待处理订单才能驳回");
        }

        order.setStatus("REJECTED");
        if (request != null && request.getRemark() != null) {
            order.setRemark(request.getRemark());
        }
        orderMapper.updateById(order);

        releaseQuota(order.getRouteId(), order.getPeopleCount());

        TourRoute route = routeMapper.selectById(order.getRouteId());
        return toDTO(order, route != null ? route.getName() : null);
    }

    @Override
    public OrderDTO complete(Long orderId) {
        TourOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw BusinessException.notFound("订单不存在");
        }
        if (!"CONFIRMED".equals(order.getStatus())) {
            throw BusinessException.badRequest("只有已确认订单才能完成");
        }

        order.setStatus("COMPLETED");
        orderMapper.updateById(order);

        return toDTO(order, getRouteName(order.getRouteId()));
    }

    @Override
    @Transactional
    public OrderDTO adminCancel(Long orderId, OrderCancelRequest request) {
        TourOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw BusinessException.notFound("订单不存在");
        }
        if (!"PENDING".equals(order.getStatus()) && !"CONFIRMED".equals(order.getStatus())) {
            throw BusinessException.badRequest("当前状态不允许取消");
        }

        order.setStatus("CANCELLED");
        if (request != null && request.getRemark() != null) {
            order.setRemark(request.getRemark());
        }
        orderMapper.updateById(order);

        releaseQuota(order.getRouteId(), order.getPeopleCount());

        TourRoute route = routeMapper.selectById(order.getRouteId());
        return toDTO(order, route != null ? route.getName() : null);
    }

    private void releaseQuota(Long routeId, int peopleCount) {
        TourRoute route = routeMapper.selectById(routeId);
        if (route == null) {
            return;
        }
        int newBookedCount = Math.max(0, route.getBookedCount() - peopleCount);
        route.setBookedCount(newBookedCount);
        if ("FULL".equals(route.getStatus()) && !"CLOSED".equals(route.getStatus())) {
            route.setStatus("OPEN");
        }
        routeMapper.updateById(route);
    }

    private void validateStatus(String status) {
        if (status == null || !VALID_STATUSES.contains(status)) {
            throw BusinessException.badRequest("状态值只能是 PENDING、CONFIRMED、CANCELLED、REJECTED 或 COMPLETED");
        }
    }

    private String getRouteName(Long routeId) {
        TourRoute route = routeMapper.selectById(routeId);
        return route != null ? route.getName() : null;
    }

    private OrderDTO toDTO(TourOrder order, String routeName) {
        OrderDTO dto = OrderDTO.fromEntity(order);
        dto.setRouteName(routeName);
        return dto;
    }

    private Page<OrderDTO> convertPage(Page<TourOrder> page) {
        Page<OrderDTO> dtoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        dtoPage.setRecords(page.getRecords().stream().map(order -> {
            String routeName = getRouteName(order.getRouteId());
            return toDTO(order, routeName);
        }).toList());
        return dtoPage;
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "TR" + timestamp + random;
    }
}
