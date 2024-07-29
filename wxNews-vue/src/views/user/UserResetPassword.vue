<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus';
//修改用户信息
import { userPwdUpdateService } from '@/api/user'

const updatePwd = ref({
    oldPwd: '',
    newPwd: '',
    rePwd: ''
})

const updateUserPwd = async () => {
    let result = await userPwdUpdateService(updatePwd.value)
    ElMessage.success(result.message || '修改成功')
    // 重置表单
    updatePwd.value = {
        oldPwd: '',
        newPwd: '',
        rePwd: ''
    }

}

const rules = {
    oldPwd: [
        { required: true, message: '请输入原密码', trigger: 'blur' },
        {
            pattern: /^\S{5,15}$/,
            message: '密码必须是5-15位的非空字符串',
            trigger: 'blur'
        }
    ],
    newPwd: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        {
            pattern: /^\S{5,15}$/,
            message: '密码必须是5-15位的非空字符串',
            trigger: 'blur'
        }
    ],
    rePwd: [
    { required: true, message: '请确认密码', trigger: 'blur' },
        {
            pattern: /^\S{5,15}$/,
            message: '密码必须是5-15位的非空字符串',
            trigger: 'blur'
        }
    ]
}
</script>

<template>
    <el-card class="page-container">
        <template #header>
            <div class="header">
                <span>更改密码</span>
            </div>
        </template>
        <el-row>
            <el-col :span="12">
                <el-form :model="updatePwd" :rules="rules" label-width="100px" size="large">
                    <el-form-item label="原密码" prop="oldPwd">
                        <el-input v-model="updatePwd.oldPwd" type="password"></el-input>
                    </el-form-item>
                    <el-form-item label="新密码" prop="newPwd">
                        <el-input v-model="updatePwd.newPwd" type="password"></el-input>
                    </el-form-item>
                    <el-form-item label="确认密码" prop="rePwd">
                        <el-input v-model="updatePwd.rePwd" type="password"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="updateUserPwd">提交修改</el-button>
                    </el-form-item>
                </el-form>
            </el-col>
        </el-row>
    </el-card>
</template>

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
</style>