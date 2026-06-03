<template>
  <div>
    <h2 class="section-title">评论审核</h2>

    <!-- 查询区 -->
    <div class="filter-bar">
      <el-select v-model="targetType" placeholder="全部类型" clearable style="width: 120px" @change="handleFilterChange">
        <el-option label="全部类型" value="" />
        <el-option label="景点" value="SPOT" />
        <el-option label="线路" value="ROUTE" />
      </el-select>
      <el-input v-model="targetId" placeholder="目标ID" clearable style="width: 120px" @keyup.enter="handleSearch" />
      <el-select v-model="status" placeholder="全部状态" clearable style="width: 120px" @change="handleFilterChange">
        <el-option label="全部状态" value="" />
        <el-option label="待审核" value="PENDING" />
        <el-option label="已通过" value="APPROVED" />
        <el-option label="已驳回" value="REJECTED" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <!-- 表格区 -->
    <el-table :data="comments" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="用户" width="100" />
      <el-table-column label="目标类型" width="80">
        <template #default="{ row }">{{ targetTypeLabel(row.targetType) }}</template>
      </el-table-column>
      <el-table-column prop="targetName" label="目标名称" min-width="120" show-overflow-tooltip />
      <el-table-column prop="targetId" label="目标ID" width="80" />
      <el-table-column label="评分" width="140">
        <template #default="{ row }">
          <el-rate :model-value="row.rating" disabled size="small" />
        </template>
      </el-table-column>
      <el-table-column prop="content" label="评论内容" min-width="150" show-overflow-tooltip />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="auditRemark" label="审核备注" width="120" show-overflow-tooltip />
      <el-table-column label="创建时间" width="160">
        <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleViewDetail(row)">查看</el-button>
          <el-button v-if="row.status === 'PENDING'" type="success" link @click="openAuditDialog(row, 'APPROVED')">通过</el-button>
          <el-button v-if="row.status === 'PENDING'" type="danger" link @click="openAuditDialog(row, 'REJECTED')">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-bar" v-if="total > pageSize">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="page" @current-change="loadComments" />
    </div>

    <!-- 评论详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="评论详情" width="600px" destroy-on-close>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="评论ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ detail.userId }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ detail.username }}</el-descriptions-item>
        <el-descriptions-item label="目标类型">{{ targetTypeLabel(detail.targetType) }}</el-descriptions-item>
        <el-descriptions-item label="目标ID">{{ detail.targetId }}</el-descriptions-item>
        <el-descriptions-item label="目标名称">{{ detail.targetName }}</el-descriptions-item>
        <el-descriptions-item label="评分">
          <el-rate :model-value="detail.rating" disabled size="small" />
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(detail.status)" size="small">{{ statusLabel(detail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="评论内容" :span="2">{{ detail.content }}</el-descriptions-item>
        <el-descriptions-item label="审核备注" :span="2">{{ detail.auditRemark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ formatTime(detail.createdAt) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 审核弹窗 -->
    <el-dialog v-model="auditDialogVisible" :title="auditDialogTitle" width="400px" destroy-on-close>
      <el-input v-model="auditRemark" type="textarea" :rows="3" :placeholder="auditPlaceholder" />
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAuditSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminComments, auditAdminComment } from '../../api/adminCommentApi'
import { ElMessage, ElMessageBox } from 'element-plus'

const comments = ref([])
const targetType = ref('')
const targetId = ref('')
const status = ref('')
const page = ref(1)
const pageSize = 10
const total = ref(0)
const loading = ref(false)

const detailDialogVisible = ref(false)
const detail = ref({})

const auditDialogVisible = ref(false)
const auditDialogTitle = ref('')
const auditPlaceholder = ref('')
const auditRemark = ref('')
const auditTarget = ref(null)
const auditStatus = ref('')

const targetTypeMap = { SPOT: '景点', ROUTE: '线路' }
const statusMap = { PENDING: '待审核', APPROVED: '已通过', REJECTED: '已驳回' }
const statusTagMap = { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger' }

function targetTypeLabel(s) { return targetTypeMap[s] || s }
function statusLabel(s) { return statusMap[s] || s }
function statusTagType(s) { return statusTagMap[s] || 'info' }
function formatTime(t) { return t ? t.replace('T', ' ').substring(0, 16) : '' }

async function loadComments() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize }
    if (targetType.value) params.targetType = targetType.value
    if (targetId.value) params.targetId = targetId.value
    if (status.value) params.status = status.value
    const data = await getAdminComments(params)
    comments.value = data.records || []
    total.value = data.total || 0
  } catch {} finally { loading.value = false }
}

function handleSearch() {
  page.value = 1
  loadComments()
}

function handleFilterChange() {
  page.value = 1
  loadComments()
}

function handleReset() {
  targetType.value = ''
  targetId.value = ''
  status.value = ''
  page.value = 1
  loadComments()
}

function handleViewDetail(row) {
  detail.value = { ...row }
  detailDialogVisible.value = true
}

function openAuditDialog(row, auditStatusValue) {
  auditTarget.value = row
  auditStatus.value = auditStatusValue
  auditRemark.value = ''
  if (auditStatusValue === 'APPROVED') {
    auditDialogTitle.value = '通过评论'
    auditPlaceholder.value = '请输入审核备注（可选）'
  } else {
    auditDialogTitle.value = '驳回评论'
    auditPlaceholder.value = '请输入驳回原因（可选）'
  }
  auditDialogVisible.value = true
}

async function handleAuditSubmit() {
  const confirmText = auditStatus.value === 'APPROVED' ? '确定通过该评论吗？' : '确定驳回该评论吗？'
  try {
    await ElMessageBox.confirm(confirmText, '确认', { type: 'warning' })
  } catch { return }
  try {
    const data = { status: auditStatus.value }
    if (auditRemark.value) data.auditRemark = auditRemark.value
    await auditAdminComment(auditTarget.value.id, data)
    ElMessage.success(auditStatus.value === 'APPROVED' ? '审核通过' : '审核驳回')
    auditDialogVisible.value = false
    loadComments()
  } catch {}
}

onMounted(() => loadComments())
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
