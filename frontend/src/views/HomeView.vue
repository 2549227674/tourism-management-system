<template>
  <div class="page-container">
    <div class="hero">
      <h1>欢迎来到旅游管理系统</h1>
      <p>发现精彩景点，规划完美旅程</p>
    </div>

    <div class="section">
      <h2 class="section-title">推荐景点</h2>
      <el-empty v-if="spots.length === 0" description="暂无数据" />
      <div v-else class="card-grid">
        <el-card v-for="spot in spots" :key="spot.id" @click="$router.push(`/spots/${spot.id}`)">
          <template #header>{{ spot.name }}</template>
          <p>地址：{{ spot.address }}</p>
          <p>票价：¥{{ spot.ticketPrice }}</p>
        </el-card>
      </div>
    </div>

    <div class="section">
      <h2 class="section-title">推荐线路</h2>
      <el-empty v-if="routes.length === 0" description="暂无数据" />
      <div v-else class="card-grid">
        <el-card v-for="r in routes" :key="r.id" @click="$router.push(`/routes/${r.id}`)">
          <template #header>{{ r.name }}</template>
          <p>价格：¥{{ r.price }}</p>
          <p>状态：{{ r.status === 'OPEN' ? '开放预订' : r.status === 'FULL' ? '名额已满' : r.status }}</p>
        </el-card>
      </div>
    </div>

    <div class="section">
      <h2 class="section-title">最新公告</h2>
      <el-empty v-if="announcements.length === 0" description="暂无数据" />
      <el-table v-else :data="announcements" stripe @row-click="goAnnouncement">
        <el-table-column prop="title" label="标题" />
        <el-table-column label="发布时间" width="200">
          <template #default="{ row }">{{ formatTime(row.publishTime) }}</template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getSpots } from '../api/spotApi'
import { getRoutes } from '../api/routeApi'
import { getAnnouncements } from '../api/announcementApi'

const router = useRouter()
const spots = ref([])
const routes = ref([])
const announcements = ref([])

function formatTime(t) {
  return t ? t.replace('T', ' ').substring(0, 16) : ''
}

function goAnnouncement(row) {
  router.push(`/announcements/${row.id}`)
}

onMounted(async () => {
  try {
    const spotData = await getSpots({ page: 1, pageSize: 4 })
    spots.value = spotData.records || []
  } catch {}
  try {
    const routeData = await getRoutes({ page: 1, pageSize: 4 })
    routes.value = routeData.records || []
  } catch {}
  try {
    const annData = await getAnnouncements({ page: 1, pageSize: 5 })
    announcements.value = annData.records || []
  } catch {}
})
</script>

<style scoped>
.hero {
  text-align: center;
  padding: 96px 0 64px;
  background: #ffffff;
  margin-bottom: 48px;
}

.hero h1 {
  font-size: 28px;
  font-weight: 700;
  color: #222222;
  margin-bottom: 12px;
}

.hero p {
  font-size: 16px;
  color: #6a6a6a;
}

.section {
  margin-bottom: 30px;
}
</style>
