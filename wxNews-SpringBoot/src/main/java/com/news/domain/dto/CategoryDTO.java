package com.news.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.groups.Default;
import lombok.Data;

@Data
public class CategoryDTO {
    @NotNull(groups = Update.class)//分组校验
    private Long id;
    @NotEmpty
    @Pattern(regexp = "^\\S{1,5}$", message = "分类名称格式有误")
    private String categoryName;

    public interface Add extends Default {
    }

    public interface Update extends Default {
    }
}
