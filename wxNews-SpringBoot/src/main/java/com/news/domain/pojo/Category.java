package com.news.domain.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @TableName category
 */

@EqualsAndHashCode(callSuper = false) //生成equals和hashCode
@Accessors(chain = true) //生成链式调用
@TableName(value = "category")
@Data
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;//分类id
    private String categoryName;//分类名称
    private Long articlesNum;//文章数量
}