<template>
  <div class="rotations-page">
    <div class="page-header">
      <h2 class="page-title">轮播图管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>添加轮播图
      </el-button>
    </div>
    
    <!-- 轮播图列表 -->
    <el-table :data="rotations" stripe v-loading="loading">
      <el-table-column prop="images" label="图片" width="200">
        <template #default="{ row }">
          <el-image
            :src="getImageUrl(row.images)"
            :preview-src-list="[getImageUrl(row.images)]"
            fit="cover"
            style="width: 150px; height: 80px; border-radius: 4px"
          />
        </template>
      </el-table-column>
      <el-table-column prop="content" label="描述内容" min-width="200" />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '显示' : '隐藏' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="primary" link @click="handleToggleStatus(row)">
            {{ row.status === 1 ? '隐藏' : '显示' }}
          </el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="fetchRotations"
        @size-change="fetchRotations"
      />
    </div>
    
    <!-- 添加/编辑弹窗 -->
    <el-dialog
      v-model="showDialog"
      :title="editingItem ? '编辑轮播图' : '添加轮播图'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="图片地址" prop="images">
          <el-input v-model="form.images" placeholder="请输入图片URL" />
        </el-form-item>
        <el-form-item label="描述内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="3"
            placeholder="请输入轮播图描述内容"
          />
        </el-form-item>
        <el-form-item label="跳转链接" prop="link">
          <el-input v-model="form.link" placeholder="点击跳转链接（选填）" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">显示</el-radio>
            <el-radio :value="0">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getRotationPage, saveRotation, editRotation, removeRotation } from '@/api/rotation'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const saving = ref(false)
const rotations = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const showDialog = ref(false)
const editingItem = ref(null)
const formRef = ref()
const form = reactive({
  images: '',
  content: '',
  link: '',
  sort: 0,
  status: 1
})

const rules = {
  images: [{ required: true, message: '请输入图片地址', trigger: 'blur' }]
}

// 获取图片URL
const getImageUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return `/api${path}`
}

// 获取轮播图列表
const fetchRotations = async () => {
  loading.value = true
  try {
    const res = await getRotationPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    rotations.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取轮播图列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  form.images = ''
  form.content = ''
  form.link = ''
  form.sort = 0
  form.status = 1
}

// 添加
const handleAdd = () => {
  editingItem.value = null
  resetForm()
  showDialog.value = true
}

// 编辑
const handleEdit = (row) => {
  editingItem.value = row
  Object.assign(form, {
    images: row.images,
    content: row.content || '',
    link: row.link || '',
    sort: row.sort || 0,
    status: row.status
  })
  showDialog.value = true
}

// 保存
const handleSave = async () => {
  try {
    await formRef.value.validate()
    saving.value = true
    
    if (editingItem.value) {
      await editRotation({ ...form, id: editingItem.value.id })
    } else {
      await saveRotation(form)
    }
    
    ElMessage.success('保存成功')
    showDialog.value = false
    fetchRotations()
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

// 显示/隐藏
const handleToggleStatus = async (row) => {
  try {
    await editRotation({ id: row.id, status: row.status === 1 ? 0 : 1 })
    ElMessage.success(row.status === 1 ? '已隐藏' : '已显示')
    fetchRotations()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该轮播图吗？', '提示', { type: 'warning' })
    await removeRotation(row.id)
    ElMessage.success('删除成功')
    fetchRotations()
  } catch (error) {
    // 取消删除
  }
}

onMounted(() => {
  fetchRotations()
})
</script>

<style lang="scss" scoped>
.rotations-page {
  .page-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
  }
  
  .page-title {
    font-size: 20px;
    font-weight: 600;
    color: #333;
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
