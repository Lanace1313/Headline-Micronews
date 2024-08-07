<template>
    <!-- element-plus容器 -->
    <el-container class="layout-container">
        <!-- 左侧菜单 -->
        <el-aside width="200px">
            <div class="el-aside__logo"></div>
            <!-- element-plus菜单标签 -->
            <el-menu class="custom-menu" active-text-color="#c1efb6" background-color="#FFFFFF" text-color="#636466"
                router>
                <el-menu-item index="/manage/home">
                    <el-icon>
                        <HomeFilled />
                    </el-icon>
                    <span>系统首页</span>
                </el-menu-item>
                <el-menu-item index="/article/category" v-if="userInfoStore.info.role == 'ROOT'">
                    <el-icon>
                        <Management />
                    </el-icon>
                    <span>文章分类</span>
                </el-menu-item>
                <el-menu-item index="/article/manage">
                    <el-icon>
                        <Promotion />
                    </el-icon>
                    <span>文章管理</span>
                </el-menu-item>
                <el-menu-item index="/article/favorite">
                    <el-icon>
                        <StarFilled />
                    </el-icon>
                    <span>收藏管理</span>
                </el-menu-item>
                <el-menu-item index="/user/manage" v-if="userInfoStore.info.role == 'ROOT'">
                    <el-icon>
                        <UserFilled />
                    </el-icon>
                    <span>用户管理</span>
                </el-menu-item>
                <el-sub-menu index="1">
                    <template #title>
                        <el-icon>
                            <Avatar />
                        </el-icon>
                        <span>个人中心</span>
                    </template>
                    <el-menu-item index="/user/info">
                        <el-icon>
                            <User />
                        </el-icon>
                        <span>基本资料</span>
                    </el-menu-item>
                    <el-menu-item index="/user/password">
                        <el-icon>
                            <EditPen />
                        </el-icon>
                        <span>重置密码</span>
                    </el-menu-item>
                </el-sub-menu>
            </el-menu>
        </el-aside>
        <!-- 右侧主区域 -->
        <el-container>
            <!-- 头部区域 -->
            <el-header>
                <div>用户名：<strong>{{ userInfoStore.info.username }}</strong></div>
                <div>注册时间：<strong>{{ userInfoStore.info.createTime }}</strong></div>
                <el-dropdown placement="bottom-end" @command="handleCommand">
                    <span class="el-dropdown__box">
                        <el-avatar :src="userInfoStore.info.avatar || avatar" />
                        <el-icon>
                            <CaretBottom />
                        </el-icon>
                    </span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item command="info" :icon="User">基本资料</el-dropdown-item>
                            <el-dropdown-item command="password" :icon="EditPen">重置密码</el-dropdown-item>
                            <el-dropdown-item command="home" :icon="Discount">前台系统</el-dropdown-item>
                            <el-dropdown-item command="logout" :icon="SwitchButton">退出登录</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </el-header>
            <!-- 中间区域 -->
            <el-main>
                <!-- 内容展示区 -->
                <router-view></router-view>
            </el-main>
            <!-- 底部区域 -->
            <el-footer>头条微讯 ©2024 Created by Lanace</el-footer>
        </el-container>
    </el-container>
</template>

<script setup>
import {
    HomeFilled,
    Management,
    Promotion,
    Avatar,
    StarFilled,
    UserFilled,
    User,
    EditPen,
    Discount,
    SwitchButton,
    CaretBottom
} from '@element-plus/icons-vue'
import avatar from '@/assets/default.png'
import { ref } from 'vue'

//导入接口函数
import { userInfoGetService } from '@/api/user.js'
//导入pinia
import { useUserInfoStore } from '@/stores/user.js'
const userInfoStore = useUserInfoStore();

//dropDown条目被点击后，回调的函数
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useTokenStore } from '@/stores/token.js'
const router = useRouter()
const tokenStore = useTokenStore()

//获取个人信息
const getUserInf = async () => {
    let result = await userInfoGetService();
    //存储pinia
    userInfoStore.info = result.data;
}
getUserInf()

//dropDown条目被点击后，回调的函数
const handleCommand = (command) => {
    if (command === 'logout') {
        //退出登录
        ElMessageBox.confirm(
            '你确认退出登录吗？',
            '温馨提示',
            {
                confirmButtonText: '确认',
                cancelButtonText: '取消',
                type: 'warning',
            }
        )
            .then(async () => {
                //用户点击了确认
                //清空pinia中的token和个人信息
                userInfoStore.info = {}
                tokenStore.token = ''
                //跳转到登录页
                router.push('/login')
            })
            .catch(() => {
                //用户点击了取消
                ElMessage({
                    type: 'info',
                    message: '取消退出',
                })
            })
    } else if (command === 'home') {
        router.push('/')
    }
    else {
        //路由
        router.push('/user/' + command)
    }
}
</script>

<style lang="scss" scoped>
.layout-container {
    height: 100vh;

    .el-aside {
        background-color: #ffffff;

        &__logo {
            height: 120px;
            background: url('@/assets/logo.png') no-repeat center / 120px auto;
        }

        .el-menu {
            border-right: none;
        }

    }

    .custom-menu {
        font-weight: bold;

        .el-menu-item.is-active {
            /* 鼠标点击时的背景颜色 */
            background-color: #c4c6c8;
        }
    }

    .el-header {
        background-color: #ffffff;
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-left: 20px;

        .el-dropdown__box {
            display: flex;
            align-items: center;

            .el-icon {
                color: #999;
                margin-left: 10px;
            }

            &:active,
            &:focus {
                outline: none;
            }
        }
    }

    .el-footer {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 30px;
        font-size: 14px;
        color: #666;
    }
}
</style>