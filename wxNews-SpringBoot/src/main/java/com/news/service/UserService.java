package com.news.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.news.domain.dto.UserLoginDTO;
import com.news.domain.dto.UserPwdDTO;
import com.news.domain.dto.UserUpdateDTO;
import com.news.domain.pojo.User;
import com.news.domain.vo.UserVo;
import com.news.utils.Result;

import java.util.List;

public interface UserService extends IService<User> {

    //注册
    Result<String> register(UserLoginDTO registerUser);

    //登录
    Result<String> login(UserLoginDTO loginUser);

    //获取用户信息
    Result<UserVo> userInfo(Long userId);

    //修改密码
    Result<String> updatePwd(UserPwdDTO pwdDTO);

    //修改用户信息
    Result<String> updateUser(UserUpdateDTO updateUser);

    //获取所有用户信息
    Result<List<UserVo>> allUserInfo(String username, String role);

    //删除用户
    Result<String> delete(Long userId);

    //修改头像
    Result<String> updateAvatar(String avatar);

}
