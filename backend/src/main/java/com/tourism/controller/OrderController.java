package com.tourism.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tourism.common.Result;
import com.tourism.dto.OrderCreateRequest;
import com.tourism.dto.OrderDTO;
import com.tourism.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Result<OrderDTO> create(Authentication authentication,
                                   @Valid @RequestBody OrderCreateRequest request) {
        Long userId = (Long) authentication.getDetails();
        return Result.success("预订成功，等待管理员处理", orderService.create(userId, request));
    }

    @GetMapping("/my")
    public Result<Page<OrderDTO>> myOrders(Authentication authentication,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int pageSize,
                                            @RequestParam(required = false) String status) {
        Long userId = (Long) authentication.getDetails();
        return Result.success(orderService.listMyOrders(userId, page, pageSize, status));
    }

    @GetMapping("/{id}")
    public Result<OrderDTO> detail(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getDetails();
        return Result.success(orderService.getMyOrderDetail(userId, id));
    }

    @PutMapping("/{id}/cancel")
    public Result<OrderDTO> cancel(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getDetails();
        return Result.success("取消成功", orderService.cancelMyOrder(userId, id));
    }
}
