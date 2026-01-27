<template>
  <div class="vegetables-page">
    <div class="page-header">
      <h2 class="page-title">商品管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>添加商品
      </el-button>
    </div>
    
    <!-- 搜索筛选 -->
    <div class="filter-bar">
      <el-input
        v-model="searchForm.name"
        placeholder="商品名称"
        clearable
        style="width: 200px"
        @keyup.enter="handleSearch"
      />
      <el-select v-model="searchForm.typeId" placeholder="商品分类" clearable style="width: 150px">
        <el-option
          v-for="item in categories"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>
    
    <!-- 商品列表 -->
    <el-table :data="vegetables" stripe v-loading="loading" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" />
      <el-table-column prop="images" label="图片" width="80">
        <template #default="{ row }">
          <el-image
            :src="getImageUrl(row.images)"
            :preview-src-list="[getImageUrl(row.images)]"
            fit="cover"
            style="width: 50px; height: 50px; border-radius: 4px"
          />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="商品名称" min-width="150" />
      <el-table-column prop="typeName" label="分类" width="100" />
      <el-table-column prop="price" label="价格" width="100">
        <template #default="{ row }">
          ¥{{ row.price }}
        </template>
      </el-table-column>
      <el-table-column prop="unit" label="单位" width="80" />
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="primary" link @click="handleToggleStatus(row)">
            {{ row.status === 1 ? '下架' : '上架' }}
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
        @current-change="fetchVegetables"
        @size-change="fetchVegetables"
      />
    </div>
    
    <!-- 添加/编辑弹窗 -->
    <el-dialog
      v-model="showDialog"
      :title="editingItem ? '编辑商品' : '添加商品'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品分类" prop="typeId">
          <el-select v-model="form.typeId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="商品价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 200px" />
          <span style="margin-left: 8px">元</span>
        </el-form-item>
        <el-form-item label="计量单位" prop="unit">
          <el-input v-model="form.unit" placeholder="如：斤、个、份" style="width: 200px" />
        </el-form-item>
        <el-form-item label="商品图片" prop="images">
          <el-input v-model="form.images" placeholder="请输入图片URL" />
        </el-form-item>
        <el-form-item label="商品描述" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入商品描述"
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
  getVegetablePage, getVegetableTypeList,
  saveVegetable, editVegetable, removeVegetable 
} from '@/api/vegetable'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const saving = ref(false)
const vegetables = ref([])
const categories = ref([])
const selectedIds = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  name: '',
  typeId: ''
})

const showDialog = ref(false)
const editingItem = ref(null)
const formRef = ref()
const form = reactive({
  name: '',
  typeId: '',
  price: 0,
  unit: '斤',
  images: '',
  remark: ''
})

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  typeId: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入商品价格', trigger: 'blur' }]
}

// 获取图片URL
const getImageUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return `/api${path}`
}

// 获取商品列表
const fetchVegetables = async () => {
  loading.value = true
  try {
    const res = await getVegetablePage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...searchForm
    })
    vegetables.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取商品列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取分类列表
const fetchCategories = async () => {
  try {
    const res = await getVegetableTypeList()
    categories.value = res.data || []
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pageNum.value = 1
  fetchVegetables()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  searchForm.typeId = ''
  handleSearch()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 重置表单
const resetForm = () => {
  form.name = ''
  form.typeId = ''
  form.price = 0
  form.unit = '斤'
  form.images = ''
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
    typeId: row.typeId,
    price: row.price,
    unit: row.unit,
    images: row.images,
    remark: row.remark
  })
  showDialog.value = true
}

// 保存
const handleSave = async () => {
  try {
    await formRef.value.validate()
    saving.value = true
    
    if (editingItem.value) {
      await editVegetable({ ...form, id: editingItem.value.id })
    } else {
      await saveVegetable(form)
    }
    
    ElMessage.success('保存成功')
    showDialog.value = false
    fetchVegetables()
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

// 上下架
const handleToggleStatus = async (row) => {
  try {
    await editVegetable({ id: row.id, status: row.status === 1 ? 0 : 1 })
    ElMessage.success(row.status === 1 ? '已下架' : '已上架')
    fetchVegetables()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', { type: 'warning' })
    await removeVegetable(row.id)
    ElMessage.success('删除成功')
    fetchVegetables()
  } catch (error) {
    // 取消删除
  }
}

onMounted(() => {
  fetchCategories()
  fetchVegetables()
})
</script>

<style lang="scss" scoped>
.vegetables-page {
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

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
