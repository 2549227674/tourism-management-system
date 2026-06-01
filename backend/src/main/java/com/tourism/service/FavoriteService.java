package com.tourism.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tourism.dto.FavoriteDTO;
import com.tourism.dto.FavoriteRequest;

public interface FavoriteService {

    FavoriteDTO add(Long userId, FavoriteRequest request);

    Page<FavoriteDTO> listMy(Long userId, int page, int pageSize, String targetType);

    void remove(Long userId, Long id);
}
