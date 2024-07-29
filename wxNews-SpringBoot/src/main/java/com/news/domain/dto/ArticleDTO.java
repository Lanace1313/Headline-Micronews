package com.news.domain.dto;

import com.news.anno.State;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.groups.Default;
import lombok.Data;

@Data
public class ArticleDTO {
    @NotNull(groups = CategoryDTO.Update.class)//分组校验
    private Long id;// 文章ID
    @NotEmpty
    @Pattern(regexp = "^\\S{1,30}$", message = "文章标题格式有误")
    private String title;// 文章标题
    @NotEmpty
    private String content;// 文章内容
    //@URL  //本地存储不使用url
    private String coverImg;// 文章封面
    @State
    private String state;// 文章状态: 只能是[已发布] 或者 [草稿]
    @NotNull
    private Long categoryId;// 分类ID

    public interface Add extends Default {
    }

    public interface Update extends Default {
    }
}
