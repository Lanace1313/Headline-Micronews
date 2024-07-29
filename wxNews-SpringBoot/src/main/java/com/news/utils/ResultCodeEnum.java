package com.news.utils;

import lombok.Getter;

/**
 * 统一返回结果状态信息类
 */

@Getter
public enum ResultCodeEnum {

    SUCCESS(200, "操作成功"),
    BUSINESS_ERROR(400, "操作失败"),
    NOTLOGIN(401, "用户未登陆"),
    USERNAME_ERROR(402, "用户无效"),
    PASSWORD_ERROR(403, "密码错误"),
    USERNAME_USED(404, "用户已被注册"),
    PARAMETER_INCORRECT(501, "参数不合法");


    private final Integer code;
    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
