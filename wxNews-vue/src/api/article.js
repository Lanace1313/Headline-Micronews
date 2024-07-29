//导入请求工具类
import request from '@/utils/request.js'
//导入@/stores/token.js
//import { useTokenStore } from '../stores/token'

//文章分类列表查询
export const articleCategoryListService = () => {
    //获取token状态
    //const tokenStore = useTokenStore()
    //通过请求头Authorization携带token
    //return request.get('/category', { headers: { 'Authorization': tokenStore.token } })
    return request.get('/category')
}

//文章分类添加
export const articleCategoryAddService = (CategoryData) => {
    return request.post('/category', CategoryData)
}

//删除文章分类
export const articleCategoryDeleteService = (id) => {
    return request.delete('/category?id='+id)
}

//修改分类
export const articleCategoryUpdateService = (categoryModel)=>{
    return request.put('/category',categoryModel)
}

//文章列表查询
export const articleListService = (params) => {
    return request.get('/article', { params: params })
}

//添加文章
export const articleAddService = (articleModel)=>{
    return request.post('/article',articleModel)
}

//修改文章  
export const articleUpdateService = (articleModel)=>{
    return request.put('/article',articleModel)
}

//删除文章
export const articleDeleteService = (id)=>{
    return request.delete('/article?id='+id)
}

//获取收藏列表
export const favoriteListService =() => {
    return request.get('/article/favorite')
}

//添加收藏
export const favoriteAddService = (id)=>{
    return request.post('/article/favorite?articleId='+id)
}

//删除收藏
export const favoriteDeleteService = (id)=>{
    return request.delete('/article/favorite?articleId='+id)
}

//清空全部收藏
export const favoriteDeleteAllService = ()=>{
    return request.delete('/article/favorite/all')
}
