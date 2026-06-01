package com.tourism.controller;

import com.tourism.common.Result;
import com.tourism.dto.CategoryDTO;
import com.tourism.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public Result<List<CategoryDTO>> listActive() {
        return Result.success(categoryService.listActive());
    }
}
