<template>
  <div class="page-container">
    <el-skeleton v-if="loading" :rows="8" animated />
    <template v-else-if="route">
      <div class="detail-section">
        <h1>{{ route.name }}</h1>
        <div class="detail-meta">
          <span>出发时间：{{ formatTime(route.departureTime) }}</span>
          <span style="margin-left: 20px">
            <el-tag :type="route.status === 'OPEN' ? 'success' : 'info'" size="small">
              {{ route.status === 'OPEN' ? '开放预订' : route.status === 'FULL' ? '名额已满' : route.status }}
            </el-tag>
          </span>
        </div>
        <el-descriptions :column="2" border style="margin-bottom: 20px">
          <el-descriptions-item label="价格">¥{{ route.price }}</el-descriptions-item>
          <el-descriptions-item label="总名额">{{ route.quota }}</el-descriptions-item>
          <el-descriptions-item label="已预订">{{ route.bookedCount }}</el-descriptions-item>
          <el-descriptions-item label="剩余名额">{{ remaining }}</el-descriptions-item>
        </el-descriptions>
        <h3 style="margin-bottom: 10px">行程安排</h3>
        <div class="detail-content">{{ route.itinerary }}</div>
      </div>
      <div class="detail-section" style="text-align: center; display: flex; gap: 16px; justify-content: center">
        <el-button type="primary" size="large" :disabled="route.status !== 'OPEN'" @click="handleBook">
          {{ route.status === 'OPEN' ? '立即预订' : route.status === 'FULL' ? '名额已满' : '暂不可预订' }}
        </el-button>
        <el-button size="large" @click="handleFavorite">收藏线路</el-button>
      </div>
    </template>
    <el-empty v-else description="线路不存在" />

    <el-dialog v-model="dialogVisible" title="预订确认" width="480px" :close-on-click-modal="false">
      <el-form :model="bookForm" :rules="bookRules" ref="bookFormRef" label-width="80px">
        <el-form-item label="线路">
          <span>{{ route?.name }}</span>
        </el-form-item>
        <el-form-item label="单价">
          <span>¥{{ route?.price }}</span>
        </el-form-item>
        <el-form-item label="预订人数" prop="peopleCount">
          <el-input-number v-model="bookForm.peopleCount" :min="1" :max="remaining" />
        </el-form-item>
        <el-form-item label="预计金额">
          <span style="font-weight: 600; color: #ff385c">¥{{ estimatedAmount }}</span>
        </el-form-item>
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="bookForm.contactName" placeholder="请输入联系人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="bookForm.contactPhone" placeholder="请输入手机号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitOrder">确认预订</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getRouteDetail } from '../api/routeApi'
import { createOrder } from '../api/orderApi'
import { addFavorite } from '../api/favoriteApi'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'

const routeParam = useRoute()
const router = useRouter()
const auth = useAuthStore()
const route = ref(null)
const loading = ref(true)

const dialogVisible = ref(false)
const submitting = ref(false)
const bookFormRef = ref()
const bookForm = reactive({ peopleCount: 1, contactName: '', contactPhone: '' })

const remaining = computed(() => (route.value ? route.value.quota - route.value.bookedCount : 0))
const estimatedAmount = computed(() => (route.value ? route.value.price * bookForm.peopleCount : 0))

const phoneValidator = (rule, value, callback) => {
  if (!/^1[3-9]\d{9}$/.test(value)) callback(new Error('请输入正确的手机号'))
  else callback()
}

const bookRules = {
  peopleCount: [{ required: true, message: '请输入预订人数', trigger: 'change' }],
  contactName: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }, { validator: phoneValidator, trigger: 'blur' }]
}

function formatTime(t) {
  return t ? t.replace('T', ' ').substring(0, 16) : ''
}

function handleBook() {
  if (!auth.isLoggedIn()) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  if (route.value.status !== 'OPEN') {
    ElMessage.warning('当前线路不可预订')
    return
  }
  bookForm.peopleCount = 1
  bookForm.contactName = auth.user?.nickname || auth.user?.username || ''
  bookForm.contactPhone = auth.user?.phone || ''
  dialogVisible.value = true
}

async function submitOrder() {
  try { await bookFormRef.value.validate() } catch { return }
  submitting.value = true
  try {
    await createOrder({
      routeId: route.value.id,
      peopleCount: bookForm.peopleCount,
      contactName: bookForm.contactName,
      contactPhone: bookForm.contactPhone
    })
    ElMessage.success('预订成功，等待管理员处理')
    dialogVisible.value = false
    router.push('/my/orders')
  } catch {} finally { submitting.value = false }
}

async function handleFavorite() {
  if (!auth.isLoggedIn()) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await addFavorite({ targetType: 'ROUTE', targetId: route.value.id })
    ElMessage.success('收藏成功')
  } catch {}
}

onMounted(async () => {
  try {
    route.value = await getRouteDetail(routeParam.params.id)
  } catch {} finally { loading.value = false }
})
</script>
