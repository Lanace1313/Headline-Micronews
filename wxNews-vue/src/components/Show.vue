<template>
    <dev class="page-container">
        <el-header>
            <div class="header-container">
                <!-- 搜索表单 -->
                <div style="margin-bottom: 10px;">
                    <el-form inline>
                        <el-form-item>
                            <el-input placeholder="搜索框" v-model="title" :prefix-icon="Search" size="large" />
                        </el-form-item>
                        <el-form-item>
                            <el-button @click="getArticles" plain type="primary" size="large">搜索</el-button>
                        </el-form-item>
                    </el-form>
                </div>
                <!-- 分类按钮容器 -->
                <div style="flex-wrap: wrap;">
                    <el-button @click="selectCategory('')" size="large" round>全部</el-button>
                    <el-button v-for="c in categorys" :key="c.id" @click="selectCategory(c.id)" size="large" round>
                        {{ c.categoryName }}
                    </el-button>
                </div>
            </div>
        </el-header>
        <!-- 文章列表 -->
        <el-main>
            <el-empty v-if="articles.length === 0" description="没有数据" />
            <ul class="article-list" else>
                <li v-for="article in articles" :key="article.id" style="margin: 10px;">
                    <el-card style="flex-direction: column; border-radius: 10px;">
                        <div style="display: flex; align-items: flex-start;">
                            <div style="flex-grow: 1;">
                                <div class="title-container">
                                    <strong class="title-style" @click="seeDetails(article)">
                                        {{ article.title }}</strong>
                                </div>
                                <el-text size="large" line-clamp=7 style="white-space: pre-wrap;">
                                    {{ article.content }}</el-text><br>
                                <el-text type="info">发布时间：{{ article.publishTime }}</el-text>
                            </div>
                            <img class="img-container" :src="article.coverImg" />
                        </div>
                    </el-card>
                </li>
            </ul>
        </el-main>
        <!-- 分页条 -->
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[3, 5, 10]"
            layout="jumper, total, sizes, prev, pager, next" background :total="total" @size-change="onSizeChange"
            @current-change="onCurrentChange" style="margin-top: 20px; justify-content: center " />

        <!-- 详细展示 -->
        <el-dialog v-model="visible" :show-close="false" width="70%" center :model="articlesModel">
            <template #header="{ close, titleId, titleClass }">
                <div class="detailed-header-container">
                    <el-button type="warning" :icon="Star" @click="addfavorite(articlesModel.id)" circle />
                    <h4 :id="titleId" :class="titleClass"></h4>
                    <el-button type="danger" @click="close">
                        <el-icon class="el-icon--left">
                            <CircleCloseFilled />
                        </el-icon>
                    </el-button>
                </div>
            </template>

            <el-card shadow="never">
                <template #header>
                    <div class="title-container">
                        <strong style="font-size: 28px;">{{ articlesModel.title }}</strong>
                    </div>
                </template>
                <el-text size="large" style="white-space: pre-wrap;">
                    {{ articlesModel.content }}
                </el-text><br>

                <template class="detailed-header-container">
                    <el-text type="info">发布时间：{{ articlesModel.publishTime }}</el-text>
                    <el-text type="info">收藏数：{{ articlesModel.favoriteNum }}</el-text>
                </template>
            </el-card>

            <template #footer>
                <el-text>更多内容请下载 头条微讯APP</el-text>
            </template>
        </el-dialog>
    </dev>
</template>

<script setup>
import { ref, Text } from 'vue'
//引入element-plus的提示组件
import { ElButton, ElDrawer, ElMessage, ElMessageBox } from 'element-plus'
//引入element-plus的图标
import { Plus, Search, Star, CircleCloseFilled } from '@element-plus/icons-vue'
//引入接口
import { articleCategoryListService, articleListService, favoriteAddService } from '@/api/article.js';

//文章分类数据模型
const categorys = ref([
    {
        "id": 1,
        "categoryName": "时事",
        "articlesNum": 1
    },
    {
        "id": 2,
        "categoryName": "科技",
        "articlesNum": 2
    },
    {
        "id": 3,
        "categoryName": "娱乐",
        "articlesNum": 0
    },
    {
        "id": 4,
        "categoryName": "体育",
        "articlesNum": 0
    }
])

//用户搜索时选中的分类id
const categoryId = ref('')

//当前类别ID发生变化，调用此函数
const selectCategory = (num) => {
    categoryId.value = num
    getArticles()
}

//文章列表数据模型
const articles = ref([
    {
        "id": 9,
        "title": "“早就该炸掉”？让西安居民痛恨的三门峡大坝，是壮举还是败笔？",
        "content": "三门峡大坝，被冠以“万里黄河第一坝”的美誉，自打它问世起，就在中国水利史上刻下了重重的一笔。\n这不仅仅是因为它给黄河中下游筑起了一道坚固的防洪墙，更是因为它在发电、灌溉、供水等多个领域都展现出了非凡的实力。\n但话说回来，就像每个硬币都有两面，三门峡大坝也不例外，随着它的运行，一些问题也渐渐浮出了水面。",
        "coverImg": "http://sfxgu7vn0.hn-bkt.clouddn.com/myimage/8f165456-ffd8-4278-a065-5bbb356cf707.jpg",
        "state": "已发布",
        "categoryId": 2,
        "userId": 1,
        "publishTime": "2024-07-03 23:42:26",
        "favoriteNum": 0
    },
    {
        "id": 11,
        "title": "网球天后李娜近况，因变得太漂亮被怀疑整容，她的回应让人很痛快",
        "content": "网球天后李娜近况，因变得太漂亮被怀疑整容，她的回应让人很痛快\n首发2024-07-02 14:59·云端书苑\n请用您发财的金手指，点赞走一走，暴富到永久，上点关注下点赞，不想暴富都很难\n2023年7月13日，温网元老赛的赛场上，一道熟悉而又焕然一新的身影吸引了所有人的目光。身着洁白网球服的李娜，这位曾经的\"网坛天后\"，时隔多年重返赛场，却因其容貌的变化引发了一场意料之外的风波。\n观众们的注意力并未集中在她的球技上，而是被她愈发靓丽的面容所吸引。议论声此起彼伏，有人惊叹她保养得宜，也有人质疑她是否动过刀子。\n面对纷纭的猜测，李娜将如何回应？她的态度又会给我们带来怎样的启示？让我们一同揭开这位传奇运动员回归赛场背后的故事，探索她如何应对这场因美貌而起的风波。\n李娜的网球之路始于一个充满运动基因的家庭。她的父亲李盛鹏是湖北省队的羽毛球健将，在李娜年仅十岁时就做出了一个改变她一生的决定——将她送入体育队接受专业训练。",
        "coverImg": "http://sfxgu7vn0.hn-bkt.clouddn.com/myimage/c1a76246-d653-45d8-9c1a-2aa39998eaea.jpg",
        "state": "已发布",
        "categoryId": 7,
        "userId": 2,
        "publishTime": "2024-07-04 12:59:51",
        "favoriteNum": 0
    },
    {
        "id": 13,
        "title": "为打压正在复苏的华为，拜登政府被曝今年以来已撤销8项出口许可",
        "content": "7月3日，路透社独家爆料称，为打压呈复苏势态的华为，拜登政府今年以来已经撤销了8个允许美企向华为出口的许可证。美国商务部曾于5月初证实，已“撤销了对华为的部分出口许可”，当时高通、英特尔等美国芯片企业被曝受到影响。\n“自2024年初以来，（商务部）又撤销了8个涉及华为的出口许可证，”路透社从获得的文件中得知，美国商务部近期在回应众议院外交事务委员会主席、共和党籍众议员迈克尔·麦考尔质询时如此表示。\n美国政府在2019年将华为列入“贸易黑名单”，限制大多数美国供应商向华为运送商品和技术。不过，美国官员仍会向部分美企发出许可证，允许华为获取部分产品。\n美国商务部在文件中表示，截至目前，美企已获得了价值数十亿美元涉及华为的出口许可证，但大多包括“运动器材、办公家具以及低技术含量组件，如平板电脑的触摸板和触摸屏传感器”；从2018年到2023年，涉及“黑名单”中企的出口许可申请总价值为8800亿美元，其中仅有3350亿美元获批。\n路透社认为，这些细节揭示了拜登政府正采取措施，进一步打压正在复苏的华为。美国会众议院外交事务委员会的一名发言人证实，该委员会于当地时间2日收到了这些数据，目前正对其进行审查。",
        "coverImg": "",
        "state": "已发布",
        "categoryId": 1,
        "userId": 2,
        "publishTime": "2024-07-04 13:27:24",
        "favoriteNum": 0
    }
])

//控制详细页面展示
const visible = ref(false)

//文章详细数据模型
const articlesModel = ref({
    "id": 1,
    "title": "测试标题",
    "content": "测试内容",
    "categoryId": 1,
    "userId": 1,
    "publishTime": "2024-00-00 23:59:59",
    "favoriteNum": 0
})

//分页条数据模型
const pageNum = ref(1)//当前页
const total = ref(20)//总条数
const pageSize = ref(3)//每页条数

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

//用户搜索时的搜索参数
const title = ref('')

//文章列表查询
const getArticles = async () => {
    let params = {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        categoryId: categoryId.value ? categoryId.value : null,
        title: title.value ? title.value : null,
        state: '已发布',
        use: 1
    }
    title.value = ''
    let result = await articleListService(params);
    //渲染列表数据
    articles.value = result.data.items
    //渲染总条数
    total.value = result.data.total
}

getArticleCategoryList();
getArticles()

//添加收藏
const addfavorite = async (id) => {
    let result = await favoriteAddService(id);
    ElMessage.success(result.message || '收藏成功')
}
//详细信息显示
const seeDetails = async (article) => {
    visible.value = true;
    articlesModel.value = article
}
</script>

<style lang="scss" scoped>
//分页容器
.page-container {
    min-height: 100%;
    max-width: 100%;
    box-sizing: border-box;
    display: flex;
    justify-content: center;
}

//头部容器
.header-container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}

//文章列表
.article-list {
    list-style-type: none;
    padding: 0;
    margin-top: 30px;
    display: flex;
    flex-wrap: wrap;
}

// 标题容器
.title-container {
    /* 使用Flexbox布局 */
    display: flex;
    /* 水平居中 */
    justify-content: center;
    /* 标题和内容之间留一些空间 */
    margin-bottom: 5px;
}

// 标题样式
.title-style {
    font-size: 24px;
    cursor: pointer;
    color: black;
    transition: color 0.3s;
}

.title-style:hover {
    color: #b25252;
}

//图片容器
.img-container {
    height: 240px;
    width: auto;
    max-width: 50%;
    margin-left: 20px;
    border-radius: 8px;
}

//详细展示头部容器
.detailed-header-container {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    gap: 10px;
    height: 20px;
}
</style>