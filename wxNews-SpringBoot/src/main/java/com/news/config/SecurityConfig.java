package com.news.config;

import com.news.exception.MyAccessDeniedHandler;
import com.news.exception.MyAuthenticationEntryPoint;
import com.news.filter.JwtAuthenticationTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    //自定义拦截器
    public final JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    //认证异常处理
    private final MyAuthenticationEntryPoint authenticationEntryPoint;
    //授权异常处理
    private final MyAccessDeniedHandler accessDeniedHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //基于token，所以不需要csrf防护
        http.csrf().disable()
                //基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                //登录接口放行
                .requestMatchers("/user/login", "/user/register", "/image/**").permitAll()
                .anyRequest().authenticated()
                .and()
                //禁用缓存
                .headers().cacheControl();
        //添加拦截器
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling()
                //认证异常
                .authenticationEntryPoint(authenticationEntryPoint)
                //授权异常
                .accessDeniedHandler(accessDeniedHandler);
        //允许跨域
        http.cors();
        return http.build();
    }

}
