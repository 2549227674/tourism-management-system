package com.tourism.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tourism.common.Result;
import com.tourism.dto.SpotDTO;
import com.tourism.dto.SpotRequest;
import com.tourism.service.SpotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/spots")
@RequiredArgsConstructor
public class AdminSpotController {

    private final SpotService spotService;

    @GetMapping
    public Result<Page<SpotDTO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status) {
        return Result.success(spotService.listAdmin(page, pageSize, keyword, categoryId, status));
    }

    @PostMapping
    public Result<SpotDTO> create(@Valid @RequestBody SpotRequest request) {
        return Result.success("新增成功", spotService.create(request));
    }

    @PutMapping("/{id}")
    public Result<SpotDTO> update(@PathVariable Long id, @Valid @RequestBody SpotRequest request) {
        return Result.success("修改成功", spotService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        spotService.delete(id);
        return Result.success("删除成功", null);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        spotService.updateStatus(id, status);
        return Result.success("状态更新成功", null);
    }
}
