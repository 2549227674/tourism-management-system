<template>
  <el-container style="min-height: 100vh;">
    <el-aside width="220px" class="admin-aside">
      <div class="admin-logo">后台管理</div>
      <el-menu :default-active="activeMenu" background-color="#181d26" text-color="#bfcbd9" active-text-color="#409eff" @select="handleMenuSelect">
        <el-menu-item index="/admin/dashboard">
          <el-icon><data-analysis /></el-icon>
          <span>后台首页</span>
        </el-menu-item>
        <el-menu-item index="/admin/spots">
          <el-icon><place /></el-icon>
          <span>景点管理</span>
        </el-menu-item>
        <el-menu-item index="routes">
          <el-icon><map-location /></el-icon>
          <span>线路管理</span>
        </el-menu-item>
        <el-menu-item index="orders">
          <el-icon><document /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="comments">
          <el-icon><chat-dot-round /></el-icon>
          <span>评论审核</span>
        </el-menu-item>
        <el-menu-item index="announcements">
          <el-icon><bell /></el-icon>
          <span>公告管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="admin-header">
        <span>管理员：{{ auth.user?.nickname || auth.user?.username }}</span>
        <el-button text @click="handleLogout">退出登录</el-button>
      </el-header>
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'
import { DataAnalysis, Place, MapLocation, Document, ChatDotRound, Bell } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const activeMenu = computed(() => route.path)

function handleMenuSelect(index) {
  if (index === '/admin/dashboard') {
    router.push('/admin/dashboard')
  } else if (index === '/admin/spots') {
    router.push('/admin/spots')
  } else {
    ElMessage.info('后续阶段实现')
  }
}

function handleLogout() {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-aside {
  background: #181d26;
}

.admin-logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  background: #0d1218;
}

.admin-header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 16px;
  border-bottom: 1px solid #dddddd;
  height: 64px;
}

.admin-main {
  background: #f8fafc;
  padding: 24px;
}
</style>
