package com.news.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.news.domain.pojo.Favorite;

import java.util.List;

public interface FavoriteService extends IService<Favorite> {

    // 添加收藏
    boolean add(Long articleId);

    // 删除收藏
    boolean delete(Long articleId, Long userId);

    //查询所有收藏
    List<Long> listFavorite(Long userId);

}
