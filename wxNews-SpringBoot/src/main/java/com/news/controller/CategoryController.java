package com.news.controller;

import com.news.domain.dto.CategoryDTO;
import com.news.domain.pojo.Category;
import com.news.service.CategoryService;
import com.news.utils.Result;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    //获取分类列表
    @GetMapping
    public Result<List<Category>> list() {
        return categoryService.getList();
    }

    //添加分类信息
    @PreAuthorize("hasAnyAuthority('ROOT')")
    @PostMapping
    public Result<String> add(@RequestBody @Validated(CategoryDTO.Add.class) CategoryDTO CategoryDTO) {
        return categoryService.add(CategoryDTO);
    }

    //更新分类信息
    @PreAuthorize("hasAnyAuthority('ROOT')")
    @PutMapping
    public Result<String> update(@RequestBody @Validated(CategoryDTO.Update.class) CategoryDTO categoryDTO) {
        return categoryService.updateCategory(categoryDTO);
    }

    //删除分类信息
    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ROOT')")
    public Result<String> delete(@NonNull Long id) {
        return categoryService.delete(id);
    }

    //获取分类信息
    @PreAuthorize("hasAnyAuthority('ROOT')")
    @GetMapping("/detail")
    public Result<Category> detail(@NonNull Long id) {
        Category category = categoryService.getById(id);
        if (category != null) return Result.ok(category);
        return Result.error("分类不存在");
    }
}
