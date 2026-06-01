package com.tourism.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tourism.common.Result;
import com.tourism.dto.SpotDTO;
import com.tourism.service.SpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spots")
@RequiredArgsConstructor
public class SpotController {

    private final SpotService spotService;

    @GetMapping
    public Result<Page<SpotDTO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        return Result.success(spotService.listPublic(page, pageSize, keyword, categoryId));
    }

    @GetMapping("/{id}")
    public Result<SpotDTO> detail(@PathVariable Long id) {
        return Result.success(spotService.getDetail(id));
    }
}
