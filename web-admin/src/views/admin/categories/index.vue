<template>
  <div class="categories-page">
    <div class="page-header">
      <h2 class="page-title">分类管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>添加分类
      </el-button>
    </div>
    
    <!-- 分类列表 -->
    <el-table :data="categories" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="200" />
      <el-table-column prop="name" label="分类名称" />
      <el-table-column prop="sort" label="排序" width="100" />
      <el-table-column prop="remark" label="备注" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
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
        @current-change="fetchCategories"
        @size-change="fetchCategories"
      />
    </div>
    
    <!-- 添加/编辑弹窗 -->
    <el-dialog
      v-model="showDialog"
      :title="editingItem ? '编辑分类' : '添加分类'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
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
import { 
  getVegetableTypePage, saveVegetableType, 
  editVegetableType, removeVegetableType 
} from '@/api/vegetable'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const saving = ref(false)
const categories = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const showDialog = ref(false)
const editingItem = ref(null)
const formRef = ref()
const form = reactive({
  name: '',
  sort: 0,
  remark: ''
})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

// 获取分类列表
const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await getVegetableTypePage({
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    categories.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取分类列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  form.name = ''
  form.sort = 0
  form.remark = ''
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
    name: row.name,
    sort: row.sort || 0,
    remark: row.remark || ''
  })
  showDialog.value = true
}

// 保存
const handleSave = async () => {
  try {
    await formRef.value.validate()
    saving.value = true
    
    if (editingItem.value) {
      await editVegetableType({ ...form, id: editingItem.value.id })
    } else {
      await saveVegetableType(form)
    }
    
    ElMessage.success('保存成功')
    showDialog.value = false
    fetchCategories()
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该分类吗？删除后该分类下的商品将失去分类', '提示', { type: 'warning' })
    await removeVegetableType(row.id)
    ElMessage.success('删除成功')
    fetchCategories()
  } catch (error) {
    // 取消删除
  }
}

onMounted(() => {
  fetchCategories()
})
</script>

<style lang="scss" scoped>
.categories-page {
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
