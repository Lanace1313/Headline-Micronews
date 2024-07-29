package com.news.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.news.utils.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MyAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Result<String> result = Result.error("禁止访问");
        ObjectMapper objectMapper = new ObjectMapper();
        // 设置响应内容类型和编码
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        // 返回响应错误
        String json = objectMapper.writeValueAsString(result);
        response.setStatus(500);
        response.getWriter().print(json);
    }
}