<template>
  <div class="delivery-page">
    <div class="page-header">
      <h2 class="page-title">配送员管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>添加配送员
      </el-button>
    </div>
    
    <!-- 搜索筛选 -->
    <div class="filter-bar">
      <el-input
        v-model="searchForm.name"
        placeholder="配送员姓名"
        clearable
        style="width: 200px"
        @keyup.enter="handleSearch"
      />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>
    
    <!-- 配送员列表 -->
    <el-table :data="deliveryList" stripe v-loading="loading">
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="tel" label="联系电话" width="150" />
      <el-table-column prop="idCard" label="身份证号" width="200" />
      <el-table-column prop="address" label="所在区域" min-width="200" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 0 ? 'success' : 'info'" size="small">
            {{ row.status === 0 ? '在职' : '离职' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="入职时间" width="180" />
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
        @current-change="fetchDeliveryList"
        @size-change="fetchDeliveryList"
      />
    </div>
    
    <!-- 添加/编辑弹窗 -->
    <el-dialog
      v-model="showDialog"
      :title="editingItem ? '编辑配送员' : '添加配送员'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入配送员姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="tel">
          <el-input v-model="form.tel" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="所在区域" prop="address">
          <el-input v-model="form.address" placeholder="请输入配送区域" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">在职</el-radio>
            <el-radio :value="1">离职</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="2"
            placeholder="备注信息"
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
import { getDeliveryPage, saveDelivery, editDelivery, removeDelivery } from '@/api/common'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const saving = ref(false)
const deliveryList = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  name: ''
})

const showDialog = ref(false)
const editingItem = ref(null)
const formRef = ref()
const form = reactive({
  name: '',
  tel: '',
  idCard: '',
  address: '',
  status: 0,
  remark: ''
})

const rules = {
  name: [{ required: true, message: '请输入配送员姓名', trigger: 'blur' }],
  tel: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 获取配送员列表
const fetchDeliveryList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (searchForm.name) params.name = searchForm.name
    
    const res = await getDeliveryPage(params)
    deliveryList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取配送员列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pageNum.value = 1
  fetchDeliveryList()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  handleSearch()
}

// 重置表单
const resetForm = () => {
  form.name = ''
  form.tel = ''
  form.idCard = ''
  form.address = ''
  form.status = 0
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
    tel: row.tel,
    idCard: row.idCard || '',
    address: row.address || '',
    status: row.status,
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
      await editDelivery({ ...form, id: editingItem.value.id })
    } else {
      await saveDelivery(form)
    }
    
    ElMessage.success('保存成功')
    showDialog.value = false
    fetchDeliveryList()
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该配送员吗？', '提示', { type: 'warning' })
    await removeDelivery(row.id)
    ElMessage.success('删除成功')
    fetchDeliveryList()
  } catch (error) {
    // 取消删除
  }
}

onMounted(() => {
  fetchDeliveryList()
})
</script>

<style lang="scss" scoped>
.delivery-page {
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
