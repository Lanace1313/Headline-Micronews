package com.news.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserUpdateDTO {
    @NotNull
    private Long id;
    @NotEmpty
    @Pattern(regexp = "^\\S{1,10}$", message = "用户名格式不正确")
    private String username;
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    @NotEmpty
    @Email
    private String email;
    //@URL
    //private String avatarUrl;
}
