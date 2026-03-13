<template>
  <el-scrollbar style="border-right: 2px solid #f0f0f0">
    <el-menu
      class="mene-style"
      default-active="/personal/my"
      router
      @open="handleOpen"
      @close="handleClose"
    >
      <el-menu-item index="/personal/my">
        <el-icon><setting /></el-icon>
        <span>我的</span>
      </el-menu-item>
      <el-menu-item index="/personal/info">
        <el-icon><setting /></el-icon>
        <span>账号管理</span>
      </el-menu-item>
      <el-menu-item index="/personal/settings">
        <el-icon><setting /></el-icon>
        <span>系统设置</span>
      </el-menu-item>
      <el-menu-item index="/personal/help">
        <el-icon><setting /></el-icon>
        <span>帮助中心</span>
      </el-menu-item>
    </el-menu>
  </el-scrollbar>
</template>

<script setup>
// import { setting } from "@element-plus/icons-vue";
// import { useRouter } from "vue-router";

// const router = useRouter();
</script>

<style lang="scss" scoped>
.menu-style {
  border: 0;
}
</style>
