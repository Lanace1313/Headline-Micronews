package com.news.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.news.domain.dto.CategoryDTO;
import com.news.domain.pojo.Category;
import com.news.mapper.CategoryMapper;
import com.news.service.CategoryService;
import com.news.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.news.utils.RedisConstants.CATEGORY_INFO_KEY;
import static com.news.utils.RedisConstants.CATEGORY_INFO_TTL;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService {

    private final StringRedisTemplate redisTemplate;

    //获取类别列表
    @Override
    public Result<List<Category>> getList() {
        //获取类别列表
        String json = redisTemplate.opsForValue().get(CATEGORY_INFO_KEY);
        if (StrUtil.isNotBlank(json)) {
            //存在，直接返回
            List<Category> categories = JSONUtil.toList(json, Category.class);
            return Result.ok(categories);
        }
        //不存在，查询数据库
        List<Category> categories = list();
        //添加到redis
        redisTemplate.opsForValue().set(CATEGORY_INFO_KEY, JSONUtil.toJsonStr(categories), CATEGORY_INFO_TTL, TimeUnit.HOURS);
        return Result.ok(categories);
    }

    //添加类别
    @Override
    public Result<String> add(CategoryDTO addCategoryDTO) {
        if (lambdaQuery().eq(Category::getCategoryName, addCategoryDTO.getCategoryName()).count() > 0) {
            return Result.error("类别重复");
        } else {//添加类别
            Category category = new Category()
                    .setCategoryName(addCategoryDTO.getCategoryName());
            //保存数据
            if (!save(category)) return Result.error();
            //删除redis数据
            redisTemplate.delete(CATEGORY_INFO_KEY);
            return Result.ok(null);
        }
    }

    //修改类别
    @Override
    public Result<String> updateCategory(CategoryDTO categoryDTO) {
        Category category = getById(categoryDTO.getId());
        if (category == null) return Result.error("类别不存在");
        else if (Objects.equals(categoryDTO.getCategoryName(), category.getCategoryName())) {
            //类别未修改
            return Result.ok(null);
        } else if (lambdaQuery().eq(Category::getCategoryName, categoryDTO.getCategoryName()).count() > 0) {
            return Result.error("类别重复");
        } else {
            //修改类别
            lambdaUpdate().eq(Category::getId, categoryDTO.getId())
                    .set(Category::getCategoryName, categoryDTO.getCategoryName()).update();
            //删除redis数据
            redisTemplate.delete(CATEGORY_INFO_KEY);
            return Result.ok(null);
        }
    }

    //删除类别
    @Override
    public Result<String> delete(Long id) {
        Category category = getById(id);
        if (category == null) return Result.error("类别不存在");
        else if (category.getArticlesNum() > 0) {
            return Result.error("分类未清空");
        }else {
            removeById(id);
            //删除redis数据
            redisTemplate.delete(CATEGORY_INFO_KEY);
            return Result.ok(null);
        }

    }

    //判断类别ID是否存在
    public boolean categoryByID(Long categoryId) {
        return lambdaQuery().eq(Category::getId, categoryId).count() > 0;
    }

    //文章分类+1
    public boolean ArticlesNumAdd(Long categoryId) {
        Category category = getById(categoryId);
        return lambdaUpdate().eq(Category::getId, category.getId())
                .set(Category::getArticlesNum, category.getArticlesNum() + 1)
                .update();
    }

    //文章分类-1
    public boolean ArticlesNumReduce(Long categoryId) {
        Category category = getById(categoryId);
        return lambdaUpdate().eq(Category::getId, category.getId())
                .set(Category::getArticlesNum, category.getArticlesNum() - 1)
                .update();
    }
}




