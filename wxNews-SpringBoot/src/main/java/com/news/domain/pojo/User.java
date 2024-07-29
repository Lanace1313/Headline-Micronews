package com.news.domain.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.news.domain.vo.UserVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;


/**
 * @TableName user
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "user")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User implements Serializable, UserDetails {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;// 用户id
    private String username;// 用户名
    private String password;// 密码
    private String phone;// 手机号
    private String role;// 角色
    private String email;// 邮箱
    private String avatar;// 头像
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    public User(UserVo userVo) {
        if (userVo != null) {
            this.id = userVo.getId();
            this.username = userVo.getUsername();
            this.phone = userVo.getPhone();
            this.role = userVo.getRole();
            this.email = userVo.getEmail();
            this.avatar = userVo.getAvatar();
            this.createTime = userVo.getCreateTime();
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        GrantedAuthority authority = new SimpleGrantedAuthority(role);
        return Collections.singleton(authority);
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}