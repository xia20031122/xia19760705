<template>
  <div class="empty-state">
    <el-empty :description="description" :image-size="imageSize">
      <template #image v-if="icon">
        <el-icon :size="64" color="#c0c4cc">
          <component :is="icon" />
        </el-icon>
      </template>
      <slot>
        <el-button v-if="buttonText" type="primary" @click="$emit('action')">
          {{ buttonText }}
        </el-button>
      </slot>
    </el-empty>
  </div>
</template>

<script setup>
defineProps({
  description: {
    type: String,
    default: '暂无数据'
  },
  icon: {
    type: String,
    default: ''
  },
  buttonText: {
    type: String,
    default: ''
  },
  imageSize: {
    type: Number,
    default: 100
  }
})

defineEmits(['action'])
</script>

<style lang="scss" scoped>
.empty-state {
  padding: 40px 20px;
}
</style>
