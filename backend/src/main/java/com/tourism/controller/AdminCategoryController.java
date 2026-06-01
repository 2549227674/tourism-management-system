package com.tourism.controller;

import com.tourism.common.Result;
import com.tourism.dto.CategoryDTO;
import com.tourism.dto.CategoryRequest;
import com.tourism.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public Result<List<CategoryDTO>> listAll() {
        return Result.success(categoryService.listAll());
    }

    @PostMapping
    public Result<CategoryDTO> create(@Valid @RequestBody CategoryRequest request) {
        return Result.success("新增成功", categoryService.create(request));
    }

    @PutMapping("/{id}")
    public Result<CategoryDTO> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return Result.success("修改成功", categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success("删除成功", null);
    }
}
