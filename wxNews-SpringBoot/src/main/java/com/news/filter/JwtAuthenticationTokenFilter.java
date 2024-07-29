package com.news.filter;

import com.news.domain.pojo.User;
import com.news.service.UserService;
import com.news.utils.JwtHelper;
import com.news.utils.ThreadLocalUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.news.utils.RedisConstants.USER_TOKEN_KEY;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final StringRedisTemplate stringRedisTemplate;

    private final UserService userService;

    private final JwtHelper jwtHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");//获取token
        if (token == null || token.isBlank()) {
            filterChain.doFilter(request, response); //放行
        } else if (jwtHelper.isExpiration(token)) {
            log.info("Token异常!!");//令牌过期或异常
            filterChain.doFilter(request, response); //放行
        } else {
            //获取用户id
            Long userId = jwtHelper.getUserId(token);
            String tokenKey = USER_TOKEN_KEY + userId;
            //从redis中获取token
            String redisToken = stringRedisTemplate.opsForValue().get(tokenKey);
            //判断用户token信息
            if (!token.equals(redisToken)) {
                log.info("redisToken信息异常!!");//用户信息过期或异常
                filterChain.doFilter(request, response);
                return;
            }
            //获取用户信息
            User user = new User(userService.userInfo(userId).getData());
            //用户ID存入ThreadLocal
            ThreadLocalUtil.set(user.getId());
            //存入SecurityContextHolder
            UsernamePasswordAuthenticationToken authentication
                    = new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());
            //将认证过了凭证保存到security的上下文中以便于在程序中使用
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response); //放行
        }
    }

    //清除数据
    @Override
    public void destroy() {
        ThreadLocalUtil.remove();
    }
}
