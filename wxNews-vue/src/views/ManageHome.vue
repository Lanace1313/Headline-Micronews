<template>
    <el-card class="page-container">
        <template #header>
            <div class="header">
                <span>系统首页</span>
            </div>
        </template>
        <div style="display: flex;">
            <!--ref 为了能够拿到图表函数 -->
            <div ref="pieChart" style="width: 45%;height: 400px"></div>
            <div ref="barChart" style="width: 45%;height: 400px"></div>
        </div>
        <template #footer>
            <AnnouncementVue></AnnouncementVue>
        </template>
    </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { articleCategoryListService } from '@/api/article.js'
import AnnouncementVue from '@/components/Announcement.vue'

import * as echarts from 'echarts';

const categorys = ref([
    {
        "id": 1,
        "categoryName": "测试",
        "articlesNum": "2",
    }
]);

const deviceChartsData = ref([]);



onMounted(() => {
    // 调用函数 处理图表将其渲染到页面
    init();
})

const pieChart = ref();
const barChart = ref();

const init = () => {
    const myPieChart = echarts.init(pieChart.value);
    const myBarChart = echarts.init(barChart.value);

    // 图表代码
    let pieOption = {
        title: {
            text: '文章分类',
            left: 'center'
        },
        tooltip: {
            trigger: 'item'
        },
        legend: {
            orient: 'vertical',
            left: 'left'
        },
        series: [
            {
                type: 'pie',
                radius: ['40%', '70%'],
                avoidLabelOverlap: false,
                padAngle: 2,
                itemStyle: {
                    borderRadius: 10,
                    borderColor: '#fff',
                    borderWidth: 2
                },
                label: {
                    show: false,
                    position: 'center'
                },
                emphasis: {
                    label: {
                        show: true,
                        fontSize: 40,
                        fontWeight: 'bold'
                    }
                },
                labelLine: {
                    show: false
                },
                data: deviceChartsData.value
            }
        ]
    };

    // 图表代码
    let barOption = {
        title: {
            text: '网站访问量',
            left: 'center'
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: [
            {
                type: 'category',
                data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
                axisTick: {
                    alignWithLabel: true
                }
            }
        ],
        yAxis: [
            {
                type: 'value'
            }
        ],
        series: [
            {
                name: '访问量',
                type: 'bar',
                barWidth: '60%',
                data: [10, 52, 200, 334, 390, 330, 220]
            }
        ]
    };

    const getCategoryPie = async () => {
        // 调用接口获取分类信息
        let result = await articleCategoryListService();
        // 确保 result.data 是数组
        if (Array.isArray(result.data)) {
            categorys.value = result.data;
            // 使用 forEach 遍历数组
            categorys.value.forEach(category => {
                // 确保 category 存在且有所需属性
                if (category && typeof category === 'object') {
                    deviceChartsData.value.push({
                        value: category.articlesNum,
                        name: category.categoryName
                    });
                }
            });
            // 设置饼图图表选项
            myPieChart.setOption(pieOption);
        }
    }
    getCategoryPie();

    // 设置柱状图图表选项
    myBarChart.setOption(barOption)
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