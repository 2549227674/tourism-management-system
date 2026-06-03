<template>
  <div>
    <h2 class="section-title">景点管理</h2>

    <!-- 查询区 -->
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索景点名称" clearable style="width: 200px" @keyup.enter="handleSearch" />
      <el-select v-model="categoryId" placeholder="全部分类" clearable style="width: 160px" @change="handleFilterChange">
        <el-option label="全部分类" value="" />
        <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
      </el-select>
      <el-select v-model="status" placeholder="全部状态" clearable style="width: 120px" @change="handleFilterChange">
        <el-option label="全部状态" value="" />
        <el-option label="上架" value="ON" />
        <el-option label="下架" value="OFF" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <!-- 操作区 -->
    <div class="action-bar">
      <el-button type="primary" @click="openSpotDialog()">新增景点</el-button>
      <el-button @click="openCategoryDialog">分类管理</el-button>
    </div>

    <!-- 表格区 -->
    <el-table :data="spots" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="图片" width="80">
        <template #default="{ row }">
          <el-image v-if="row.imageUrl" :src="row.imageUrl" style="width: 50px; height: 50px" fit="cover" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名称" min-width="120" />
      <el-table-column label="分类" width="100">
        <template #default="{ row }">{{ row.categoryName || '-' }}</template>
      </el-table-column>
      <el-table-column prop="address" label="地址" min-width="120" show-overflow-tooltip />
      <el-table-column label="票价" width="80">
        <template #default="{ row }">¥{{ row.ticketPrice }}</template>
      </el-table-column>
      <el-table-column prop="openTime" label="开放时间" width="120" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 'ON' ? 'success' : 'info'" size="small">
            {{ row.status === 'ON' ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="160">
        <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openSpotDialog(row)">编辑</el-button>
          <el-button :type="row.status === 'ON' ? 'warning' : 'success'" link @click="handleToggleStatus(row)">
            {{ row.status === 'ON' ? '下架' : '上架' }}
          </el-button>
          <el-button type="danger" link @click="handleDeleteSpot(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-bar" v-if="total > pageSize">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="page" @current-change="loadSpots" />
    </div>

    <!-- 景点新增/编辑弹窗 -->
    <el-dialog v-model="spotDialogVisible" :title="spotForm.id ? '编辑景点' : '新增景点'" width="600px" destroy-on-close>
      <el-form :model="spotForm" :rules="spotRules" ref="spotFormRef" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="spotForm.name" placeholder="请输入景点名称" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="spotForm.categoryId" placeholder="请选择分类">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="spotForm.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="票价" prop="ticketPrice">
          <el-input-number v-model="spotForm.ticketPrice" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="开放时间">
          <el-input v-model="spotForm.openTime" placeholder="如 08:00-18:00" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="spotForm.introduction" type="textarea" :rows="3" placeholder="请输入简介" />
        </el-form-item>
        <el-form-item label="图片地址">
          <el-input v-model="spotForm.imageUrl" placeholder="请输入图片 URL" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="spotDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveSpot" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <!-- 分类管理弹窗 -->
    <el-dialog v-model="categoryDialogVisible" title="分类管理" width="600px" destroy-on-close>
      <div class="category-header">
        <el-button type="primary" size="small" @click="openCategoryForm()">新增分类</el-button>
      </div>
      <el-table :data="categoryList" v-loading="categoryLoading" stripe size="small">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openCategoryForm(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDeleteCategory(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分类表单内嵌 -->
      <el-dialog v-model="categoryFormVisible" :title="categoryForm.id ? '编辑分类' : '新增分类'" width="400px" append-to-body destroy-on-close>
        <el-form :model="categoryForm" :rules="categoryRules" ref="categoryFormRef" label-width="80px">
          <el-form-item label="名称" prop="name">
            <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
          </el-form-item>
          <el-form-item label="排序">
            <el-input-number v-model="categoryForm.sortOrder" :min="0" style="width: 200px" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="categoryFormVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveCategory" :loading="categorySaving">保存</el-button>
        </template>
      </el-dialog>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminSpots, createAdminSpot, updateAdminSpot, deleteAdminSpot, updateSpotStatus } from '../../api/adminSpotApi'
import { getAdminCategories, createAdminCategory, updateAdminCategory, deleteAdminCategory } from '../../api/adminCategoryApi'
import { getCategories } from '../../api/spotApi'
import { ElMessage, ElMessageBox } from 'element-plus'

// 景点列表
const spots = ref([])
const categories = ref([])
const keyword = ref('')
const categoryId = ref('')
const status = ref('')
const page = ref(1)
const pageSize = 10
const total = ref(0)
const loading = ref(false)

// 景点表单
const spotDialogVisible = ref(false)
const spotFormRef = ref(null)
const saving = ref(false)
const spotForm = ref({
  id: null,
  name: '',
  categoryId: null,
  address: '',
  ticketPrice: 0,
  openTime: '',
  introduction: '',
  imageUrl: ''
})
const spotRules = {
  name: [{ required: true, message: '请输入景点名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  ticketPrice: [{ required: true, message: '请输入票价', trigger: 'blur' }]
}

// 分类管理
const categoryDialogVisible = ref(false)
const categoryList = ref([])
const categoryLoading = ref(false)
const categoryFormVisible = ref(false)
const categoryFormRef = ref(null)
const categorySaving = ref(false)
const categoryForm = ref({ id: null, name: '', sortOrder: 0 })
const categoryRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

function formatTime(t) {
  return t ? t.replace('T', ' ').substring(0, 16) : ''
}

// 加载景点列表
async function loadSpots() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize }
    if (keyword.value) params.keyword = keyword.value
    if (categoryId.value) params.categoryId = categoryId.value
    if (status.value) params.status = status.value
    const data = await getAdminSpots(params)
    spots.value = data.records || []
    total.value = data.total || 0
  } catch {} finally { loading.value = false }
}

// 加载分类列表（筛选用）
async function loadCategories() {
  try {
    categories.value = await getCategories()
  } catch {}
}

// 搜索
function handleSearch() {
  page.value = 1
  loadSpots()
}

// 筛选变化
function handleFilterChange() {
  page.value = 1
  loadSpots()
}

// 重置
function handleReset() {
  keyword.value = ''
  categoryId.value = ''
  status.value = ''
  page.value = 1
  loadSpots()
}

// 打开景点弹窗
function openSpotDialog(row) {
  if (row) {
    spotForm.value = {
      id: row.id,
      name: row.name,
      categoryId: row.categoryId,
      address: row.address || '',
      ticketPrice: row.ticketPrice,
      openTime: row.openTime || '',
      introduction: row.introduction || '',
      imageUrl: row.imageUrl || ''
    }
  } else {
    spotForm.value = {
      id: null,
      name: '',
      categoryId: null,
      address: '',
      ticketPrice: 0,
      openTime: '',
      introduction: '',
      imageUrl: ''
    }
  }
  spotDialogVisible.value = true
}

// 保存景点
async function handleSaveSpot() {
  try {
    await spotFormRef.value.validate()
  } catch { return }
  saving.value = true
  try {
    const { id, ...data } = spotForm.value
    if (id) {
      await updateAdminSpot(id, data)
      ElMessage.success('修改成功')
    } else {
      await createAdminSpot(data)
      ElMessage.success('新增成功')
    }
    spotDialogVisible.value = false
    loadSpots()
  } catch {} finally { saving.value = false }
}

// 切换状态
async function handleToggleStatus(row) {
  const newStatus = row.status === 'ON' ? 'OFF' : 'ON'
  const label = newStatus === 'ON' ? '上架' : '下架'
  try {
    await ElMessageBox.confirm(`确定${label}该景点吗？`, '确认', { type: 'warning' })
  } catch { return }
  try {
    await updateSpotStatus(row.id, newStatus)
    ElMessage.success(`${label}成功`)
    loadSpots()
  } catch {}
}

// 删除景点
async function handleDeleteSpot(row) {
  try {
    await ElMessageBox.confirm('确定删除该景点吗？', '确认删除', { type: 'warning' })
  } catch { return }
  try {
    await deleteAdminSpot(row.id)
    ElMessage.success('删除成功')
    loadSpots()
  } catch {}
}

// 打开分类管理弹窗
async function openCategoryDialog() {
  categoryDialogVisible.value = true
  await loadCategoryList()
}

// 加载分类列表（管理用）
async function loadCategoryList() {
  categoryLoading.value = true
  try {
    categoryList.value = await getAdminCategories()
  } catch {} finally { categoryLoading.value = false }
}

// 打开分类表单
function openCategoryForm(row) {
  if (row) {
    categoryForm.value = { id: row.id, name: row.name, sortOrder: row.sortOrder || 0 }
  } else {
    categoryForm.value = { id: null, name: '', sortOrder: 0 }
  }
  categoryFormVisible.value = true
}

// 保存分类
async function handleSaveCategory() {
  try {
    await categoryFormRef.value.validate()
  } catch { return }
  categorySaving.value = true
  try {
    const { id, ...data } = categoryForm.value
    if (id) {
      await updateAdminCategory(id, data)
      ElMessage.success('修改成功')
    } else {
      await createAdminCategory(data)
      ElMessage.success('新增成功')
    }
    categoryFormVisible.value = false
    loadCategoryList()
    loadCategories()
  } catch {} finally { categorySaving.value = false }
}

// 删除分类
async function handleDeleteCategory(row) {
  try {
    await ElMessageBox.confirm(`确定删除分类"${row.name}"吗？`, '确认删除', { type: 'warning' })
  } catch { return }
  try {
    await deleteAdminCategory(row.id)
    ElMessage.success('删除成功')
    loadCategoryList()
    loadCategories()
  } catch {}
}

onMounted(() => {
  loadCategories()
  loadSpots()
})
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

.category-header {
  margin-bottom: 12px;
  display: flex;
  justify-content: flex-end;
}
</style>
