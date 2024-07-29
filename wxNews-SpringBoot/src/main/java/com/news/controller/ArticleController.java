package com.news.controller;

import com.news.domain.dto.ArticleDTO;
import com.news.domain.dto.PageDTO.ArticlePageDTO;
import com.news.domain.pojo.Article;
import com.news.domain.vo.PageBean;
import com.news.service.ArticleService;
import com.news.utils.Result;
import com.news.utils.ThreadLocalUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    //分页获取文章列表
    @GetMapping
    public Result<PageBean<Article>> list(ArticlePageDTO articlePageDTO) {
        PageBean<Article> pb = articleService.listPage(articlePageDTO);
        return Result.ok(pb);
    }

    //发布文章
    @PostMapping
    public Result<String> add(@RequestBody @Validated ArticleDTO articleDTO) {
        return articleService.add(articleDTO);
    }

    //更新文章
    @PutMapping
    public Result<String> update(@RequestBody @Validated(ArticleDTO.Update.class) ArticleDTO articleDTO) {
        return articleService.updateArticle(articleDTO);
    }

    //获取文章文章详细信息
    @GetMapping("/detail")
    public Result<Article> detail(@NonNull Long id) {
        Article article = articleService.getById(id);
        if (article != null) return Result.ok(article);
        return Result.error("文章ID不存在");
    }

    //删除文章
    @DeleteMapping
    public Result<String> delete(@NonNull Long id) {
        return articleService.delete(id);
    }

    //添加收藏
    @PostMapping("/favorite")
    public Result<String> addFavorite(@NonNull Long articleId) {
        return articleService.addFavorite(articleId);
    }

    //删除收藏
    @DeleteMapping("/favorite")
    public Result<String> deleteFavorite(@NonNull Long articleId) {
        if (articleService.deleteFavorite(articleId, ThreadLocalUtil.get())) return Result.ok(null);
        return Result.error();
    }

    //查询所有收藏
    @GetMapping("/favorite")
    public Result<List<Article>> listFavorite() {
        return Result.ok(articleService.listFavorite(ThreadLocalUtil.get()));
    }

    //删除所有收藏
    @DeleteMapping("/favorite/all")
    public Result<String> deleteAllFavorite() {
        if (articleService.deleteAllFavoriteByUserId(ThreadLocalUtil.get())) return Result.ok(null);
        return Result.error();
    }
}
