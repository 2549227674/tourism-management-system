<template>
  <div class="page-container">
    <h2 class="section-title">我的订单</h2>
    <div class="filter-bar">
      <el-select v-model="status" placeholder="全部状态" clearable style="width: 160px" @change="loadData">
        <el-option label="全部" value="" />
        <el-option label="待处理" value="PENDING" />
        <el-option label="已确认" value="CONFIRMED" />
        <el-option label="已取消" value="CANCELLED" />
        <el-option label="已驳回" value="REJECTED" />
        <el-option label="已完成" value="COMPLETED" />
      </el-select>
    </div>

    <el-empty v-if="!loading && orders.length === 0" description="暂无订单" />
    <el-table v-else :data="orders" v-loading="loading" stripe>
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="routeName" label="线路名称" />
      <el-table-column prop="peopleCount" label="人数" width="80" />
      <el-table-column prop="contactName" label="联系人" width="100" />
      <el-table-column prop="contactPhone" label="联系电话" width="130" />
      <el-table-column label="总金额" width="100">
        <template #default="{ row }">¥{{ row.totalAmount }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="170">
        <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button v-if="row.status === 'PENDING' || row.status === 'CONFIRMED'" type="danger" link @click="handleCancel(row)">取消订单</el-button>
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
import { getMyOrders, cancelOrder } from '../api/orderApi'
import { ElMessage, ElMessageBox } from 'element-plus'

const orders = ref([])
const status = ref('')
const page = ref(1)
const pageSize = 10
const total = ref(0)
const loading = ref(false)

const statusMap = { PENDING: '待处理', CONFIRMED: '已确认', CANCELLED: '已取消', REJECTED: '已驳回', COMPLETED: '已完成' }
const statusTagMap = { PENDING: 'warning', CONFIRMED: 'success', CANCELLED: 'info', REJECTED: 'danger', COMPLETED: '' }

function statusLabel(s) { return statusMap[s] || s }
function statusTagType(s) { return statusTagMap[s] || 'info' }
function formatTime(t) { return t ? t.replace('T', ' ').substring(0, 16) : '' }

async function loadData() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize }
    if (status.value) params.status = status.value
    const data = await getMyOrders(params)
    orders.value = data.records || []
    total.value = data.total || 0
  } catch {} finally { loading.value = false }
}

async function handleCancel(row) {
  try {
    await ElMessageBox.confirm('确定取消该订单？取消后名额将释放。', '确认取消', { type: 'warning' })
  } catch { return }
  try {
    await cancelOrder(row.id)
    ElMessage.success('取消成功')
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
