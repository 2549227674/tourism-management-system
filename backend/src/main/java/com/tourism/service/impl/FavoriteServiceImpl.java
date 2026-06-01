package com.tourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tourism.common.BusinessException;
import com.tourism.dto.FavoriteDTO;
import com.tourism.dto.FavoriteRequest;
import com.tourism.entity.Favorite;
import com.tourism.entity.ScenicSpot;
import com.tourism.entity.TourRoute;
import com.tourism.mapper.FavoriteMapper;
import com.tourism.mapper.ScenicSpotMapper;
import com.tourism.mapper.TourRouteMapper;
import com.tourism.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private static final Set<String> VALID_TARGET_TYPES = Set.of("SPOT", "ROUTE");

    private final FavoriteMapper favoriteMapper;
    private final ScenicSpotMapper spotMapper;
    private final TourRouteMapper routeMapper;

    @Override
    public FavoriteDTO add(Long userId, FavoriteRequest request) {
        validateTargetType(request.getTargetType());

        if ("SPOT".equals(request.getTargetType())) {
            ScenicSpot spot = spotMapper.selectById(request.getTargetId());
            if (spot == null || !"ON".equals(spot.getStatus())) {
                throw BusinessException.badRequest("景点不存在或已下架");
            }
        } else {
            TourRoute route = routeMapper.selectById(request.getTargetId());
            if (route == null || (!"OPEN".equals(route.getStatus()) && !"FULL".equals(route.getStatus()))) {
                throw BusinessException.badRequest("线路不存在或未开放");
            }
        }

        Long count = favoriteMapper.selectCount(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getTargetType, request.getTargetType())
                        .eq(Favorite::getTargetId, request.getTargetId()));
        if (count > 0) {
            throw BusinessException.conflict("已收藏该内容");
        }

        Favorite fav = new Favorite();
        fav.setUserId(userId);
        fav.setTargetType(request.getTargetType());
        fav.setTargetId(request.getTargetId());
        favoriteMapper.insert(fav);

        return toDTO(fav);
    }

    @Override
    public Page<FavoriteDTO> listMy(Long userId, int page, int pageSize, String targetType) {
        if (targetType != null && !targetType.isBlank()) {
            validateTargetType(targetType);
        }

        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId);
        if (targetType != null && !targetType.isBlank()) {
            wrapper.eq(Favorite::getTargetType, targetType);
        }
        wrapper.orderByDesc(Favorite::getCreatedAt);

        Page<Favorite> pageResult = favoriteMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return convertPage(pageResult);
    }

    @Override
    public void remove(Long userId, Long id) {
        Favorite fav = favoriteMapper.selectById(id);
        if (fav == null || !fav.getUserId().equals(userId)) {
            throw BusinessException.notFound("收藏记录不存在");
        }
        favoriteMapper.deleteById(id);
    }

    private void validateTargetType(String targetType) {
        if (targetType == null || !VALID_TARGET_TYPES.contains(targetType)) {
            throw BusinessException.badRequest("收藏类型只能是 SPOT 或 ROUTE");
        }
    }

    private FavoriteDTO toDTO(Favorite fav) {
        FavoriteDTO dto = FavoriteDTO.fromEntity(fav);
        dto.setTargetName(getTargetName(fav.getTargetType(), fav.getTargetId()));
        return dto;
    }

    private Page<FavoriteDTO> convertPage(Page<Favorite> page) {
        Page<FavoriteDTO> dtoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        dtoPage.setRecords(page.getRecords().stream().map(this::toDTO).toList());
        return dtoPage;
    }

    private String getTargetName(String targetType, Long targetId) {
        if ("SPOT".equals(targetType)) {
            ScenicSpot spot = spotMapper.selectById(targetId);
            return spot != null ? spot.getName() : null;
        } else {
            TourRoute route = routeMapper.selectById(targetId);
            return route != null ? route.getName() : null;
        }
    }
}
