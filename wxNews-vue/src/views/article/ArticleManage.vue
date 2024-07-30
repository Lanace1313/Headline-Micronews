<template>
    <el-card class="page-container">
        <template #header>
            <div class="header">
                <span>文章管理</span>
                <div class="extra">
                    <el-button type="primary"
                        @click="title = '添加文章'; visibleDrawer = true; clearArticleModel()">添加文章</el-button>
                </div>
            </div>
        </template>
        <!-- 搜索表单 -->
        <el-form inline>
            <el-form-item label="文章分类：">
                <el-select placeholder="请选择" v-model="categoryId">
                    <el-option v-for="c in categorys" :key="c.id" :label="c.categoryName" :value="c.id">
                    </el-option>
                </el-select>
            </el-form-item>

            <el-form-item label="发布状态：">
                <el-select placeholder="请选择" v-model="state">
                    <el-option label="已发布" value="已发布"></el-option>
                    <el-option label="草稿" value="草稿"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="getArticles">搜索</el-button>
                <el-button @click="categoryId = ''; state = ''; getArticles()">重置</el-button>
            </el-form-item>
        </el-form>
        <!-- 文章列表 -->
        <el-table :data="articles" style="width: 100%">
            <el-table-column label="文章标题" width="300" prop="title"></el-table-column>
            <el-table-column label="分类" width="100" prop="categoryName"></el-table-column>
            <el-table-column label="用户ID" width="100" prop="userId"> </el-table-column>
            <el-table-column label="发表时间" prop="publishTime"> </el-table-column>
            <el-table-column label="状态" prop="state"></el-table-column>
            <el-table-column label="操作" width="100">
                <!-- eslint-disable-next-line -->
                <template #default="{ row }">
                    <el-button :icon="Edit" circle plain type="primary" @click="updateArticlesEcho(row)"></el-button>
                    <el-button :icon="Delete" circle plain type="danger" @click="deleteArticle(row)"></el-button>
                </template>
            </el-table-column>
            <template #empty>
                <el-empty description="没有数据" />
            </template>
        </el-table>
        <!-- 分页条 -->
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[3, 5, 10]"
            layout="jumper, total, sizes, prev, pager, next" background :total="total" @size-change="onSizeChange"
            @current-change="onCurrentChange" style="margin-top: 20px; justify-content: center; " />
        <el-drawer v-model="visibleDrawer" :title="title" direction="rtl" size="50%">
            <!-- 添加文章表单 -->
            <el-form :model="articleModel" label-width="100px" :rules="articleDataRules">
                <el-form-item label="文章标题" prop="title">
                    <el-input v-model="articleModel.title" placeholder="请输入标题"></el-input>
                </el-form-item>
                <el-form-item label="文章分类" prop="categoryId">
                    <el-select placeholder="请选择" v-model="articleModel.categoryId">
                        <el-option v-for="c in categorys" :key="c.id" :label="c.categoryName" :value="c.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="文章封面">
                    <!-- auto-upload:是否自动上传
                    action: 服务器接口路径
                    name: 上传的文件字段名
                    headers: 设置上传的请求头
                    on-success: 上传成功的回调函数 -->
                    <el-upload class="avatar-uploader" :auto-upload="true" :show-file-list="false" action="/api/upload"
                        name="file" :headers="{ 'Authorization': tokenStore.token }" :on-success="uploadSuccess">
                        <img v-if="articleModel.coverImg" :src="articleModel.coverImg" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                </el-form-item>
                <el-form-item label="文章内容" prop="content">
                    <el-input v-model="articleModel.content" type="textarea" :rows="11" style="width: 100%;"
                        placeholder="请输入文章内容"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary"
                        @click="title == '添加文章' ? addArticle('已发布') : updateArticle('已发布')">发布</el-button>
                    <el-button type="info"
                        @click="title == '添加文章' ? addArticle('草稿') : updateArticle('草稿')">草稿</el-button>
                </el-form-item>
            </el-form>
        </el-drawer>
    </el-card>
</template>

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
import { articleCategoryListService, articleListService, articleAddService, articleUpdateService, articleDeleteService } from '@/api/article.js';

//文章分类数据模型
const categorys = ref([
    {
        "id": 1,
        "categoryName": "测试",
        "publishTime": "2023-09-02 12:06:59"
    }
])

//用户搜索时选中的分类id
const categoryId = ref('')

//用户搜索时选中的发布状态
const state = ref('')

//文章列表数据模型
const articles = ref([
    {
        "id": 1,
        "title": "测试",
        "userId": "1",
        "content": "无数据",
        "coverImg": "",
        "state": "已发布",
        "categoryId": 1,
        "publishTime": "2023-09-03 11:55:30",
    }
])

//分页条数据模型
const pageNum = ref(1)//当前页
const total = ref(20)//总条数
const pageSize = ref(5)//每页条数

//当每页条数发生了变化，调用此函数
const onSizeChange = (size) => {
    pageSize.value = size
    getArticles()
}
//当前页码发生变化，调用此函数
const onCurrentChange = (num) => {
    pageNum.value = num
    getArticles()
}

//文章分类列表查询
const getArticleCategoryList = async () => {
    //获取所有分类
    let resultC = await articleCategoryListService();
    categorys.value = resultC.data
}

//文章列表查询
const getArticles = async () => {
    let params = {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        categoryId: categoryId.value ? categoryId.value : null,
        state: state.value ? state.value : null
    }
    let result = await articleListService(params);
    //渲染列表数据
    articles.value = result.data.items
    //为列表中添加categoryName属性
    for (let i = 0; i < articles.value.length; i++) {
        let article = articles.value[i];
        for (let j = 0; j < categorys.value.length; j++) {
            if (article.categoryId === categorys.value[j].id) {
                article.categoryName = categorys.value[j].categoryName
            }
        }
    }
    //渲染总条数
    total.value = result.data.total
}

getArticleCategoryList();
getArticles()


//控制抽屉是否显示
const visibleDrawer = ref(false)
//抽屉标题
const title = ref('')

//添加表单数据模型
const articleModel = ref({
    title: '',
    categoryId: '',
    coverImg: '',
    content: '',
    state: ''
})

import { useTokenStore } from '@/stores/token.js'
const tokenStore = useTokenStore();

//上传图片成功回调
const uploadSuccess = (img) => {
    //img就是后台响应的数据，格式为：{code:状态码，message：提示信息，data: 图片的存储地址}
    if(img.code === 200){
    articleModel.value.coverImg = img.data
    console.log(img.data);
    }
    else ElMessage.error('上传文件异常')
}

//添加文章
const addArticle = async (state) => {
    articleModel.value.state = state
    let result = await articleAddService(articleModel.value);
    ElMessage.success(result.message || '添加成功')
    //再次调用getArticles,获取文章
    getArticles()
    //隐藏抽屉
    visibleDrawer.value = false
    //清空表单数据
    clearArticleModel
}

//编辑文章弹窗回显
const updateArticlesEcho = (row) => {
    title.value = '编辑文章'
    visibleDrawer.value = true
    //将row的数据赋值给articleModel
    articleModel.value = row
}

//编辑文章
const updateArticle = async (state) => {
    articleModel.value.state = state
    let result = await articleUpdateService(articleModel.value);
    ElMessage.success(result.message || '编辑成功')
    //再次调用getArticles,获取文章
    getArticles()
    //隐藏抽屉
    visibleDrawer.value = false
    //清空表单数据
    clearArticleModel
}

//删除文章数据
const deleteArticle = (row) => {
    ElMessageBox.confirm(
        '你确认删除该文章信息吗？',
        '温馨提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
        .then(async () => {
            //调用接口
            let result = await articleDeleteService(row.id)
            //点击确认
            ElMessage({
                type: 'success',
                message: '删除成功',
            })
            //刷新文章列表
            getArticles();
        })
        .catch(() => {
            //点击取消
            ElMessage({
                type: 'info',
                message: '取消删除',
            })
        })
}

// 清空文章表单模型
const clearArticleModel = () => {
    articleModel.value = {
        title: '',
        categoryId: '',
        coverImg: '',
        content: '',
        state: ''
    }
};

//用于文章的表单校验模型
const articleDataRules = ref({
    title: [
        { required: true, message: '请输入文章标题', trigger: 'blur' },
        { min: 1, max: 30, message: '文章标题过长', trigger: 'blur' }
    ],
    categoryId: [
        { required: true, message: '请选择文章分类', trigger: 'blur' }
    ],
    content: [
        { required: true, message: '请输入文章内容', trigger: 'blur' },
    ]
})
</script>

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

/* 抽屉样式 */
.avatar-uploader {
    :deep() {
        .avatar {
            width: 178px;
            height: 178px;
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
            width: 178px;
            height: 178px;
            text-align: center;
        }
    }
}
</style>