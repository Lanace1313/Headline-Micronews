<template>
    <el-card class="page-container">
        <template #header>
            <div class="header">
                <span>收藏管理</span>
                <div class="extra">
                    <el-button type="danger" @click="deleteAllFavorite()">清空收藏</el-button>
                </div>
            </div>
        </template>
        <!-- 收藏列表 -->
        <el-table :data="favorites" style="width: 100%">
            <el-table-column label="文章标题" width="500" prop="title"></el-table-column>
            <el-table-column label="分类" width="150" prop="categoryName"></el-table-column>
            <el-table-column label="发表时间" prop="publishTime"> </el-table-column>
            <el-table-column label="操作" width="100">
                <!-- eslint-disable-next-line -->
                <template #default="{ row }">
                    <el-button :icon="View" circle plain type="success" @click="seeDetails(row)"></el-button>
                    <el-button :icon="Delete" circle plain type="danger" @click="deleteFavorite(row)"></el-button>
                </template>
            </el-table-column>
            <template #empty>
                <el-empty description="没有数据" />
            </template>
        </el-table>
    </el-card>

    <!-- 详细展示 -->
    <el-dialog v-model="visible" :show-close="true" width="70%" center: model="favoritesModel">
        <el-card shadow="never">
            <template #header>
                <div class="title-container">
                    <strong style="font-size: 28px;">{{ favoritesModel.title }}</strong>
                </div>
            </template>
            <el-text size="large" style="white-space: pre-wrap;">
                {{ favoritesModel.content }}
            </el-text><br>
            <el-text type="info">发布时间：{{ favoritesModel.publishTime }}</el-text>
        </el-card>
    </el-dialog>
</template>

<script setup>
//引入element-plus的图标
import {
    Delete,
    Plus,
    CircleCloseFilled,
    View
} from '@element-plus/icons-vue'
import { ref , markRaw} from 'vue'
//引入element-plus的提示组件
import { ElMessage, ElMessageBox } from 'element-plus'
//引入接口
import { articleCategoryListService, favoriteListService, favoriteDeleteService, favoriteDeleteAllService } from '@/api/article.js';

//文章分类数据模型
const categorys = ref([
    {
        "id": 1,
        "categoryName": "测试",
        "publishTime": "2023-09-02 12:06:59"
    }
])

//文章收藏列表数据模型
const favorites = ref([
    {
        "id": 1,
        "title": "测试",
        "userId": "1",
        "content": "无数据",
        "coverImg": "https://big-event-gwd.oss-cn-beijing.aliyuncs.com/9bf1cf5b-1420-4c1b-91ad-e0f4631cbed4.png",
        "state": "已发布",
        "categoryId": 1,
        "publishTime": "2023-09-03 11:55:30",
    }
])

//控制详细页面展示
const visible = ref(false)

//文章详细数据模型
const favoritesModel = ref({
    "title": "测试标题",
    "content": "测试内容",
    "categoryId": 1,
    "userId": 1,
    "publishTime": "2024-00-00 23:59:59",
    "favoriteNum": 0
})

//详细信息显示
const seeDetails = async (row) => {
    visible.value = true;
    favoritesModel.value = row
}

//分类列表查询
const getArticleCategoryList = async () => {
    //获取所有分类
    let resultC = await articleCategoryListService();
    categorys.value = resultC.data
}

//文章收藏列表查询
const getFavorite = async () => {
    let result = await favoriteListService()
    //渲染列表数据
    favorites.value = result.data
    //为列表中添加categoryName属性
    for (let i = 0; i < favorites.value.length; i++) {
        let article = favorites.value[i];
        for (let j = 0; j < categorys.value.length; j++) {
            if (article.categoryId === categorys.value[j].id) {
                article.categoryName = categorys.value[j].categoryName
            }
        }
    }
}

getArticleCategoryList();
getFavorite();

//删除收藏数据
const deleteFavorite = (row) => {
    ElMessageBox.confirm(
        '你确认删除该收藏信息吗？',
        '温馨提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'error',
            icon: markRaw(Delete)
        }
    )
        .then(async () => {
            //调用接口
            let result = await favoriteDeleteService(row.id)
            //点击确认
            ElMessage({
                type: 'success',
                message: '删除成功',
            })
            //刷新收藏列表
            getFavorite();
        })
        .catch(() => {
            //点击取消
            ElMessage({
                type: 'info',
                message: '取消删除',
            })
        })
}

//删除全部收藏数据
const deleteAllFavorite = () => {
    ElMessageBox.confirm(
        '你确认删除全部收藏吗？',
        '温馨提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'error',
            icon: markRaw(Delete)
        }
    )
        .then(async () => {
            //调用接口
            let result = await favoriteDeleteAllService()
            //点击确认
            ElMessage({
                type: 'success',
                message: '删除成功',
            })
            //刷新收藏列表
            getFavorite();
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

<style lang="scss" scoped>
.el-select {
    width: 150px;
}

//页面布局
.page-container {
    min-height: 100%;
    box-sizing: border-box;

    .header {
        display: flex;
        align-items: center;
        justify-content: space-between;
    }
}

//详细展示头部容器
.detailed-header-container {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    gap: 16px;
}
</style>