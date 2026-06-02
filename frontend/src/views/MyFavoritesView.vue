<template>
  <div class="page-container">
    <h2 class="section-title">我的收藏</h2>
    <div class="filter-bar">
      <el-select v-model="targetType" placeholder="全部类型" clearable style="width: 160px" @change="loadData">
        <el-option label="全部" value="" />
        <el-option label="景点" value="SPOT" />
        <el-option label="线路" value="ROUTE" />
      </el-select>
    </div>

    <el-empty v-if="!loading && favorites.length === 0" description="暂无收藏" />
    <el-table v-else :data="favorites" v-loading="loading" stripe>
      <el-table-column label="类型" width="100">
        <template #default="{ row }">{{ row.targetType === 'SPOT' ? '景点' : '线路' }}</template>
      </el-table-column>
      <el-table-column prop="targetName" label="名称" />
      <el-table-column label="收藏时间" width="170">
        <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button type="primary" link @click="goDetail(row)">查看详情</el-button>
          <el-button type="danger" link @click="handleRemove(row)">取消收藏</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-bar" v-if="total > pageSize">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="page" @current-change="loadData" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyFavorites, removeFavorite } from '../api/favoriteApi'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const favorites = ref([])
const targetType = ref('')
const page = ref(1)
const pageSize = 10
const total = ref(0)
const loading = ref(false)

function formatTime(t) { return t ? t.replace('T', ' ').substring(0, 16) : '' }

function goDetail(row) {
  if (row.targetType === 'SPOT') router.push(`/spots/${row.targetId}`)
  else router.push(`/routes/${row.targetId}`)
}

async function loadData() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize }
    if (targetType.value) params.targetType = targetType.value
    const data = await getMyFavorites(params)
    favorites.value = data.records || []
    total.value = data.total || 0
  } catch {} finally { loading.value = false }
}

async function handleRemove(row) {
  try {
    await ElMessageBox.confirm('确定取消收藏？', '确认', { type: 'warning' })
  } catch { return }
  try {
    await removeFavorite(row.id)
    ElMessage.success('取消收藏成功')
    loadData()
  } catch {}
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
