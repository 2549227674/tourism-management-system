package com.tourism.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tourism.common.Result;
import com.tourism.dto.FavoriteDTO;
import com.tourism.dto.FavoriteRequest;
import com.tourism.service.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping
    public Result<FavoriteDTO> add(Authentication authentication,
                                   @Valid @RequestBody FavoriteRequest request) {
        Long userId = (Long) authentication.getDetails();
        return Result.success("收藏成功", favoriteService.add(userId, request));
    }

    @GetMapping("/my")
    public Result<Page<FavoriteDTO>> myFavorites(Authentication authentication,
                                                  @RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                  @RequestParam(required = false) String targetType) {
        Long userId = (Long) authentication.getDetails();
        return Result.success(favoriteService.listMy(userId, page, pageSize, targetType));
    }

    @DeleteMapping("/{id}")
    public Result<Void> remove(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getDetails();
        favoriteService.remove(userId, id);
        return Result.success("取消收藏成功", null);
    }
}
