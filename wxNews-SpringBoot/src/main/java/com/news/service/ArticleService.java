package com.news.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.news.domain.dto.ArticleDTO;
import com.news.domain.dto.PageDTO.ArticlePageDTO;
import com.news.domain.pojo.Article;
import com.news.domain.vo.PageBean;
import com.news.utils.Result;

import java.util.List;

public interface ArticleService extends IService<Article> {
    //添加文章
    Result<String> add(ArticleDTO articleDTO);

    //分页查询
    PageBean<Article> listPage(ArticlePageDTO articlePageDTO);

    //修改文章
    Result<String> updateArticle(ArticleDTO articleDTO);

    //删除文章
    Result<String> delete(Long id);

    //删除用户所有文章
    boolean deleteAllByUserId(Long userId);

    //添加收藏
    Result<String> addFavorite(Long articleId);

    //删除收藏
    boolean deleteFavorite(Long articleId, Long userId);

    //查询用户所有收藏
    List<Article> listFavorite(Long userId);

    //删除所有收藏
    boolean deleteAllFavoriteByUserId(Long userId);
}
