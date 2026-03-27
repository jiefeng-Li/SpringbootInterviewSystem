<template>
  <el-scrollbar style="border-right: 2px solid #f0f0f0">
    <el-menu class="menu-style" :active-index="activeIndex" router>
      <el-menu-item index="/personal/my">
        <el-icon><Avatar /></el-icon>
        <span>我的</span>
      </el-menu-item>
      <el-menu-item index="/personal/message">
        <el-icon><Message /></el-icon>
        <span>消息</span>
      </el-menu-item>
      <el-menu-item index="/personal/settings">
        <el-icon><Setting /></el-icon>
        <span>系统设置</span>
      </el-menu-item>
      <el-menu-item index="/personal/help">
        <el-icon><Help /></el-icon>
        <span>帮助中心</span>
      </el-menu-item>
    </el-menu>
  </el-scrollbar>
</template>

<script setup>
import { computed } from "vue";
import { useRoute } from "vue-router";
import { Setting, Message, Avatar, Grid, Help } from "@element-plus/icons-vue";

const route = useRoute();
const activeIndex = computed(() => route.path);
</script>

<style lang="scss" scoped>
.menu-style {
  border: 0;
}
</style>
