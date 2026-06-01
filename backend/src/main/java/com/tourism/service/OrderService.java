package com.tourism.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tourism.dto.*;

public interface OrderService {

    OrderDTO create(Long userId, OrderCreateRequest request);

    Page<OrderDTO> listMyOrders(Long userId, int page, int pageSize, String status);

    OrderDTO getMyOrderDetail(Long userId, Long orderId);

    OrderDTO cancelMyOrder(Long userId, Long orderId);

    Page<OrderDTO> listAdmin(int page, int pageSize, String keyword, String status);

    OrderDTO getAdminDetail(Long orderId);

    OrderDTO confirm(Long orderId);

    OrderDTO reject(Long orderId, OrderRejectRequest request);

    OrderDTO complete(Long orderId);

    OrderDTO adminCancel(Long orderId, OrderCancelRequest request);
}
