package com.tourism.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tourism.common.Result;
import com.tourism.dto.AnnouncementDTO;
import com.tourism.dto.AnnouncementRequest;
import com.tourism.service.AnnouncementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/announcements")
@RequiredArgsConstructor
public class AdminAnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    public Result<Page<AnnouncementDTO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        return Result.success(announcementService.listAdmin(page, pageSize, keyword, status));
    }

    @GetMapping("/{id}")
    public Result<AnnouncementDTO> detail(@PathVariable Long id) {
        return Result.success(announcementService.getAdminById(id));
    }

    @PostMapping
    public Result<AnnouncementDTO> create(@Valid @RequestBody AnnouncementRequest request) {
        return Result.success("公告创建成功", announcementService.create(request));
    }

    @PutMapping("/{id}")
    public Result<AnnouncementDTO> update(@PathVariable Long id,
                                          @Valid @RequestBody AnnouncementRequest request) {
        return Result.success("公告修改成功", announcementService.update(id, request));
    }

    @PutMapping("/{id}/publish")
    public Result<AnnouncementDTO> publish(@PathVariable Long id) {
        return Result.success("公告发布成功", announcementService.publish(id));
    }

    @PutMapping("/{id}/offline")
    public Result<AnnouncementDTO> offline(@PathVariable Long id) {
        return Result.success("公告下架成功", announcementService.offline(id));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return Result.success("公告删除成功", null);
    }
}
