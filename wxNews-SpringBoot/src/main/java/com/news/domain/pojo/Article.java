package com.news.domain.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @TableName article
 */
@EqualsAndHashCode(callSuper = false) //生成equals和hashCode
@Accessors(chain = true) //生成链式调用
@TableName(value = "article")
@Data
public class Article implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;//文章id
    private String title;//文章标题
    private String content;//文章内容
    private String coverImg;//文章封面
    private String state;//文章状态
    private Long categoryId;//分类id
    private Long userId;//用户id
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime publishTime;//发布时间
    private Long favoriteNum;//收藏数
}