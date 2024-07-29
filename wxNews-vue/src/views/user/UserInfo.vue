<template>
    <el-card class="page-container">
        <template #header>
            <div class="header">
                <span>基本资料</span>
            </div>
        </template>
        <el-row :gutter="100">
            <el-col :span="12">
                <el-form :model="userInfo" :rules="rules" label-width="100px" size="large">
                    <el-form-item label="用户名" prop="username">
                        <el-input v-model="userInfo.username"></el-input>
                    </el-form-item>
                    <el-form-item label="手机号" prop="phone">
                        <el-input v-model="userInfo.phone"></el-input>
                    </el-form-item>
                    <el-form-item label="用户邮箱" prop="email">
                        <el-input v-model="userInfo.email"></el-input>
                    </el-form-item>
                    <el-form-item label="用户身份">
                        <el-input v-model="userInfo.role" disabled></el-input>
                    </el-form-item>
                    <el-form-item label="创建时间">
                        <el-input v-model="userInfo.createTime" disabled></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="updateUserInfo">提交修改</el-button>
                    </el-form-item>
                </el-form>
            </el-col>

            <el-col :span="12">
                <el-upload ref="uploadRef" class="avatar-uploader" :show-file-list="false" :auto-upload="true"
                    action="/api/upload" name="file" :headers="{ 'Authorization': tokenStore.token }"
                    :on-success="uploadSuccess">
                    <img v-if="imgUrl" :src="imgUrl" class="avatar" />
                    <img v-else src="@/assets/avatar.jpg" width="278" />
                </el-upload>
                <br />
                <el-button type="primary" :icon="Plus" size="large"
                    @click="uploadRef.$el.querySelector('input').click()" plain>
                    选择图片
                </el-button>
                <el-button type="success" :icon="Upload" size="large" @click="updateAvatar" plain>
                    上传头像
                </el-button>
            </el-col>
        </el-row>
    </el-card>
</template>

<script setup>
import { ref } from 'vue'
//获取用户信息
import { useUserInfoStore } from '@/stores/user.js';
const userInfoStore = useUserInfoStore()
const userInfo = ref({ ...userInfoStore.info })

import { userInfoUpdateService, userAvatarUpdateService } from '@/api/user.js'
import { ElMessage } from 'element-plus';

import { Plus, Upload } from '@element-plus/icons-vue'
import avatar from '@/assets/default.png'

//读取token信息
import { useTokenStore } from '@/stores/token.js'

//修改用户信息
const updateUserInfo = async () => {
    let result = await userInfoUpdateService(userInfo.value)
    ElMessage.success(result.message || '修改成功')
    //更新pinia中的数据
    userInfoStore.info.username = userInfo.value.username
    userInfoStore.info.phone = userInfo.value.phone
    userInfoStore.info.email = userInfo.value.email
}

//校验规则
const rules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        {
            pattern: /^\S{1,10}$/,
            message: '昵称必须是1-10位的非空字符串',
            trigger: 'blur'
        }
    ],
    phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        {
            pattern: /^1[34578]\d{9}$/,
            message: '手机号格式不正确',
            trigger: 'blur'
        }
    ],
    email: [
        { required: true, message: '请输入用户邮箱', trigger: 'blur' },
        { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
    ]
}

const tokenStore = useTokenStore()
//用户头像地址
const imgUrl = ref(userInfoStore.info.avatar)
//获取el-upload元素
const uploadRef = ref()

//修改头像
const updateAvatar = async () => {
    let result = await userAvatarUpdateService(imgUrl.value)
    ElMessage.success(result.message || '修改成功')
    //更新pinia中的数据
    userInfoStore.info.avatar = imgUrl.value
}

//图片上传成功的回调
const uploadSuccess = (result) => {
    //回显图片
    imgUrl.value = result.data
}
</script>

<style lang="scss" scoped>
.page-container {
    min-height: 100%;
    box-sizing: border-box;

    .header {
        display: flex;
        align-items: center;
        justify-content: space-between;
    }
}

.avatar-uploader {
    :deep() {
        .avatar {
            width: 278px;
            height: 278px;
            display: block;
        }

        .el-upload {
            border: 1px dashed var(--el-border-color);
            border-radius: 6px;
            cursor: pointer;
            position: relative;
            overflow: hidden;
            transition: var(--el-transition-duration-fast);
        }

        .el-upload:hover {
            border-color: var(--el-color-primary);
        }

        .el-icon.avatar-uploader-icon {
            font-size: 28px;
            color: #8c939d;
            width: 278px;
            height: 278px;
            text-align: center;
        }
    }
}
</style>