package com.news.domain.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserLoginDTO {
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Pattern(regexp = "^\\S{5,15}$", message = "密码格式不正确")
    private String password;
}