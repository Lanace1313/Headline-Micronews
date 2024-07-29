<template>
    <el-card class="page-container">
        <template #header>
            <div class="header">
                <span>文章分类</span>
                <div class="extra">
                    <el-button type="primary"
                        @click="title = '添加分类'; dialogVisible = true; clearCategoryModel()">添加分类</el-button>
                </div>
            </div>
        </template>
        <el-table :data="categorys" style="width: 100%">
            <el-table-column label="序号" width="100" type="index"> </el-table-column>
            <el-table-column label="分类名称" prop="categoryName"></el-table-column>
            <el-table-column label="类别文章数" prop="articlesNum"></el-table-column>
            <el-table-column label="操作" width="100">
                <!-- eslint-disable-next-line -->
                <template #default="{ row }">
                    <el-button :icon="Edit" circle plain type="primary" @click="updateCategoryEcho(row)"></el-button>
                    <el-button :icon="Delete" circle plain type="danger" @click="deleteCategory(row)"></el-button>
                </template>
            </el-table-column>
            <template #empty>
                <el-empty description="没有数据" />
            </template>
        </el-table>

        <!-- 分类弹窗 -->
        <el-dialog v-model="dialogVisible" :title="title" width="30%">
            <el-form :model="categoryModel" :rules="rules" label-width="100px" style="padding-right: 30px">
                <el-form-item label="分类名称" prop="categoryName">
                    <el-input v-model="categoryModel.categoryName" minlength="1" maxlength="10"></el-input>
                </el-form-item>
                <el-form-item v-if="title !== '添加分类'" label="类别文章数" prop="articlesNum">
                    <el-input v-model="categoryModel.articlesNum" minlength="1" maxlength="15" readonly disabled></el-input>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="title === '添加分类' ? addCategory() : updateCategory()"> 确认
                    </el-button>
                </span>
            </template>
        </el-dialog>
    </el-card>
</template>

<script setup>
import { Edit, Delete } from '@element-plus/icons-vue'
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { articleCategoryListService, articleCategoryAddService, articleCategoryDeleteService, articleCategoryUpdateService } from '@/api/article.js'

const categorys = ref([
    {
        "id": 1,
        "categoryName": "测试",
        "articlesNum": "2",
    }
])

//获取所有文章分类数据
const getAllCategory = async () => {
    let result = await articleCategoryListService();
    categorys.value = result.data;
}
getAllCategory();

//控制分类弹窗
const dialogVisible = ref(false)

//弹窗标题
const title = ref('')

//分类数据模型
const categoryModel = ref({
    categoryName: '',
    articlesNum: ''
})

//分类表单校验
const rules = {
    categoryName: [
        { required: true, message: '请输入分类名称', trigger: 'blur' },
    ]
}

//添加分类文章数据
const addCategory = async () => {
    //调用接口
    let result = await articleCategoryAddService(categoryModel.value);
    ElMessage.success(result.message || '添加成功')
    //再次调用获取所有文章分类的函数
    getAllCategory();
    //隐藏弹窗
    dialogVisible.value = false;
    //清空添加表单
    clearCategoryModel
}

//删除分类文章数据
const deleteCategory = (row) => {
    ElMessageBox.confirm(
        '你确认删除该分类信息吗？',
        '温馨提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
        .then(async () => {
            //调用接口
            let result = await articleCategoryDeleteService(row.id)
            //点击确认
            ElMessage({
                type: 'success',
                message: '删除成功',
            })
            //刷新列表
            getAllCategory();
        })
        .catch(() => {
            //点击取消
            ElMessage({
                type: 'info',
                message: '取消删除',
            })
        })
}

//修改分类弹窗回显
const updateCategoryEcho = (row) => {
    title.value = '修改分类'
    dialogVisible.value = true
    //将row中的数据赋值给categoryModel
    categoryModel.value.categoryName = row.categoryName
    categoryModel.value.articlesNum = row.articlesNum
    //修改的时候必须传递分类的id，所以扩展一个id属性
    categoryModel.value.id = row.id
}

//修改分类
const updateCategory = async () => {
    let result = await articleCategoryUpdateService(categoryModel.value)
    ElMessage.success(result.message || '修改成功')
    //隐藏弹窗
    dialogVisible.value = false
    //再次访问后台接口，查询所有分类
    getAllCategory()
}

//清空模型数据
const clearCategoryModel = () => {
    categoryModel.value.categoryName = ''
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
</style>