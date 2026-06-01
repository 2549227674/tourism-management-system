package com.tourism.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tourism.common.Result;
import com.tourism.dto.RouteDTO;
import com.tourism.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @GetMapping
    public Result<Page<RouteDTO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        return Result.success(routeService.listPublic(page, pageSize, keyword, status));
    }

    @GetMapping("/{id}")
    public Result<RouteDTO> detail(@PathVariable Long id) {
        return Result.success(routeService.getPublicDetail(id));
    }
}
