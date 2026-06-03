<template>
  <div>
    <h2 class="section-title">公告管理</h2>

    <!-- 查询区 -->
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索公告标题" clearable style="width: 200px" @keyup.enter="handleSearch" />
      <el-select v-model="status" placeholder="全部状态" clearable style="width: 120px" @change="handleFilterChange">
        <el-option label="全部状态" value="" />
        <el-option label="草稿" value="DRAFT" />
        <el-option label="已发布" value="PUBLISHED" />
        <el-option label="已下架" value="OFFLINE" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <!-- 操作区 -->
    <div class="action-bar">
      <el-button type="primary" @click="openFormDialog()">新增公告</el-button>
    </div>

    <!-- 表格区 -->
    <el-table :data="announcements" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" min-width="150" show-overflow-tooltip />
      <el-table-column prop="content" label="内容摘要" min-width="200" show-overflow-tooltip />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发布时间" width="160">
        <template #default="{ row }">{{ formatTime(row.publishTime) }}</template>
      </el-table-column>
      <el-table-column label="创建时间" width="160">
        <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="更新时间" width="160">
        <template #default="{ row }">{{ formatTime(row.updatedAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleViewDetail(row)">查看</el-button>
          <el-button type="primary" link @click="openFormDialog(row)">编辑</el-button>
          <el-button v-if="row.status !== 'PUBLISHED'" type="success" link @click="handlePublish(row)">发布</el-button>
          <el-button v-if="row.status === 'PUBLISHED'" type="warning" link @click="handleOffline(row)">下架</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-bar" v-if="total > pageSize">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="page" @current-change="loadAnnouncements" />
    </div>

    <!-- 公告详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="公告详情" width="600px" destroy-on-close>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="公告ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(detail.status)" size="small">{{ statusLabel(detail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ detail.title }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">{{ detail.content }}</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ formatTime(detail.publishTime) }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatTime(detail.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间" :span="2">{{ formatTime(detail.updatedAt) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 公告表单弹窗 -->
    <el-dialog v-model="formDialogVisible" :title="form.id ? '编辑公告' : '新增公告'" width="600px" destroy-on-close>
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入公告内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminAnnouncements, getAdminAnnouncementDetail, createAdminAnnouncement, updateAdminAnnouncement, publishAnnouncement, offlineAnnouncement, deleteAdminAnnouncement } from '../../api/adminAnnouncementApi'
import { ElMessage, ElMessageBox } from 'element-plus'

const announcements = ref([])
const keyword = ref('')
const status = ref('')
const page = ref(1)
const pageSize = 10
const total = ref(0)
const loading = ref(false)

const detailDialogVisible = ref(false)
const detail = ref({})

const formDialogVisible = ref(false)
const formRef = ref(null)
const saving = ref(false)
const form = ref({ id: null, title: '', content: '' })

const formRules = {
  title: [
    { required: true, message: '公告标题不能为空', trigger: 'blur' },
    { max: 100, message: '公告标题不能超过 100 字', trigger: 'blur' }
  ],
  content: [{ required: true, message: '公告内容不能为空', trigger: 'blur' }]
}

const statusMap = { DRAFT: '草稿', PUBLISHED: '已发布', OFFLINE: '已下架' }
const statusTagMap = { DRAFT: 'info', PUBLISHED: 'success', OFFLINE: 'warning' }

function statusLabel(s) { return statusMap[s] || s }
function statusTagType(s) { return statusTagMap[s] || 'info' }
function formatTime(t) { return t ? t.replace('T', ' ').substring(0, 16) : '-' }

async function loadAnnouncements() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize }
    if (keyword.value) params.keyword = keyword.value
    if (status.value) params.status = status.value
    const data = await getAdminAnnouncements(params)
    announcements.value = data.records || []
    total.value = data.total || 0
  } catch {} finally { loading.value = false }
}

function handleSearch() {
  page.value = 1
  loadAnnouncements()
}

function handleFilterChange() {
  page.value = 1
  loadAnnouncements()
}

function handleReset() {
  keyword.value = ''
  status.value = ''
  page.value = 1
  loadAnnouncements()
}

async function handleViewDetail(row) {
  try {
    detail.value = await getAdminAnnouncementDetail(row.id)
    detailDialogVisible.value = true
  } catch {}
}

function openFormDialog(row) {
  if (row) {
    form.value = { id: row.id, title: row.title, content: row.content }
  } else {
    form.value = { id: null, title: '', content: '' }
  }
  formDialogVisible.value = true
}

async function handleSave() {
  try {
    await formRef.value.validate()
  } catch { return }
  saving.value = true
  try {
    const { id, ...data } = form.value
    if (id) {
      await updateAdminAnnouncement(id, data)
      ElMessage.success('修改成功')
    } else {
      await createAdminAnnouncement(data)
      ElMessage.success('新增成功')
    }
    formDialogVisible.value = false
    loadAnnouncements()
  } catch {} finally { saving.value = false }
}

async function handlePublish(row) {
  try {
    await ElMessageBox.confirm('确定发布该公告吗？', '确认', { type: 'warning' })
  } catch { return }
  try {
    await publishAnnouncement(row.id)
    ElMessage.success('发布成功')
    loadAnnouncements()
  } catch {}
}

async function handleOffline(row) {
  try {
    await ElMessageBox.confirm('确定下架该公告吗？', '确认', { type: 'warning' })
  } catch { return }
  try {
    await offlineAnnouncement(row.id)
    ElMessage.success('下架成功')
    loadAnnouncements()
  } catch {}
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定删除该公告吗？', '确认删除', { type: 'warning' })
  } catch { return }
  try {
    await deleteAdminAnnouncement(row.id)
    ElMessage.success('删除成功')
    loadAnnouncements()
  } catch {}
}

onMounted(() => loadAnnouncements())
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
