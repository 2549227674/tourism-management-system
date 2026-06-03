<template>
  <div>
    <h2 class="section-title">订单管理</h2>

    <!-- 查询区 -->
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="订单号/联系人/电话" clearable style="width: 200px" @keyup.enter="handleSearch" />
      <el-select v-model="status" placeholder="全部状态" clearable style="width: 140px" @change="handleFilterChange">
        <el-option label="全部状态" value="" />
        <el-option label="待处理" value="PENDING" />
        <el-option label="已确认" value="CONFIRMED" />
        <el-option label="已取消" value="CANCELLED" />
        <el-option label="已驳回" value="REJECTED" />
        <el-option label="已完成" value="COMPLETED" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <!-- 表格区 -->
    <el-table :data="orders" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="orderNo" label="订单号" width="180" show-overflow-tooltip />
      <el-table-column prop="routeName" label="线路名称" min-width="120" show-overflow-tooltip />
      <el-table-column prop="userId" label="用户ID" width="80" />
      <el-table-column prop="peopleCount" label="人数" width="70" />
      <el-table-column prop="contactName" label="联系人" width="90" />
      <el-table-column prop="contactPhone" label="联系电话" width="120" />
      <el-table-column label="总金额" width="90">
        <template #default="{ row }">¥{{ row.totalAmount }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" width="120" show-overflow-tooltip />
      <el-table-column label="创建时间" width="160">
        <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleViewDetail(row)">查看</el-button>
          <el-button v-if="row.status === 'PENDING'" type="success" link @click="handleConfirm(row)">确认</el-button>
          <el-button v-if="row.status === 'PENDING'" type="danger" link @click="openRemarkDialog(row, 'reject')">驳回</el-button>
          <el-button v-if="row.status === 'CONFIRMED'" type="primary" link @click="handleComplete(row)">完成</el-button>
          <el-button v-if="row.status === 'PENDING' || row.status === 'CONFIRMED'" type="warning" link @click="openRemarkDialog(row, 'cancel')">取消</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-bar" v-if="total > pageSize">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="page" @current-change="loadOrders" />
    </div>

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="订单详情" width="600px" destroy-on-close>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ detail.userId }}</el-descriptions-item>
        <el-descriptions-item label="线路ID">{{ detail.routeId }}</el-descriptions-item>
        <el-descriptions-item label="线路名称" :span="2">{{ detail.routeName }}</el-descriptions-item>
        <el-descriptions-item label="预订人数">{{ detail.peopleCount }}</el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ detail.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ detail.contactName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detail.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(detail.status)" size="small">{{ statusLabel(detail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatTime(detail.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 驳回/取消备注弹窗 -->
    <el-dialog v-model="remarkDialogVisible" :title="remarkDialogTitle" width="400px" destroy-on-close>
      <el-input v-model="remark" type="textarea" :rows="3" placeholder="请输入备注（可选）" />
      <template #footer>
        <el-button @click="remarkDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRemarkSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getAdminOrders, getAdminOrderDetail, confirmAdminOrder, rejectAdminOrder, completeAdminOrder, cancelAdminOrder } from '../../api/adminOrderApi'
import { ElMessage, ElMessageBox } from 'element-plus'

const orders = ref([])
const keyword = ref('')
const status = ref('')
const page = ref(1)
const pageSize = 10
const total = ref(0)
const loading = ref(false)

const detailDialogVisible = ref(false)
const detail = ref({})

const remarkDialogVisible = ref(false)
const remarkDialogTitle = ref('')
const remark = ref('')
const remarkTarget = ref(null)
const remarkAction = ref('')

const statusMap = { PENDING: '待处理', CONFIRMED: '已确认', CANCELLED: '已取消', REJECTED: '已驳回', COMPLETED: '已完成' }
const statusTagMap = { PENDING: 'warning', CONFIRMED: 'success', CANCELLED: 'info', REJECTED: 'danger', COMPLETED: '' }

function statusLabel(s) { return statusMap[s] || s }
function statusTagType(s) { return statusTagMap[s] || 'info' }
function formatTime(t) { return t ? t.replace('T', ' ').substring(0, 16) : '' }

async function loadOrders() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize }
    if (keyword.value) params.keyword = keyword.value
    if (status.value) params.status = status.value
    const data = await getAdminOrders(params)
    orders.value = data.records || []
    total.value = data.total || 0
  } catch {} finally { loading.value = false }
}

function handleSearch() {
  page.value = 1
  loadOrders()
}

function handleFilterChange() {
  page.value = 1
  loadOrders()
}

function handleReset() {
  keyword.value = ''
  status.value = ''
  page.value = 1
  loadOrders()
}

async function handleViewDetail(row) {
  try {
    detail.value = await getAdminOrderDetail(row.id)
    detailDialogVisible.value = true
  } catch {}
}

async function handleConfirm(row) {
  try {
    await ElMessageBox.confirm('确定确认该订单吗？', '确认', { type: 'warning' })
  } catch { return }
  try {
    await confirmAdminOrder(row.id)
    ElMessage.success('确认成功')
    loadOrders()
  } catch {}
}

async function handleComplete(row) {
  try {
    await ElMessageBox.confirm('确定将该订单标记为完成吗？', '确认', { type: 'warning' })
  } catch { return }
  try {
    await completeAdminOrder(row.id)
    ElMessage.success('完成成功')
    loadOrders()
  } catch {}
}

function openRemarkDialog(row, action) {
  remarkTarget.value = row
  remarkAction.value = action
  remark.value = ''
  remarkDialogTitle.value = action === 'reject' ? '驳回订单' : '取消订单'
  remarkDialogVisible.value = true
}

async function handleRemarkSubmit() {
  const action = remarkAction.value
  const row = remarkTarget.value
  const confirmText = action === 'reject' ? '确定驳回该订单吗？' : '确定取消该订单吗？'
  try {
    await ElMessageBox.confirm(confirmText, '确认', { type: 'warning' })
  } catch { return }
  try {
    const data = remark.value ? { remark: remark.value } : {}
    if (action === 'reject') {
      await rejectAdminOrder(row.id, data)
      ElMessage.success('驳回成功')
    } else {
      await cancelAdminOrder(row.id, data)
      ElMessage.success('取消成功')
    }
    remarkDialogVisible.value = false
    loadOrders()
  } catch {}
}

onMounted(() => loadOrders())
</script>

<style scoped>
.filter-bar {
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
