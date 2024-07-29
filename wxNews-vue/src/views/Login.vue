<template>
    <el-row class="login-page">
        <el-col :span="12" class="bg"></el-col>
        <el-col :span="6" :offset="3" class="form">
            <!-- Logo区域 -->
            <div class="logo-container">
                <img src="@/assets/logo.png" alt="Logo" class="logo">
            </div>
            <!-- 注册表单 -->
            <el-form ref="form" size="large" autocomplete="off" :model="registerData" :rules="registerDataRules"
                v-if="isRegister">
                <el-form-item>
                    <h1>注册</h1>
                </el-form-item>
                <el-form-item prop="phone">
                    <el-input :prefix-icon="User" placeholder="请输入手机号" v-model="registerData.phone"></el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input :prefix-icon="Lock" type="password" placeholder="请输入密码"
                        v-model="registerData.password"></el-input>
                </el-form-item>
                <el-form-item prop="rePassword">
                    <el-input :prefix-icon="Lock" type="password" placeholder="请输入再次密码"
                        v-model="registerData.rePassword"></el-input>
                </el-form-item>
                <!-- 注册按钮 -->
                <el-form-item>
                    <el-button class="button" type="primary" auto-insert-space @click="register">
                        注册
                    </el-button>
                </el-form-item>
                <el-form-item class="flex">
                    <el-link type="info" :underline="false" @click="isRegister = false; clearRegisterData()">
                        ← 返回
                    </el-link>
                </el-form-item>
            </el-form>
            <!-- 登录表单 -->
            <el-form ref="form" size="large" autocomplete="off" :model="registerData" :rules="registerDataRules" v-else>
                <el-form-item>
                    <h1>登录</h1>
                </el-form-item>
                <el-form-item prop="phone">
                    <el-input :prefix-icon="User" placeholder="请输入手机号" v-model="registerData.phone"></el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input name="password" :prefix-icon="Lock" type="password" placeholder="请输入密码"
                        v-model="registerData.password"></el-input>
                </el-form-item>
                <el-form-item class="flex">
                    <div class="flex">
                        <el-checkbox>记住我</el-checkbox>
                        <el-link type="primary" :underline="false">忘记密码？</el-link>
                    </div>
                </el-form-item>
                <!-- 登录按钮 -->
                <el-form-item>
                    <el-button class="button" type="primary" auto-insert-space @click="login">登录</el-button>
                </el-form-item>
                <el-form-item class="flex">
                    <el-link type="info" :underline="false" @click="isRegister = true; clearRegisterData()">
                        注册 →
                    </el-link>
                </el-form-item>
            </el-form>
        </el-col>
    </el-row>
</template>

<script setup>
import { User, Lock } from '@element-plus/icons-vue'
import { ref } from 'vue'
//引入element-plus的提示组件
import { ElMessage } from 'element-plus'
//调用后台接口，完成注册
import { userLoginService, userRegisterService } from '@/api/user.js'
//控制注册与登录表单的显示， 默认显示登陆
const isRegister = ref(false)
//用于注册的数据模型
const registerData = ref({
    phone: '13111111111',
    password: '',
    rePassword: ''
})
//自定义确认密码的校验函数
const rePasswordValid = (rule, value, callback) => {
    if (value == null || value === '') {
        return callback(new Error('请再次确认密码'))
    } else if (value !== registerData.value.password) {
        return callback(new Error('两次输入密码不一致'))
    }
}
//用于注册的表单校验模型
const registerDataRules = ref({
    phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 5, max: 15, message: '密码长度必须为5~15位', trigger: 'blur' }
    ],
    rePassword: [
        { validator: rePasswordValid, trigger: 'blur' }
    ]
})

//用于注册的事件函数
const register = async () => {
    //console.log('注册...');
    let result = await userRegisterService(registerData.value);
    ElMessage.success(result.message || '注册成功')
    //跳转会登陆界面
    isRegister.value = false;
}

//用于登陆的事件函数
import { useRouter } from 'vue-router'
const router = useRouter()
//导入token状态
import { useTokenStore } from '@/stores/token.js'
//调用useTokenStore得到状态
const tokenStore = useTokenStore();

const login = async () => {
    //调用接口，完成登陆
    let result = await userLoginService(registerData.value);
    //保存token
    tokenStore.setToken(result.data)
    ElMessage.success(result.message || '登陆成功')
    router.push('/')
}
// 定义函数，清空数据模型的数据
const clearRegisterData = () => {
    registerData.value = {
        phone: '',
        password: '',
        rePassword: ''
    }
}
</script>

<style lang="scss" scoped>
/* 样式 */
.login-page {
    height: 100vh;
    background-color: #fff;

    .bg {
        background: //url('@/assets/logo.png') no-repeat 60% center / 240px auto,
            url('@/assets/login_bg.jpg') no-repeat center / cover;
        border-radius: 0 20px 20px 0;
    }

    .form {
        display: flex;
        flex-direction: column;
        justify-content: center;
        user-select: none;

        .title {
            margin: 0 auto;
        }

        .button {
            width: 100%;
        }

        .flex {
            width: 100%;
            display: flex;
            justify-content: space-between;
        }

        .logo-container {
            text-align: center; //水平居中
            margin-bottom: 10px;
        }

        .logo {
            width: 200px;
            height: auto;
        }
    }
}
</style>