<template>
  <div class="address-page">
    <div class="page-header">
      <h2 class="page-title">收货地址</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增地址
      </el-button>
    </div>
    
    <div class="address-list" v-loading="loading">
      <div 
        class="address-card" 
        v-for="item in addressList" 
        :key="item.id"
        :class="{ default: item.first === 0 }"
      >
        <div class="address-info">
          <div class="info-top">
            <span class="name">{{ item.name }}</span>
            <span class="phone">{{ item.tel }}</span>
            <el-tag size="small" type="danger" v-if="item.first === 0">默认</el-tag>
          </div>
          <div class="info-address">
            {{ item.address }}
          </div>
        </div>
        
        <div class="address-actions">
          <el-button type="primary" link @click="handleEdit(item)">编辑</el-button>
          <el-button type="primary" link @click="handleSetDefault(item)" v-if="item.first !== 0">
            设为默认
          </el-button>
          <el-button type="danger" link @click="handleDelete(item)">删除</el-button>
        </div>
      </div>
      
      <el-empty v-if="!loading && addressList.length === 0" description="暂无收货地址" />
    </div>
    
    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="showDialog"
      :title="editingAddress ? '编辑地址' : '新增地址'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="收货人" prop="name">
          <el-input v-model="form.name" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="tel">
          <el-input v-model="form.tel" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input
            v-model="form.address"
            type="textarea"
            :rows="2"
            placeholder="请输入详细地址"
          />
        </el-form-item>
        <el-form-item label="设为默认">
          <!-- 后端字段：first=0 表示默认，first=1 表示非默认 -->
          <el-switch v-model="form.first" :active-value="0" :inactive-value="1" />
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
import { getAddressList, saveAddress, editAddress, removeAddress, setDefaultAddress } from '@/api/address'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const saving = ref(false)
const addressList = ref([])
const showDialog = ref(false)
const editingAddress = ref(null)

const formRef = ref()
const form = reactive({
  name: '',
  tel: '',
  address: '',
  first: 1
})

const rules = {
  name: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  tel: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

// 获取地址列表
const fetchAddressList = async () => {
  loading.value = true
  try {
    const res = await getAddressList()
    addressList.value = res.data || []
  } catch (error) {
    console.error('获取地址列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  form.name = ''
  form.tel = ''
  form.address = ''
  form.first = 1
}

// 新增
const handleAdd = () => {
  editingAddress.value = null
  resetForm()
  showDialog.value = true
}

// 编辑
const handleEdit = (item) => {
  editingAddress.value = item
  Object.assign(form, {
    name: item.name,
    tel: item.tel,
    address: item.address,
    first: item.first
  })
  showDialog.value = true
}

// 保存
const handleSave = async () => {
  try {
    await formRef.value.validate()
    saving.value = true
    
    if (editingAddress.value) {
      await editAddress({ ...form, id: editingAddress.value.id })
    } else {
      await saveAddress(form)
    }
    
    ElMessage.success('保存成功')
    showDialog.value = false
    fetchAddressList()
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

// 设为默认
const handleSetDefault = async (item) => {
  try {
    await setDefaultAddress(item.id)
    ElMessage.success('设置成功')
    fetchAddressList()
  } catch (error) {
    console.error('设置失败:', error)
  }
}

// 删除
const handleDelete = async (item) => {
  try {
    await ElMessageBox.confirm('确定要删除该地址吗？', '提示', { type: 'warning' })
    await removeAddress(item.id)
    ElMessage.success('删除成功')
    fetchAddressList()
  } catch (error) {
    // 取消删除
  }
}

onMounted(() => {
  fetchAddressList()
})
</script>

<style lang="scss" scoped>
.address-page {
  .page-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid #f0f0f0;
  }
  
  .page-title {
    font-size: 18px;
    font-weight: 600;
    color: #333;
  }
}

.address-list {
  min-height: 200px;
}

.address-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  margin-bottom: 12px;
  transition: all 0.3s;
  
  &:hover {
    border-color: #67c23a;
  }
  
  &.default {
    border-color: #67c23a;
    background: #f0f9eb;
  }
}

.address-info {
  flex: 1;
  
  .info-top {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 8px;
    
    .name {
      font-size: 15px;
      font-weight: 600;
      color: #333;
    }
    
    .phone {
      font-size: 14px;
      color: #666;
    }
  }
  
  .info-address {
    font-size: 13px;
    color: #999;
  }
}

.address-actions {
  display: flex;
  gap: 8px;
}

.region-inputs {
  display: flex;
  gap: 8px;
  
  .el-input {
    flex: 1;
  }
}
</style>
