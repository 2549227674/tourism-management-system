<template>
  <div class="page-container">
    <h2 class="section-title">旅游线路</h2>
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索线路名称" clearable style="width: 240px" @keyup.enter="loadData" />
      <el-select v-model="status" placeholder="全部状态" clearable style="width: 160px" @change="loadData">
        <el-option label="开放预订" value="OPEN" />
        <el-option label="名额已满" value="FULL" />
      </el-select>
      <el-button type="primary" @click="loadData">搜索</el-button>
    </div>

    <el-empty v-if="!loading && routes.length === 0" description="暂无线路数据" />
    <div v-else class="card-grid">
      <el-card v-for="r in routes" :key="r.id" @click="$router.push(`/routes/${r.id}`)">
        <template #header>{{ r.name }}</template>
        <p>价格：¥{{ r.price }}</p>
        <p>出发时间：{{ formatTime(r.departureTime) }}</p>
        <p>名额：{{ r.bookedCount }} / {{ r.quota }}</p>
        <p>
          <el-tag :type="r.status === 'OPEN' ? 'success' : 'info'" size="small">
            {{ r.status === 'OPEN' ? '开放预订' : r.status === 'FULL' ? '名额已满' : r.status }}
          </el-tag>
        </p>
      </el-card>
    </div>

    <div class="pagination-bar" v-if="total > pageSize">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="page" @current-change="loadData" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getRoutes } from '../api/routeApi'

const routes = ref([])
const keyword = ref('')
const status = ref('')
const page = ref(1)
const pageSize = 10
const total = ref(0)
const loading = ref(false)

function formatTime(t) {
  return t ? t.replace('T', ' ').substring(0, 16) : ''
}

async function loadData() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize }
    if (keyword.value) params.keyword = keyword.value
    if (status.value) params.status = status.value
    const data = await getRoutes(params)
    routes.value = data.records || []
    total.value = data.total || 0
  } catch {} finally { loading.value = false }
}

onMounted(() => loadData())
</script>

<style scoped>
.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.pagination-bar {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
