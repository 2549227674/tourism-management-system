<template>
  <div class="page-container">
    <el-skeleton v-if="loading" :rows="8" animated />
    <template v-else-if="spot">
      <div class="detail-section">
        <h1>{{ spot.name }}</h1>
        <div class="detail-meta">
          <span>地址：{{ spot.address }}</span>
          <span style="margin-left: 20px">票价：¥{{ spot.ticketPrice }}</span>
          <span style="margin-left: 20px">开放时间：{{ spot.openTime }}</span>
        </div>
        <el-image v-if="spot.imageUrl" :src="spot.imageUrl" style="max-width: 400px; margin-bottom: 20px" fit="cover" />
        <div class="detail-content">{{ spot.introduction }}</div>
      </div>
      <div class="detail-section">
        <h3>评论区</h3>
        <el-empty description="评论功能将在后续阶段实现" />
      </div>
    </template>
    <el-empty v-else description="景点不存在" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getSpotDetail } from '../api/spotApi'

const route = useRoute()
const spot = ref(null)
const loading = ref(true)

onMounted(async () => {
  try {
    spot.value = await getSpotDetail(route.params.id)
  } catch {} finally { loading.value = false }
})
</script>
