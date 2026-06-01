package com.tourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tourism.common.BusinessException;
import com.tourism.dto.SpotDTO;
import com.tourism.dto.SpotRequest;
import com.tourism.entity.ScenicSpot;
import com.tourism.entity.SpotCategory;
import com.tourism.mapper.ScenicSpotMapper;
import com.tourism.mapper.SpotCategoryMapper;
import com.tourism.service.SpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpotServiceImpl implements SpotService {

    private final ScenicSpotMapper spotMapper;
    private final SpotCategoryMapper categoryMapper;

    @Override
    public Page<SpotDTO> listPublic(int page, int pageSize, String keyword, Long categoryId) {
        LambdaQueryWrapper<ScenicSpot> wrapper = new LambdaQueryWrapper<ScenicSpot>()
                .eq(ScenicSpot::getStatus, "ON");

        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w
                    .like(ScenicSpot::getName, keyword)
                    .or()
                    .like(ScenicSpot::getAddress, keyword)
            );
        }
        if (categoryId != null) {
            wrapper.eq(ScenicSpot::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(ScenicSpot::getCreatedAt);

        Page<ScenicSpot> pageResult = spotMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return convertPage(pageResult);
    }

    @Override
    public SpotDTO getDetail(Long id) {
        ScenicSpot spot = spotMapper.selectById(id);
        if (spot == null || !"ON".equals(spot.getStatus())) {
            throw BusinessException.notFound("景点不存在或已下架");
        }
        return toDTO(spot);
    }

    @Override
    public Page<SpotDTO> listAdmin(int page, int pageSize, String keyword, Long categoryId, String status) {
        LambdaQueryWrapper<ScenicSpot> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w
                    .like(ScenicSpot::getName, keyword)
                    .or()
                    .like(ScenicSpot::getAddress, keyword)
            );
        }
        if (categoryId != null) {
            wrapper.eq(ScenicSpot::getCategoryId, categoryId);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(ScenicSpot::getStatus, status);
        }
        wrapper.orderByDesc(ScenicSpot::getCreatedAt);

        Page<ScenicSpot> pageResult = spotMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return convertPage(pageResult);
    }

    @Override
    public SpotDTO create(SpotRequest request) {
        validateCategoryId(request.getCategoryId());

        ScenicSpot spot = new ScenicSpot();
        spot.setName(request.getName());
        spot.setCategoryId(request.getCategoryId());
        spot.setAddress(request.getAddress());
        spot.setTicketPrice(request.getTicketPrice());
        spot.setOpenTime(request.getOpenTime());
        spot.setIntroduction(request.getIntroduction());
        spot.setImageUrl(request.getImageUrl());
        spot.setStatus("ON");
        spotMapper.insert(spot);

        return toDTO(spot);
    }

    @Override
    public SpotDTO update(Long id, SpotRequest request) {
        ScenicSpot spot = spotMapper.selectById(id);
        if (spot == null) {
            throw BusinessException.notFound("景点不存在");
        }

        validateCategoryId(request.getCategoryId());

        spot.setName(request.getName());
        spot.setCategoryId(request.getCategoryId());
        spot.setAddress(request.getAddress());
        spot.setTicketPrice(request.getTicketPrice());
        spot.setOpenTime(request.getOpenTime());
        spot.setIntroduction(request.getIntroduction());
        spot.setImageUrl(request.getImageUrl());
        spotMapper.updateById(spot);

        return toDTO(spot);
    }

    @Override
    public void delete(Long id) {
        ScenicSpot spot = spotMapper.selectById(id);
        if (spot == null) {
            throw BusinessException.notFound("景点不存在");
        }
        spotMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, String status) {
        if (!"ON".equals(status) && !"OFF".equals(status)) {
            throw BusinessException.badRequest("状态值只能是 ON 或 OFF");
        }

        ScenicSpot spot = spotMapper.selectById(id);
        if (spot == null) {
            throw BusinessException.notFound("景点不存在");
        }
        spot.setStatus(status);
        spotMapper.updateById(spot);
    }

    private void validateCategoryId(Long categoryId) {
        if (categoryId != null) {
            SpotCategory category = categoryMapper.selectById(categoryId);
            if (category == null) {
                throw BusinessException.badRequest("指定的分类不存在");
            }
            if (!"ON".equals(category.getStatus())) {
                throw BusinessException.badRequest("指定的分类已禁用");
            }
        }
    }

    private Page<SpotDTO> convertPage(Page<ScenicSpot> page) {
        Map<Long, String> categoryMap = categoryMapper.selectList(null).stream()
                .collect(Collectors.toMap(SpotCategory::getId, SpotCategory::getName));

        Page<SpotDTO> dtoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        dtoPage.setRecords(page.getRecords().stream().map(spot -> {
            SpotDTO dto = SpotDTO.fromEntity(spot);
            dto.setCategoryName(categoryMap.get(spot.getCategoryId()));
            return dto;
        }).toList());
        return dtoPage;
    }

    private SpotDTO toDTO(ScenicSpot spot) {
        SpotDTO dto = SpotDTO.fromEntity(spot);
        if (spot.getCategoryId() != null) {
            SpotCategory category = categoryMapper.selectById(spot.getCategoryId());
            if (category != null) {
                dto.setCategoryName(category.getName());
            }
        }
        return dto;
    }
}
