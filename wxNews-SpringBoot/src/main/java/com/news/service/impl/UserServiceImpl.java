package com.news.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.news.domain.dto.UserLoginDTO;
import com.news.domain.dto.UserPwdDTO;
import com.news.domain.dto.UserUpdateDTO;
import com.news.domain.pojo.Article;
import com.news.domain.pojo.User;
import com.news.domain.vo.UserVo;
import com.news.mapper.UserMapper;
import com.news.service.ArticleService;
import com.news.service.UserService;
import com.news.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.news.utils.RedisConstants.*;


@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    private final JwtHelper jwtHelper;

    private final ArticleService articleService;

    private final StringRedisTemplate stringRedisTemplate;

    //注册用户
    @Override
    public Result<String> register(UserLoginDTO registerUser) {
        //查询用户是否被占用
        if (UserJudgment(registerUser.getPhone()) != null)
            //用户被占用
            return Result.build(null, ResultCodeEnum.USERNAME_USED);
        //加密密码
        User user = new User();
        user.setPhone(registerUser.getPhone())
                .setUsername(registerUser.getPhone())
                .setPassword(MD5Util.encrypt(registerUser.getPassword()))
                .setCreateTime(LocalDateTime.now());
        save(user);
        return Result.ok(null);
    }

    //登录
    @Override
    public Result<String> login(UserLoginDTO loginUser) {
        User user = UserJudgment(loginUser.getPhone());
        if (user == null) {
            //手机号错误
            return Result.build(null, ResultCodeEnum.USERNAME_ERROR);
        } else if (!user.getPassword().equals(MD5Util.encrypt(loginUser.getPassword()))) {
            //密码错误
            return Result.build(null, ResultCodeEnum.PASSWORD_ERROR);
        } else {
            String tokenKey = USER_TOKEN_KEY + user.getId().toString();
            //生成token
            String token = jwtHelper.createToken(user.getId());
            //将token存入redis
            stringRedisTemplate.opsForValue().set(tokenKey, token, USER_TOKEN_TTL, TimeUnit.HOURS);
            //返回token
            return Result.ok(token);
        }
    }

    //获取用户信息
    @Override
    public Result<UserVo> userInfo(Long userId) {
        UserVo userVo;
        String userKey = USER_INFO_KEY + userId;
        //从redis中获取用户信息
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(userKey);
        //redis中不存在用户,添加用户信息
        if (userMap.isEmpty()) {
            //查询数据
            User user = getById(userId);
            if (user == null) return Result.build(null, ResultCodeEnum.USERNAME_ERROR);
            //封装数据
            userVo = new UserVo(user);
            //将用户数据全部转换成String类型
            Map<String, Object> newUserMap = BeanUtil.beanToMap(userVo, new HashMap<>(),
                    CopyOptions.create()
                            .setIgnoreNullValue(true)
                            .setFieldValueEditor((fieldName, fieldValue) -> fieldValue == null ? null : fieldValue.toString())
            );
            //将用户信息存入redis
            stringRedisTemplate.opsForHash().putAll(userKey, newUserMap);
        } else userVo = BeanUtil.fillBeanWithMap(userMap, new UserVo(), false);
        //更新过期时间
        stringRedisTemplate.expire(userKey, USER_INFO_TTL, TimeUnit.HOURS);
        return Result.ok(userVo);
    }

    //更新用户密码
    @Override
    public Result<String> updatePwd(UserPwdDTO pwdDTO) {
        //获取用户id
        Long userId = ThreadLocalUtil.get();
        //判断用户密码正确，用户是否存在
        if (lambdaQuery().eq(User::getPassword, MD5Util.encrypt(pwdDTO.getOldPwd()))
                .eq(User::getId, userId).count() > 0) {
            //判断新密码和确认密码是否一致
            if (pwdDTO.getRePwd().equals(pwdDTO.getNewPwd())) {
                //更新数据
                boolean update = lambdaUpdate().eq(User::getId, userId)
                        .set(User::getPassword, MD5Util.encrypt(pwdDTO.getNewPwd()))
                        .update();
                stringRedisTemplate.delete(USER_TOKEN_KEY + userId.toString());
                //判断更新是否成功
                if (update) return Result.ok(null);
                return Result.error();
            }
            return Result.error("确认密码不一致");
        }
        return Result.build(null, ResultCodeEnum.PASSWORD_ERROR);
    }

    //更新用户信息
    @Override
    public Result<String> updateUser(UserUpdateDTO updateUser) {
        //查询用户是否被占用
        User user = UserJudgment(updateUser.getPhone());
        if (user == null || user.getPhone().equals(getById(updateUser.getId()).getPhone())) {
            //设置修改条件
            boolean updatePwd = lambdaUpdate().eq(User::getId, updateUser.getId())
                    .set(User::getUsername, updateUser.getUsername())
                    .set(User::getPhone, updateUser.getPhone())
                    .set(User::getEmail, updateUser.getEmail())
                    .update();
            //判断更新是否成功
            if (!updatePwd) return Result.build(null, ResultCodeEnum.PARAMETER_INCORRECT);
            //删除redis中的用户信息
            stringRedisTemplate.delete(USER_INFO_KEY + updateUser.getId().toString());
            return Result.ok(null);
        }
        //用户被占用
        return Result.build(null, ResultCodeEnum.USERNAME_USED);
    }

    //获取所有用户信息
    @Override
    public Result<List<UserVo>> allUserInfo(String username, String role) {
        List<User> userList = lambdaQuery()
                .like(username != null, User::getUsername, username)
                .eq(role != null, User::getRole, role)
                .list();
        //封装数据
        List<UserVo> userVoList = new ArrayList<>(userList.size());
        for (User user : userList) {
            //查询用户文章数量
            Long ArticleNum = articleService.count(new LambdaQueryWrapper<Article>()
                    .eq(Article::getUserId, user.getId()));
            UserVo userVo = new UserVo(user);
            userVoList.add(userVo);
            userVo.setArticleNum(ArticleNum);
        }
        return Result.ok(userVoList);
    }


    //删除用户
    @Override
    public Result<String> delete(Long userId) {
        //判断删除用户是否存在
        if (lambdaQuery().eq(User::getId, userId).count() > 0) {
            //删除用户所有文章/收藏
            if (articleService.deleteAllByUserId(userId) &&
                    articleService.deleteAllFavoriteByUserId(userId)) {
                //删除用户
                removeById(userId);
                //删除redis中的用户信息
                stringRedisTemplate.delete(USER_INFO_KEY + userId.toString());
                stringRedisTemplate.delete(USER_TOKEN_KEY + userId);
                return Result.ok(null);
            }
            return Result.error();
        }
        return Result.error("用户不存在");
    }

    //更新用户头像
    @Override
    public Result<String> updateAvatar(String avatar) {
        if (avatar == null || avatar.isBlank()) return Result.error("头像为空");
        else if (lambdaUpdate().eq(User::getId, ThreadLocalUtil.get())
                .set(User::getAvatar, avatar).update()) {
            //删除redis中的用户信息
            stringRedisTemplate.delete(USER_INFO_KEY + ThreadLocalUtil.get().toString());
            return Result.ok(null);
        }
        return Result.error();
    }

    //判断用户是否存在
    private User UserJudgment(String userPhone) {
        return lambdaQuery().eq(User::getPhone, userPhone).one();
    }
}




