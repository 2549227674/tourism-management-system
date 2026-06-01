package com.tourism.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tourism.common.Result;
import com.tourism.dto.RouteDTO;
import com.tourism.dto.RouteRequest;
import com.tourism.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/routes")
@RequiredArgsConstructor
public class AdminRouteController {

    private final RouteService routeService;

    @GetMapping
    public Result<Page<RouteDTO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        return Result.success(routeService.listAdmin(page, pageSize, keyword, status));
    }

    @GetMapping("/{id}")
    public Result<RouteDTO> detail(@PathVariable Long id) {
        return Result.success(routeService.getAdminDetail(id));
    }

    @PostMapping
    public Result<RouteDTO> create(@Valid @RequestBody RouteRequest request) {
        return Result.success("新增成功", routeService.create(request));
    }

    @PutMapping("/{id}")
    public Result<RouteDTO> update(@PathVariable Long id, @Valid @RequestBody RouteRequest request) {
        return Result.success("修改成功", routeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        routeService.delete(id);
        return Result.success("删除成功", null);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        routeService.updateStatus(id, status);
        return Result.success("状态更新成功", null);
    }
}
