<script setup>
import {
    Edit,
    Delete
} from '@element-plus/icons-vue'
import { ref } from 'vue'

//引入element-plus的提示组件
import { ElMessage, ElMessageBox } from 'element-plus'
//引入element-plus的图标
import { Plus } from '@element-plus/icons-vue'
//引入接口
import { userListService, userDeleteService } from '@/api/user.js'

//用户搜索时选择的用户名
const username = ref('')

//搜索选中的身份状态
const role = ref('')

//用户列表数据模型
const userList = ref([
    {
        "id": 1,
        "username": "测试",
        "phone": "11111111111",
        "role": "USER",
        "email": "Test@163.com",
        "articleNum": "3",
        "avatar": "http://sfxgu7vn0.hn-bkt.clouddn.com/myimage/c073604b-3447-45c4-a9dc-2e9e400a29ff.jpg",
        "createTime": "2024-07-03 12:27:31"
    }
])

//用户列表查询
const getUserList = async () => {
    let params = {
        username: username.value ? username.value : null,
        role: role.value || null
    }
    let result = await userListService(params);
    //渲染列表数据
    userList.value = result.data
}
getUserList()

//删除用户数据
const deleteUser = (row) => {
    ElMessageBox.confirm(
        '你确认删除该用户信息吗？',
        '温馨提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'error',
        }
    )
        .then(async () => {
            //调用接口
            let result = await userDeleteService(row.id)
            //点击确认
            ElMessage({
                type: 'success',
                message: '删除成功',
            })
            //刷新列表
            getUserList();
        })
        .catch(() => {
            //点击取消
            ElMessage({
                type: 'info',
                message: '取消删除',
            })
        })
}

</script>

<template>
    <el-card class="page-container">
        <template #header>
            <div class="header">
                <span>用户管理</span>
            </div>
        </template>
        <!-- 搜索表单 -->
        <el-form inline>
            <el-form-item label="用户名">
                <el-input placeholder="请输入用户名" v-model="username"></el-input>
            </el-form-item>
            <el-form-item label="用户身份：">
                <el-select placeholder="请选择" v-model="role">
                    <el-option label="管理员" value="ROOT"></el-option>
                    <el-option label="普通用户" value="USER"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="getUserList">搜索</el-button>
                <el-button @click="username = ''; role = ''; getUserList()">重置</el-button>
            </el-form-item>
        </el-form>
        <!-- 用户列表 -->
        <el-table :data="userList" style="width: 100%">
            <el-table-column label="序号" width="100" type="index"> </el-table-column>
            <el-table-column label="用户名" width="120" prop="username"></el-table-column>
            <el-table-column label="手机号" prop="phone"> </el-table-column>
            <el-table-column label="邮箱" prop="email"> </el-table-column>
            <el-table-column label="发表文章数" prop="articleNum"></el-table-column>
            <el-table-column label="身份" prop="role"></el-table-column>
            <el-table-column label="操作" width="100">
                <!-- eslint-disable-next-line -->
                <template #default="{ row }">
                    <el-button v-if="row.role !== 'ROOT'" :icon="Delete" circle plain type="danger"
                        @click=" deleteUser(row)"></el-button>
                </template>
            </el-table-column>
            <template #empty>
                <el-empty description="没有数据" />
            </template>
        </el-table>
    </el-card>
</template>

<style lang="scss" scoped>
.el-select {
    width: 150px;
}


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