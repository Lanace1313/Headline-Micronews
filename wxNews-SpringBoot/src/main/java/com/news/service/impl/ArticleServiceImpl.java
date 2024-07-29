package com.news.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.news.domain.dto.ArticleDTO;
import com.news.domain.dto.PageDTO.ArticlePageDTO;
import com.news.domain.pojo.Article;
import com.news.domain.pojo.Favorite;
import com.news.domain.vo.PageBean;
import com.news.mapper.ArticleMapper;
import com.news.mapper.UserMapper;
import com.news.service.ArticleService;
import com.news.service.CategoryService;
import com.news.service.FavoriteService;
import com.news.utils.Result;
import com.news.utils.ThreadLocalUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
        implements ArticleService {

    private final ArticleMapper articleMapper;
    private final CategoryService categoryService;
    private final FavoriteService favoriteService;
    private final UserMapper userMapper;

    //添加文章
    @Override
    public Result<String> add(ArticleDTO articleDTO) {
        // 先检查类别ID是否存在
        if (!categoryService.categoryByID(articleDTO.getCategoryId()))
            return Result.error("分类ID不存在");
        // 封装数据
        Article article = new Article();
        article.setContent(articleDTO.getContent())
                .setCoverImg(articleDTO.getCoverImg())
                .setCategoryId(articleDTO.getCategoryId())
                .setUserId(ThreadLocalUtil.get())
                .setTitle(articleDTO.getTitle())
                .setState(articleDTO.getState())
                .setPublishTime(LocalDateTime.now());
        //保存,使分类文章数量+1
        if (save(article) && categoryService.ArticlesNumAdd(article.getCategoryId()))
            return Result.ok(null);
        return Result.error();
    }

    //分页查询
    @Override
    public PageBean<Article> listPage(ArticlePageDTO articlePageDTO) {
        Long userId = ThreadLocalUtil.get();
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //判断是否为管理员或者是否为查询首页列表
        queryWrapper.eq(!Objects.equals(userMapper.selectById(userId).getRole(), "ROOT")
                        && articlePageDTO.getUse() == 0, Article::getUserId, userId)
                .eq(articlePageDTO.getCategoryId() != null, Article::getCategoryId, articlePageDTO.getCategoryId())
                .eq(articlePageDTO.getState() != null, Article::getState, articlePageDTO.getState())
                .like(articlePageDTO.getTitle() != null, Article::getTitle, articlePageDTO.getTitle())
                .orderByDesc(Article::getId);
        //分页
        IPage<Article> page = new Page<>();
        page.setCurrent(articlePageDTO.getPageNum()).setSize(articlePageDTO.getPageSize());
        //查询返回结果
        List<Article> articles = articleMapper.selectList(page, queryWrapper);
        return new PageBean<>(page.getTotal(), articles);
    }

    //更新文章
    @Override
    @Transactional//事务
    public Result<String> updateArticle(ArticleDTO articleDTO) {
        Article article = lambdaQuery().eq(Article::getId, articleDTO.getId()).one();
        if (article == null) {
            return Result.error("文章记录不存在");
        }
        //判断新分类是否存在
        if (categoryService.categoryByID(articleDTO.getCategoryId())) {
            //获取原分类ID
            Long categoryId = article.getCategoryId();
            //判断是否更改了分类
            if (!Objects.equals(categoryId, articleDTO.getCategoryId())
            ) {
                //更新分类数目
                categoryService.ArticlesNumAdd(articleDTO.getCategoryId());
                categoryService.ArticlesNumReduce(categoryId);
            }
            //更新文章
            lambdaUpdate().eq(Article::getId, articleDTO.getId())
                    .set(Article::getTitle, articleDTO.getTitle())
                    .set(Article::getContent, articleDTO.getContent())
                    .set(Article::getCoverImg, articleDTO.getCoverImg())
                    .set(Article::getState, articleDTO.getState())
                    .set(Article::getCategoryId, articleDTO.getCategoryId())
                    .set(Article::getPublishTime, LocalDateTime.now())
                    .update();
            return Result.ok(null);
        }
        return Result.error("分类异常");
    }

    //删除文章
    @Override
    public Result<String> delete(Long id) {
        //判断文章是否存在
        Article article = getById(id);
        if (article == null) return Result.error("文章不存在");
        //判断用户是否是管理员或发布者
        if (userMapper.selectById(ThreadLocalUtil.get()).getRole().equals("ROOT")
                || article.getUserId().equals(ThreadLocalUtil.get())) {
            //删除该文章全部收藏,分类数目-1，
            if (article.getFavoriteNum() != 0)
                favoriteService.remove(new LambdaQueryWrapper<Favorite>().eq(Favorite::getArticleId, id));
            if (categoryService.ArticlesNumReduce(article.getCategoryId())
                    && removeById(id)) return Result.ok(null);
            return Result.error();
        }
        return Result.error("权限不足");
    }

    //删除用户所有文章
    @Override
    public boolean deleteAllByUserId(Long userId) {
        List<Article> list = lambdaQuery().eq(Article::getUserId, userId).list();
        if (list.isEmpty()) return true;
        //遍历删除文章
        for (Article article : list) {
            if (delete(article.getId()).getCode() != 200) return false;
        }
        return true;
    }

    //添加收藏
    @Override
    public Result<String> addFavorite(Long articleId) {
        if (getById(articleId) == null) return Result.error("文章不存在");
        else if (favoriteService.add(articleId)) {
            //更新收藏数
            lambdaUpdate().eq(Article::getId, articleId)
                    .set(Article::getFavoriteNum, getById(articleId).getFavoriteNum() + 1).update();
            return Result.ok("收藏成功");
        }
        return Result.error("已添加收藏");
    }

    //取消收藏
    @Override
    public boolean deleteFavorite(Long articleId, Long userId) {
        if (favoriteService.delete(articleId, userId)) {
            //更新收藏数
            lambdaUpdate().eq(Article::getId, articleId)
                    .set(Article::getFavoriteNum, getById(articleId).getFavoriteNum() - 1).update();
            return true;
        }
        return false;
    }

    //获取收藏列表
    @Override
    public List<Article> listFavorite(Long userId) {
        List<Long> articleIds = favoriteService.listFavorite(userId);
        if (!articleIds.isEmpty()) return listByIds(articleIds);
        return null;
    }

    //删除用户所有收藏
    @Override
    public boolean deleteAllFavoriteByUserId(Long userId) {
        //获取收藏列表
        List<Long> articleIds = favoriteService.listFavorite(userId);
        if (articleIds.isEmpty()) return true;
        //遍历删除收藏
        for (Long id : articleIds) {
            if (!deleteFavorite(id, userId)) return false;
        }
        return true;
    }
}




