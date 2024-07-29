package com.news.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.news.domain.dto.CategoryDTO;
import com.news.domain.pojo.Category;
import com.news.utils.Result;

import java.util.List;

public interface CategoryService extends IService<Category> {

    //获取分类列表
    Result<List<Category>> getList();

    //添加分类
    Result<String> add(CategoryDTO addCategoryDTO);

    //更新分类
    Result<String> updateCategory(CategoryDTO categoryDTO);

    //删除分类
    Result<String> delete(Long id);

    //判断分类ID是否存在
    boolean categoryByID(Long categoryId);

    //分类文章数+1
    boolean ArticlesNumAdd(Long categoryId);

    //分类文章数-1
    boolean ArticlesNumReduce(Long categoryId);
}

