package com.news.interceptor;

//未使用SpringSecurity时的认证拦截器
/*@Component
@RequiredArgsConstructor
public class LoginProtectInterceptor implements HandlerInterceptor {

    private final JwtHelper jwtHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader("Authorization");
        //判断token是否为空或过期失效
        if (token == null || token.isBlank() || jwtHelper.isExpiration(token)) {
            Result<String> result = Result.build(null, ResultCodeEnum.NOTLOGIN);
            ObjectMapper objectMapper = new ObjectMapper();
            // 设置响应内容类型和编码
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            // 返回响应错误
            String json = objectMapper.writeValueAsString(result);
            response.setStatus(401);
            response.getWriter().print(json);
            //拦截
            return false;
        } else {
            ThreadLocalUtil.set(jwtHelper.getUserId(token));
            //放行
            return true;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清除ThreadLocal中的数据
        ThreadLocalUtil.remove();
    }
}*/