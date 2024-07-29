package com.news.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserPwdDTO {
    @NotEmpty
    @Pattern(regexp = "^\\S{5,15}$", message = "原密码格式不正确")
    String oldPwd;
    @NotEmpty
    @Pattern(regexp = "^\\S{5,15}$", message = "新密码格式不正确")
    String newPwd;
    @NotEmpty
    @Pattern(regexp = "^\\S{5,15}$", message = "新密码格式不正确")
    String rePwd;
}
