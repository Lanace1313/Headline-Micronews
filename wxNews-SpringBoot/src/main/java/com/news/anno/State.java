package com.news.anno;

import com.news.validation.StateValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented//描述注解的注解
@Target({ElementType.FIELD})//作用在字段上
@Retention(RetentionPolicy.RUNTIME)//运行时生效
@Constraint(validatedBy = {StateValidation.class})//验证器
public @interface State {
    //提示信息
    String message() default "state的值只能是“已发布”或“草稿”";

    //分组
    Class<?>[] groups() default {};

    //负载
    Class<? extends Payload>[] payload() default {};
}
