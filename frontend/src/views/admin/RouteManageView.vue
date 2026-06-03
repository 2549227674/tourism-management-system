<template>
  <div>
    <h2 class="section-title">线路管理</h2>

    <!-- 查询区 -->
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索线路名称" clearable style="width: 200px" @keyup.enter="handleSearch" />
      <el-select v-model="status" placeholder="全部状态" clearable style="width: 140px" @change="handleFilterChange">
        <el-option label="全部状态" value="" />
        <el-option label="草稿" value="DRAFT" />
        <el-option label="开放预订" value="OPEN" />
        <el-option label="名额已满" value="FULL" />
        <el-option label="已关闭" value="CLOSED" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <!-- 操作区 -->
    <div class="action-bar">
      <el-button type="primary" @click="openRouteDialog()">新增线路</el-button>
    </div>

    <!-- 表格区 -->
    <el-table :data="routes" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="封面" width="80">
        <template #default="{ row }">
          <el-image v-if="row.coverImageUrl" :src="row.coverImageUrl" style="width: 50px; height: 50px" fit="cover" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="线路名称" min-width="120" />
      <el-table-column label="价格" width="80">
        <template #default="{ row }">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column label="出发时间" width="160">
        <template #default="{ row }">{{ formatTime(row.departureTime) }}</template>
      </el-table-column>
      <el-table-column label="名额" width="80">
        <template #default="{ row }">{{ row.quota }}</template>
      </el-table-column>
      <el-table-column label="已预订" width="80">
        <template #default="{ row }">{{ row.bookedCount }}</template>
      </el-table-column>
      <el-table-column label="剩余" width="80">
        <template #default="{ row }">{{ row.quota - row.bookedCount }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="160">
        <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openRouteDialog(row)">编辑</el-button>
          <el-dropdown @command="(cmd) => handleStatusChange(row, cmd)">
            <el-button type="warning" link>状态</el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="DRAFT">设为草稿</el-dropdown-item>
                <el-dropdown-item command="OPEN">开放预订</el-dropdown-item>
                <el-dropdown-item command="FULL">设为已满</el-dropdown-item>
                <el-dropdown-item command="CLOSED">关闭</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-button type="danger" link @click="handleDeleteRoute(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-bar" v-if="total > pageSize">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="page" @current-change="loadRoutes" />
    </div>

    <!-- 线路新增/编辑弹窗 -->
    <el-dialog v-model="routeDialogVisible" :title="routeForm.id ? '编辑线路' : '新增线路'" width="600px" destroy-on-close>
      <el-form :model="routeForm" :rules="routeRules" ref="routeFormRef" label-width="100px">
        <el-form-item label="线路名称" prop="name">
          <el-input v-model="routeForm.name" placeholder="请输入线路名称" />
        </el-form-item>
        <el-form-item label="行程安排" prop="itinerary">
          <el-input v-model="routeForm.itinerary" type="textarea" :rows="4" placeholder="请输入行程安排" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="routeForm.price" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="出发时间" prop="departureTime">
          <el-date-picker v-model="routeForm.departureTime" type="datetime" placeholder="选择出发时间" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 240px" />
        </el-form-item>
        <el-form-item label="名额" prop="quota">
          <el-input-number v-model="routeForm.quota" :min="1" style="width: 200px" />
        </el-form-item>
        <el-form-item label="已预订" prop="bookedCount">
          <el-input-number v-model="routeForm.bookedCount" :min="0" style="width: 200px" />
        </el-form-item>
        <el-form-item label="封面图地址">
          <el-input v-model="routeForm.coverImageUrl" placeholder="请输入封面图 URL" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="routeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveRoute" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminRoutes, createAdminRoute, updateAdminRoute, deleteAdminRoute, updateRouteStatus } from '../../api/adminRouteApi'
import { ElMessage, ElMessageBox } from 'element-plus'

const routes = ref([])
const keyword = ref('')
const status = ref('')
const page = ref(1)
const pageSize = 10
const total = ref(0)
const loading = ref(false)

const routeDialogVisible = ref(false)
const routeFormRef = ref(null)
const saving = ref(false)
const routeForm = ref({
  id: null,
  name: '',
  itinerary: '',
  price: 0,
  departureTime: '',
  quota: 1,
  bookedCount: 0,
  coverImageUrl: ''
})

const routeRules = {
  name: [{ required: true, message: '请输入线路名称', trigger: 'blur' }],
  itinerary: [{ required: true, message: '请输入行程安排', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  departureTime: [{ required: true, message: '请选择出发时间', trigger: 'change' }],
  quota: [{ required: true, message: '请输入名额', trigger: 'blur' }]
}

const statusMap = { DRAFT: '草稿', OPEN: '开放预订', FULL: '名额已满', CLOSED: '已关闭' }
const statusTagMap = { DRAFT: 'info', OPEN: 'success', FULL: 'warning', CLOSED: 'danger' }

function statusLabel(s) { return statusMap[s] || s }
function statusTagType(s) { return statusTagMap[s] || 'info' }
function formatTime(t) { return t ? t.replace('T', ' ').substring(0, 16) : '' }

async function loadRoutes() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize }
    if (keyword.value) params.keyword = keyword.value
    if (status.value) params.status = status.value
    const data = await getAdminRoutes(params)
    routes.value = data.records || []
    total.value = data.total || 0
  } catch {} finally { loading.value = false }
}

function handleSearch() {
  page.value = 1
  loadRoutes()
}

function handleFilterChange() {
  page.value = 1
  loadRoutes()
}

function handleReset() {
  keyword.value = ''
  status.value = ''
  page.value = 1
  loadRoutes()
}

function openRouteDialog(row) {
  if (row) {
    routeForm.value = {
      id: row.id,
      name: row.name,
      itinerary: row.itinerary || '',
      price: row.price,
      departureTime: row.departureTime || '',
      quota: row.quota,
      bookedCount: row.bookedCount || 0,
      coverImageUrl: row.coverImageUrl || ''
    }
  } else {
    routeForm.value = {
      id: null,
      name: '',
      itinerary: '',
      price: 0,
      departureTime: '',
      quota: 1,
      bookedCount: 0,
      coverImageUrl: ''
    }
  }
  routeDialogVisible.value = true
}

async function handleSaveRoute() {
  try {
    await routeFormRef.value.validate()
  } catch { return }
  saving.value = true
  try {
    const { id, ...data } = routeForm.value
    if (id) {
      await updateAdminRoute(id, data)
      ElMessage.success('修改成功')
    } else {
      await createAdminRoute(data)
      ElMessage.success('新增成功')
    }
    routeDialogVisible.value = false
    loadRoutes()
  } catch {} finally { saving.value = false }
}

async function handleStatusChange(row, cmd) {
  const label = statusMap[cmd] || cmd
  try {
    await ElMessageBox.confirm(`确定将该线路状态改为${label}吗？`, '确认', { type: 'warning' })
  } catch { return }
  try {
    await updateRouteStatus(row.id, cmd)
    ElMessage.success('状态修改成功')
    loadRoutes()
  } catch {}
}

async function handleDeleteRoute(row) {
  try {
    await ElMessageBox.confirm('确定删除该线路吗？', '确认删除', { type: 'warning' })
  } catch { return }
  try {
    await deleteAdminRoute(row.id)
    ElMessage.success('删除成功')
    loadRoutes()
  } catch {}
}

onMounted(() => loadRoutes())
</script>

<style scoped>
.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.action-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.pagination-bar {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
