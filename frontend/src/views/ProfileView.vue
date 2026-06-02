<template>
  <div class="page-container">
    <h2 class="section-title">个人中心</h2>
    <div class="detail-section">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户 ID">{{ user?.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ user?.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ user?.nickname || '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ user?.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ user?.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="user?.role === 'ADMIN' ? 'danger' : ''" size="small">{{ user?.role === 'ADMIN' ? '管理员' : '普通用户' }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <div style="margin-top: 20px">
        <el-button type="primary" :loading="refreshing" @click="handleRefresh">刷新资料</el-button>
      </div>
      <p style="margin-top: 12px; color: #909399; font-size: 14px">资料修改功能将在后续阶段完善</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'

const auth = useAuthStore()
const refreshing = ref(false)
const user = computed(() => auth.user)

async function handleRefresh() {
  refreshing.value = true
  try {
    await auth.refreshCurrentUser()
    ElMessage.success('资料已刷新')
  } catch {} finally { refreshing.value = false }
}
</script>
