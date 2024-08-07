<template>
    <!-- element-plus容器 -->
    <el-container class="layout-container">
        <!-- 头部区域 -->
        <el-header>
            <!-- Logo区域 -->
            <div class="logo-container">
                <img src="@/assets/logo.png" alt="Logo" class="logo">
            </div>
            <div class="right-content">
                <div style="margin-right: 20px;">用户：<strong>{{ userInfoStore.info.username }}</strong></div>
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
                            <el-dropdown-item command="manage" :icon="Operation">后台系统</el-dropdown-item>
                            <el-dropdown-item command="logout" :icon="SwitchButton">退出登录</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </el-header>
        <!-- 中间区域 -->
        <el-main>
            <!-- <div style="border: 1px solid red;"> -->
            <!-- 内容展示区 -->
            <ShowVue style="max-width: 1000px; margin: 0 auto; display: block;"></ShowVue>
            <!-- </div> -->
        </el-main>
        <!-- 底部区域 -->
        <el-footer>头条微讯 ©2024 Created by Lanace</el-footer>
    </el-container>
</template>

<script setup>
import {
    Management,
    Promotion,
    Avatar,
    StarFilled,
    UserFilled,
    User,
    EditPen,
    Operation,
    SwitchButton,
    CaretBottom
} from '@element-plus/icons-vue'
import avatar from '@/assets/default.png'

//导入接口函数
import { userInfoGetService } from '@/api/user.js'
//导入pinia
import { useUserInfoStore } from '@/stores/user.js'
const userInfoStore = useUserInfoStore()

//dropDown条目被点击后，回调的函数
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useTokenStore } from '@/stores/token.js'
const router = useRouter()
const tokenStore = useTokenStore()

import ShowVue from '@/components/ArticleListShow.vue'

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
    } else if (command === 'manage') {
        router.push('/manage/home')
    } else {
        //路由
        router.push('/user/' + command)
    }
}
</script>

<style lang="scss" scoped>
.layout-container {
    height: 100vh;
    background-color: #f7f8fc;
    background-image: url('@/assets/home_bg.jpg');
    background-size: contain; //将图像缩放成适合背景定位区域的最大大小

    .el-header {
        background-color: #ffffff;
        display: flex;
        align-items: center;
        justify-content: space-between;

        .logo-container {
            text-align: center;
            margin-bottom: 5px;
        }

        .logo {
            width: 40%;
            //height: auto;
        }

        .right-content {
            margin-left: auto;
            display: flex;
            align-items: center;
            margin-right: 10px
        }

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