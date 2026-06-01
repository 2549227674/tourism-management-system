package com.tourism.service;

import com.tourism.dto.CategoryDTO;
import com.tourism.dto.CategoryRequest;

import java.util.List;

public interface CategoryService {

    List<CategoryDTO> listAll();

    List<CategoryDTO> listActive();

    CategoryDTO create(CategoryRequest request);

    CategoryDTO update(Long id, CategoryRequest request);

    void delete(Long id);
}
