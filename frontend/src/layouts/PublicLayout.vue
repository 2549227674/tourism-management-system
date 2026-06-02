<template>
  <el-container>
    <el-header class="public-header">
      <div class="header-content">
        <div class="header-left">
          <router-link to="/" class="logo">旅游管理系统</router-link>
          <el-menu mode="horizontal" :default-active="activeMenu" :ellipsis="false" router>
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/spots">景点</el-menu-item>
            <el-menu-item index="/routes">线路</el-menu-item>
            <el-menu-item index="/announcements">公告</el-menu-item>
          </el-menu>
        </div>
        <div class="header-right">
          <template v-if="auth.isLoggedIn()">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                {{ auth.user?.nickname || auth.user?.username }}
                <el-icon><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="auth.isAdmin()" command="admin">后台管理</el-dropdown-item>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary" @click="$router.push('/login')">登录</el-button>
            <el-button @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </el-header>
    <el-main class="public-main">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { ArrowDown } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const activeMenu = computed(() => {
  const path = route.path
  if (path === '/') return '/'
  if (path.startsWith('/spots')) return '/spots'
  if (path.startsWith('/routes')) return '/routes'
  if (path.startsWith('/announcements')) return '/announcements'
  return '/'
})

function handleCommand(cmd) {
  if (cmd === 'admin') router.push('/admin/dashboard')
  else if (cmd === 'logout') {
    auth.logout()
    router.push('/')
  }
}
</script>

<style scoped>
.public-header {
  background: #fff;
  box-shadow: 0 1px 0 #dddddd;
  padding: 0;
  height: 80px;
}

.header-content {
  max-width: 1280px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 32px;
}

.logo {
  font-size: 22px;
  font-weight: 600;
  color: #ff385c;
  text-decoration: none;
  white-space: nowrap;
}

.header-left .el-menu {
  border-bottom: none;
  background: transparent;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 600;
  color: #222222;
}

.public-main {
  min-height: calc(100vh - 80px);
  background: #ffffff;
}
</style>
