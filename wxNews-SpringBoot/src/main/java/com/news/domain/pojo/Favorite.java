package com.news.domain.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @TableName favorite
 */
@EqualsAndHashCode(callSuper = false) //生成equals和hashCode
@Accessors(chain = true) //生成链式调用
@TableName(value = "favorite")
@Data
public class Favorite implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long articleId;
}