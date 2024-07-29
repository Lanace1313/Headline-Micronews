package com.news.controller;

import com.news.domain.dto.UserLoginDTO;
import com.news.domain.dto.UserPwdDTO;
import com.news.domain.dto.UserUpdateDTO;
import com.news.domain.vo.UserVo;
import com.news.service.UserService;
import com.news.utils.Result;
import com.news.utils.ThreadLocalUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService UserService;

    // 用户注册
    @PostMapping("/register")
    public Result<String> register(@Validated UserLoginDTO registerUser) {
        return UserService.register(registerUser);
    }

    //用户登陆
    @PostMapping("/login")
    public Result<String> login(@Validated UserLoginDTO loginUser) {
        return UserService.login(loginUser);
    }

    //修改用户信息
    @PutMapping("/update")
    public Result<String> update(@RequestBody @Validated UserUpdateDTO updateUser) {
        //判断用户id是否正确
        if (ThreadLocalUtil.get().equals(updateUser.getId()))
            return UserService.updateUser(updateUser);
        //用户参数异常
        return Result.error("用户异常");
    }

    //获取所有用户信息
    @PreAuthorize("hasAnyAuthority('ROOT')")
    @GetMapping("/all")
    public Result<List<UserVo>> all(String username, String role) {
        return UserService.allUserInfo(username, role);
    }

    //删除用户
    @PreAuthorize("hasAnyAuthority('ROOT')")
    @DeleteMapping("/delete")
    public Result<String> delete(Long userId) {
        return UserService.delete(userId);
    }


    //获取当前用户信息
    @GetMapping("/userInfo")
    public Result<UserVo> userInfo() {
        return UserService.userInfo(ThreadLocalUtil.get());
    }

    //修改密码
    @PatchMapping("/updatePwd")
    public Result<String> updatePwd(@RequestBody @Validated UserPwdDTO pwdDTO) {
        return UserService.updatePwd(pwdDTO);
    }

    //修改用户头像
    @PatchMapping("/updateAvatar")
    public Result<String> updateAvatar(String avatarUrl) {
        return UserService.updateAvatar(avatarUrl);
    }
}
