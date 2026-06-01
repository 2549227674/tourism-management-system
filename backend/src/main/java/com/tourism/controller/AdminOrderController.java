package com.tourism.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tourism.common.Result;
import com.tourism.dto.*;
import com.tourism.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public Result<Page<OrderDTO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        return Result.success(orderService.listAdmin(page, pageSize, keyword, status));
    }

    @GetMapping("/{id}")
    public Result<OrderDTO> detail(@PathVariable Long id) {
        return Result.success(orderService.getAdminDetail(id));
    }

    @PutMapping("/{id}/confirm")
    public Result<OrderDTO> confirm(@PathVariable Long id) {
        return Result.success("确认成功", orderService.confirm(id));
    }

    @PutMapping("/{id}/reject")
    public Result<OrderDTO> reject(@PathVariable Long id,
                                   @RequestBody(required = false) OrderRejectRequest request) {
        return Result.success("驳回成功", orderService.reject(id, request));
    }

    @PutMapping("/{id}/complete")
    public Result<OrderDTO> complete(@PathVariable Long id) {
        return Result.success("完成成功", orderService.complete(id));
    }

    @PutMapping("/{id}/cancel")
    public Result<OrderDTO> cancel(@PathVariable Long id,
                                   @RequestBody(required = false) OrderCancelRequest request) {
        return Result.success("取消成功", orderService.adminCancel(id, request));
    }
}
