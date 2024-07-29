package com.news.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.news.domain.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVo {
    private Long id;// 用户ID

    private String username;// 用户名

    private String phone;// 手机号

    private String role;// 角色

    private String email;// 邮箱

    private String avatar;// 头像

    private Long ArticleNum;// 文章数

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;// 创建时间

    public UserVo(User user) {
        if (user != null) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.phone = user.getPhone();
            this.role = user.getRole();
            this.email = user.getEmail();
            this.avatar = user.getAvatar();
            this.createTime = user.getCreateTime();
        }
    }
}
