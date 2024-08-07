//导入vue-router
import { createRouter, createWebHistory } from 'vue-router'
//导入组件
import ManagetVue from '@/layouts/Manage.vue'
import HomeVue from '@/layouts/Home.vue'

import LoginVue from '@/views/Login.vue'
import ManageHomeVue from '@/views/ManageHome.vue'
import ArticleCategoryVue from '@/views/article/ArticleCategory.vue'
import ArticleFavorite from '@/views/article/ArticleFavorite.vue'
import ArticleManageVue from '@/views/article/ArticleManage.vue'
import UserManage from '@/views/user/UserManage.vue'
import UserInfoVue from '@/views/user/UserInfo.vue'
import UserResetPasswordVue from '@/views/user/UserResetPassword.vue'

//定义路由关系
const routes = [
    { path: '/login', component: LoginVue },
    { path: '/', component: HomeVue },
    {
        path: '/manage',
        component: ManagetVue,
        //重定向
        redirect: '/manage/home',
        //子路由
        children: [
            { path: '/manage/home', component: ManageHomeVue },
            { path: '/article/category', component: ArticleCategoryVue },
            { path: '/article/favorite', component: ArticleFavorite },
            { path: '/article/manage', component: ArticleManageVue },
            { path: '/user/manage', component: UserManage },
            { path: '/user/info', component: UserInfoVue },
            { path: '/user/password', component: UserResetPasswordVue },
        ]
    }
]

//创建路由器
const router = createRouter({
    history: createWebHistory(),
    routes: routes
});

//导出路由
export default router