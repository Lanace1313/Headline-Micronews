package com.news.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.news.domain.pojo.Favorite;
import com.news.mapper.FavoriteMapper;
import com.news.service.FavoriteService;
import com.news.utils.ThreadLocalUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite>
        implements FavoriteService {

    // 添加收藏
    @Override
    public boolean add(Long articleId) {
        if (lambdaQuery().eq(Favorite::getArticleId, articleId)
                .eq(Favorite::getUserId, ThreadLocalUtil.get()).count() == 0) {
            Favorite favorite = new Favorite()
                    .setUserId(ThreadLocalUtil.get())
                    .setArticleId(articleId);
            return save(favorite);
        }
        return false;
    }

    // 删除收藏
    @Override
    public boolean delete(Long articleId, Long userId) {
        Favorite favorite = lambdaQuery()
                .eq(Favorite::getArticleId, articleId)
                .eq(Favorite::getUserId, userId)
                .one();
        if (favorite == null) return false;
        return removeById(favorite.getId());
    }

    // 获取收藏列表
    @Override
    public List<Long> listFavorite(Long userId) {
        return lambdaQuery()
                .eq(Favorite::getUserId, userId)
                .list()
                .stream()
                .map(Favorite::getArticleId)
                .toList();
    }

}




