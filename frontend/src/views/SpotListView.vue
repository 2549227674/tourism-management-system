<template>
  <div class="page-container">
    <h2 class="section-title">景点列表</h2>
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索景点名称" clearable style="width: 240px" @keyup.enter="loadData" />
      <el-select v-model="categoryId" placeholder="全部分类" clearable style="width: 160px" @change="loadData">
        <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-button type="primary" @click="loadData">搜索</el-button>
    </div>

    <el-empty v-if="!loading && spots.length === 0" description="暂无景点数据" />
    <div v-else class="card-grid">
      <el-card v-for="spot in spots" :key="spot.id" @click="$router.push(`/spots/${spot.id}`)">
        <template #header>{{ spot.name }}</template>
        <p>地址：{{ spot.address }}</p>
        <p>票价：¥{{ spot.ticketPrice }}</p>
        <p>开放时间：{{ spot.openTime }}</p>
      </el-card>
    </div>

    <div class="pagination-bar" v-if="total > pageSize">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="page" @current-change="loadData" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getSpots, getCategories } from '../api/spotApi'

const spots = ref([])
const categories = ref([])
const keyword = ref('')
const categoryId = ref(null)
const page = ref(1)
const pageSize = 10
const total = ref(0)
const loading = ref(false)

async function loadData() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize }
    if (keyword.value) params.keyword = keyword.value
    if (categoryId.value) params.categoryId = categoryId.value
    const data = await getSpots(params)
    spots.value = data.records || []
    total.value = data.total || 0
  } catch {} finally { loading.value = false }
}

onMounted(async () => {
  try {
    categories.value = await getCategories()
  } catch {}
  loadData()
})
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
