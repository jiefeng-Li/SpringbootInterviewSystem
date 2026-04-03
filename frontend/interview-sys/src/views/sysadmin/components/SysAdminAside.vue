<template>
  <el-scrollbar>
    <el-menu :default-active="activeMenu" class="el-menu-vertical-demo" router>
      <el-menu-item index="/admin/certification-manage">
        <el-icon><Files /></el-icon>
        <span>企业认证审核</span>
      </el-menu-item>

      <el-menu-item index="/admin/company-manage">
        <el-icon><OfficeBuilding /></el-icon>
        <span>公司管理</span>
      </el-menu-item>

      <el-menu-item index="/admin/user-manage">
        <el-icon><User /></el-icon>
        <span>用户状态管理</span>
      </el-menu-item>

      <el-menu-item index="/admin/job-manage">
        <el-icon><Collection /></el-icon>
        <span>职位管理</span>
      </el-menu-item>

      <el-menu-item index="/admin/offer-manage">
        <el-icon><Document /></el-icon>
        <span>Offer记录</span>
      </el-menu-item>

      <el-menu-item index="/admin/statistics">
        <el-icon><DataLine /></el-icon>
        <span>统计看板</span>
      </el-menu-item>

      <el-menu-item index="/admin/system-message">
        <el-icon><Bell /></el-icon>
        <span>系统消息推送</span>
      </el-menu-item>
    </el-menu>
  </el-scrollbar>
</template>

<script setup>
import { computed } from "vue";
import { useRoute } from "vue-router";
import {
  Bell,
  Collection,
  DataLine,
  Document,
  Files,
  OfficeBuilding,
  User,
} from "@element-plus/icons-vue";

const route = useRoute();

const activeMenu = computed(() => route.path);
</script>

<style lang="scss" scoped>
.el-menu-vertical-demo {
  border-right: none;
}
</style>
