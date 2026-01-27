<template>
  <div class="image-upload">
    <el-upload
      :action="uploadUrl"
      :headers="headers"
      :show-file-list="false"
      :on-success="handleSuccess"
      :on-error="handleError"
      :before-upload="beforeUpload"
      accept="image/*"
    >
      <div class="upload-area" v-if="!imageUrl">
        <el-icon size="32" color="#c0c4cc"><Plus /></el-icon>
        <span class="upload-text">上传图片</span>
      </div>
      <div class="image-preview" v-else>
        <el-image :src="getImageUrl(imageUrl)" fit="cover" />
        <div class="image-actions">
          <el-icon @click.stop="handlePreview"><ZoomIn /></el-icon>
          <el-icon @click.stop="handleRemove"><Delete /></el-icon>
        </div>
      </div>
    </el-upload>
    
    <!-- 图片预览 -->
    <el-image-viewer
      v-if="showViewer"
      :url-list="[getImageUrl(imageUrl)]"
      @close="showViewer = false"
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  maxSize: {
    type: Number,
    default: 5 // MB
  }
})

const emit = defineEmits(['update:modelValue'])

const userStore = useUserStore()
const showViewer = ref(false)

const uploadUrl = '/api/common/upload'
const headers = computed(() => ({
  token: userStore.token
}))

const imageUrl = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const getImageUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return `/api${path}`
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt = file.size / 1024 / 1024 < props.maxSize
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt) {
    ElMessage.error(`图片大小不能超过 ${props.maxSize}MB!`)
    return false
  }
  return true
}

const handleSuccess = (res) => {
  if (res.code === 0) {
    imageUrl.value = res.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

const handleError = () => {
  ElMessage.error('上传失败')
}

const handlePreview = () => {
  showViewer.value = true
}

const handleRemove = () => {
  imageUrl.value = ''
}
</script>

<style lang="scss" scoped>
.image-upload {
  .upload-area {
    width: 120px;
    height: 120px;
    border: 1px dashed #d9d9d9;
    border-radius: 8px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: border-color 0.3s;
    
    &:hover {
      border-color: #67c23a;
    }
    
    .upload-text {
      margin-top: 8px;
      font-size: 12px;
      color: #999;
    }
  }
  
  .image-preview {
    width: 120px;
    height: 120px;
    border-radius: 8px;
    overflow: hidden;
    position: relative;
    
    .el-image {
      width: 100%;
      height: 100%;
    }
    
    .image-actions {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 16px;
      opacity: 0;
      transition: opacity 0.3s;
      
      .el-icon {
        color: #fff;
        font-size: 20px;
        cursor: pointer;
        
        &:hover {
          color: #67c23a;
        }
      }
    }
    
    &:hover .image-actions {
      opacity: 1;
    }
  }
}
</style>
