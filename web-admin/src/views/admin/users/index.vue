<template>
  <div class="users-page">
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>添加用户
      </el-button>
    </div>
    
    <!-- 搜索筛选 -->
    <div class="filter-bar">
      <el-input
        v-model="searchForm.userName"
        placeholder="用户名"
        clearable
        style="width: 200px"
        @keyup.enter="handleSearch"
      />
      <el-select v-model="searchForm.userType" placeholder="用户类型" clearable style="width: 150px">
        <el-option label="管理员" :value="0" />
        <el-option label="采购商" :value="1" />
        <el-option label="供应商" :value="2" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>
    
    <!-- 用户列表 -->
    <el-table :data="users" stripe v-loading="loading">
      <el-table-column prop="loginAccount" label="登录账号" width="150" />
      <el-table-column prop="userName" label="用户名" width="120" />
      <el-table-column prop="userType" label="用户类型" width="100">
        <template #default="{ row }">
          <el-tag :type="getUserTypeTag(row.userType)" size="small">
            {{ getUserTypeText(row.userType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="sex" label="性别" width="80">
        <template #default="{ row }">
          {{ row.sex === 0 ? '男' : '女' }}
        </template>
      </el-table-column>
      <el-table-column prop="tel" label="手机号" width="130" />
      <el-table-column prop="email" label="邮箱" min-width="150" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="small">
            {{ row.status === 0 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="primary" link @click="handleResetPassword(row)">重置密码</el-button>
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
        @current-change="fetchUsers"
        @size-change="fetchUsers"
      />
    </div>
    
    <!-- 添加/编辑弹窗 -->
    <el-dialog
      v-model="showDialog"
      :title="editingItem ? '编辑用户' : '添加用户'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="登录账号" prop="loginAccount">
          <el-input v-model="form.loginAccount" placeholder="请输入登录账号" :disabled="!!editingItem" />
        </el-form-item>
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!editingItem">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="form.userType" placeholder="请选择用户类型" style="width: 100%">
            <el-option label="管理员" :value="0" />
            <el-option label="采购商" :value="1" />
            <el-option label="供应商" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="form.sex">
            <el-radio :value="0">男</el-radio>
            <el-radio :value="1">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号" prop="tel">
          <el-input v-model="form.tel" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">正常</el-radio>
            <el-radio :value="1">禁用</el-radio>
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
import { getUserPage, saveUser, editUser, removeUser, resetPassword } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const saving = ref(false)
const users = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  userName: '',
  userType: ''
})

const showDialog = ref(false)
const editingItem = ref(null)
const formRef = ref()
const form = reactive({
  loginAccount: '',
  userName: '',
  password: '',
  userType: 1,
  sex: 0,
  tel: '',
  email: '',
  status: 0
})

const rules = {
  loginAccount: [{ required: true, message: '请输入登录账号', trigger: 'blur' }],
  userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  userType: [{ required: true, message: '请选择用户类型', trigger: 'change' }]
}

// 用户类型映射
const getUserTypeText = (type) => {
  const map = { 0: '管理员', 1: '采购商', 2: '供应商' }
  return map[type] || '未知'
}

const getUserTypeTag = (type) => {
  const map = { 0: 'danger', 1: 'primary', 2: 'success' }
  return map[type] || 'info'
}

// 获取用户列表
const fetchUsers = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (searchForm.userName) params.userName = searchForm.userName
    if (searchForm.userType !== '') params.userType = searchForm.userType
    
    const res = await getUserPage(params)
    users.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pageNum.value = 1
  fetchUsers()
}

// 重置
const handleReset = () => {
  searchForm.userName = ''
  searchForm.userType = ''
  handleSearch()
}

// 重置表单
const resetForm = () => {
  form.loginAccount = ''
  form.userName = ''
  form.password = ''
  form.userType = 1
  form.sex = 0
  form.tel = ''
  form.email = ''
  form.status = 0
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
    loginAccount: row.loginAccount,
    userName: row.userName,
    userType: row.userType,
    sex: row.sex,
    tel: row.tel || '',
    email: row.email || '',
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
      await editUser({ ...form, id: editingItem.value.id })
    } else {
      await saveUser(form)
    }
    
    ElMessage.success('保存成功')
    showDialog.value = false
    fetchUsers()
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

// 重置密码
const handleResetPassword = async (row) => {
  try {
    await ElMessageBox.confirm('确定要将该用户密码重置为 123456 吗？', '提示', { type: 'warning' })
    await resetPassword({ id: row.id, password: '123456' })
    ElMessage.success('密码已重置为 123456')
  } catch (error) {
    // 取消操作
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', { type: 'warning' })
    await removeUser(row.id)
    ElMessage.success('删除成功')
    fetchUsers()
  } catch (error) {
    // 取消删除
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style lang="scss" scoped>
.users-page {
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
