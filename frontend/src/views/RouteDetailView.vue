<template>
  <div class="page-container">
    <el-skeleton v-if="loading" :rows="8" animated />
    <template v-else-if="route">
      <div class="detail-section">
        <h1>{{ route.name }}</h1>
        <div class="detail-meta">
          <span>出发时间：{{ formatTime(route.departureTime) }}</span>
          <span style="margin-left: 20px">
            <el-tag :type="route.status === 'OPEN' ? 'success' : 'info'" size="small">
              {{ route.status === 'OPEN' ? '开放预订' : route.status === 'FULL' ? '名额已满' : route.status }}
            </el-tag>
          </span>
        </div>
        <el-descriptions :column="2" border style="margin-bottom: 20px">
          <el-descriptions-item label="价格">¥{{ route.price }}</el-descriptions-item>
          <el-descriptions-item label="总名额">{{ route.quota }}</el-descriptions-item>
          <el-descriptions-item label="已预订">{{ route.bookedCount }}</el-descriptions-item>
          <el-descriptions-item label="剩余名额">{{ route.quota - route.bookedCount }}</el-descriptions-item>
        </el-descriptions>
        <h3 style="margin-bottom: 10px">行程安排</h3>
        <div class="detail-content">{{ route.itinerary }}</div>
      </div>
      <div class="detail-section" style="text-align: center">
        <el-button type="primary" size="large" @click="handleBook">
          {{ route.status === 'OPEN' ? '立即预订' : '暂不可预订' }}
        </el-button>
      </div>
    </template>
    <el-empty v-else description="线路不存在" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getRouteDetail } from '../api/routeApi'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'

const routeParam = useRoute()
const router = useRouter()
const auth = useAuthStore()
const route = ref(null)
const loading = ref(true)

function formatTime(t) {
  return t ? t.replace('T', ' ').substring(0, 16) : ''
}

function handleBook() {
  if (!auth.isLoggedIn()) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  ElMessage.info('订单预订将在下一阶段实现')
}

onMounted(async () => {
  try {
    route.value = await getRouteDetail(routeParam.params.id)
  } catch {} finally { loading.value = false }
})
</script>
