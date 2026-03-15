<template>
  <el-scrollbar>
    <el-menu
      default-active="2"
      class="el-menu-vertical-demo"
      @open="handleOpen"
      @close="handleClose"
    >
      <el-sub-menu index="1">
        <template #title>
          <el-icon><OfficeBuilding /></el-icon>
          <span>企业管理</span>
        </template>
        <el-menu-item index="1-1">企业信息</el-menu-item>
        <el-menu-item index="1-2">企业认证</el-menu-item>
        <el-menu-item index="1-3">用户认证</el-menu-item>
      </el-sub-menu>
      <el-menu-item index="2">
        <el-icon><TrendCharts /></el-icon>
        <span>数据统计</span>
      </el-menu-item>
      <el-menu-item index="3">
        <el-icon><setting /></el-icon>
        <span>设置</span>
      </el-menu-item>
    </el-menu>
  </el-scrollbar>
</template>

<script setup>
import {
  Document,
  TrendCharts,
  OfficeBuilding,
  Setting,
} from "@element-plus/icons-vue";
</script>
