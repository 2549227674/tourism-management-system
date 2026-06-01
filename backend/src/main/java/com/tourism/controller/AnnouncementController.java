package com.tourism.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tourism.common.Result;
import com.tourism.dto.AnnouncementDTO;
import com.tourism.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    public Result<Page<AnnouncementDTO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(announcementService.listPublic(page, pageSize, keyword));
    }

    @GetMapping("/{id}")
    public Result<AnnouncementDTO> detail(@PathVariable Long id) {
        return Result.success(announcementService.getPublicById(id));
    }
}
