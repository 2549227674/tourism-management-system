<template>
  <div class="page-container">
    <el-skeleton v-if="loading" :rows="6" animated />
    <template v-else-if="announcement">
      <div class="detail-section">
        <h1>{{ announcement.title }}</h1>
        <div class="detail-meta">
          发布时间：{{ formatTime(announcement.publishTime) }}
        </div>
        <div class="detail-content">{{ announcement.content }}</div>
      </div>
    </template>
    <el-empty v-else description="公告不存在或未发布" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getAnnouncementDetail } from '../api/announcementApi'

const route = useRoute()
const announcement = ref(null)
const loading = ref(true)

function formatTime(t) {
  return t ? t.replace('T', ' ').substring(0, 16) : ''
}

onMounted(async () => {
  try {
    announcement.value = await getAnnouncementDetail(route.params.id)
  } catch {} finally { loading.value = false }
})
</script>
