<template>
  <div class="page-container">
    <h2 class="section-title">公告资讯</h2>
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索公告标题" clearable style="width: 240px" @keyup.enter="loadData" />
      <el-button type="primary" @click="loadData">搜索</el-button>
    </div>

    <el-empty v-if="!loading && announcements.length === 0" description="暂无公告" />
    <el-table v-else :data="announcements" stripe @row-click="goDetail">
      <el-table-column prop="title" label="标题" />
      <el-table-column label="发布时间" width="200">
        <template #default="{ row }">{{ formatTime(row.publishTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="primary" link @click.stop="goDetail(row)">查看详情</el-button>
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
import { getAnnouncements } from '../api/announcementApi'

const router = useRouter()
const announcements = ref([])
const keyword = ref('')
const page = ref(1)
const pageSize = 10
const total = ref(0)
const loading = ref(false)

function formatTime(t) {
  return t ? t.replace('T', ' ').substring(0, 16) : ''
}

function goDetail(row) {
  router.push(`/announcements/${row.id}`)
}

async function loadData() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize }
    if (keyword.value) params.keyword = keyword.value
    const data = await getAnnouncements(params)
    announcements.value = data.records || []
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
