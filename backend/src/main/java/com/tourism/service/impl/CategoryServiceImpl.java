package com.tourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tourism.common.BusinessException;
import com.tourism.dto.CategoryDTO;
import com.tourism.dto.CategoryRequest;
import com.tourism.entity.SpotCategory;
import com.tourism.mapper.SpotCategoryMapper;
import com.tourism.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final SpotCategoryMapper categoryMapper;

    @Override
    public List<CategoryDTO> listAll() {
        List<SpotCategory> list = categoryMapper.selectList(
                new LambdaQueryWrapper<SpotCategory>().orderByAsc(SpotCategory::getSortOrder)
        );
        return list.stream().map(CategoryDTO::fromEntity).toList();
    }

    @Override
    public List<CategoryDTO> listActive() {
        List<SpotCategory> list = categoryMapper.selectList(
                new LambdaQueryWrapper<SpotCategory>()
                        .eq(SpotCategory::getStatus, "ON")
                        .orderByAsc(SpotCategory::getSortOrder)
        );
        return list.stream().map(CategoryDTO::fromEntity).toList();
    }

    @Override
    public CategoryDTO create(CategoryRequest request) {
        Long count = categoryMapper.selectCount(
                new LambdaQueryWrapper<SpotCategory>().eq(SpotCategory::getName, request.getName())
        );
        if (count > 0) {
            throw BusinessException.conflict("分类名称已存在");
        }

        SpotCategory category = new SpotCategory();
        category.setName(request.getName());
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        category.setStatus("ON");
        categoryMapper.insert(category);

        return CategoryDTO.fromEntity(category);
    }

    @Override
    public CategoryDTO update(Long id, CategoryRequest request) {
        SpotCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw BusinessException.notFound("分类不存在");
        }

        Long count = categoryMapper.selectCount(
                new LambdaQueryWrapper<SpotCategory>()
                        .eq(SpotCategory::getName, request.getName())
                        .ne(SpotCategory::getId, id)
        );
        if (count > 0) {
            throw BusinessException.conflict("分类名称已存在");
        }

        category.setName(request.getName());
        if (request.getSortOrder() != null) {
            category.setSortOrder(request.getSortOrder());
        }
        categoryMapper.updateById(category);

        return CategoryDTO.fromEntity(category);
    }

    @Override
    public void delete(Long id) {
        SpotCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw BusinessException.notFound("分类不存在");
        }
        categoryMapper.deleteById(id);
    }
}
