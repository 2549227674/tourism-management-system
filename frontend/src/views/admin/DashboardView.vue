<template>
  <div>
    <h2 class="section-title">后台首页</h2>
    <el-skeleton v-if="loading" :rows="4" animated />
    <template v-else>
      <el-row :gutter="20">
        <el-col :span="6" v-for="item in cards" :key="item.label">
          <el-card class="stat-card">
            <div class="stat-value">{{ item.value }}</div>
            <div class="stat-label">{{ item.label }}</div>
          </el-card>
        </el-col>
      </el-row>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getStatisticsSummary } from '../../api/statisticsApi'

const stats = ref(null)
const loading = ref(true)

const cards = computed(() => {
  if (!stats.value) return []
  return [
    { label: '景点数', value: stats.value.spotCount },
    { label: '线路数', value: stats.value.routeCount },
    { label: '用户数', value: stats.value.userCount },
    { label: '订单总数', value: stats.value.orderCount },
    { label: '待处理订单', value: stats.value.pendingOrderCount },
    { label: '有效订单金额', value: `¥${stats.value.totalOrderAmount}` }
  ]
})

onMounted(async () => {
  try {
    stats.value = await getStatisticsSummary()
  } catch {} finally { loading.value = false }
})
</script>

<style scoped>
.stat-card {
  margin-bottom: 20px;
}
</style>
