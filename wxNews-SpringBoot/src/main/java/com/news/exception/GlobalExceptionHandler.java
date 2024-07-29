package com.news.exception;

import com.news.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理器方法专门处理验证失败异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.info("参数验证异常：" + ex.getMessage());
        // 获取第一个错误信息
        FieldError fieldError = ex.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "参数验证失败";
        return Result.error(errorMessage);
    }

    //处理Security权限不足异常
    @ExceptionHandler(AccessDeniedException.class)
    public void accessDeniedException(AccessDeniedException ex) throws AccessDeniedException {
        log.info("权限异常：" + ex.getMessage()); // 打印错误信息
        throw ex;
    }

    // 保留对其他所有异常的处理
    @ExceptionHandler(Exception.class)
    public Result<String> error(Exception e) {
        log.info("全局异常：" + e.getMessage()); // 打印错误信息
        return Result.error(StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "未知错误");
    }
}