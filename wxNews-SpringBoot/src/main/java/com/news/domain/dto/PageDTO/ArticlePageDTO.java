package com.news.domain.dto.PageDTO;

import lombok.Data;

@Data
public class ArticlePageDTO {
    private Integer pageNum;   //页码
    private Integer pageSize;  //内容数量
    private String state;  //状态
    private Long categoryId;   //分类
    private String title; //搜索标题
    private Integer use;

    public ArticlePageDTO() {
        this.pageNum = 1;
        this.pageSize = 5;
        this.state = null;
        this.categoryId = null;
        this.title = null;
        this.use = 0;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = (pageNum != null) ? pageNum : 1;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = (pageSize != null) ? pageSize : 5;
    }

    public void setState(String state) {
        this.state = state.isEmpty() ? null : state;
    }

    public void setUse(Integer use) {
        this.use = (use != null) ? use : 0;
    }

}