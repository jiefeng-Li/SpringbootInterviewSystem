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
    </el-menu>
  </el-scrollbar>
</template>

<script setup>
import { computed } from "vue";
import { useRoute } from "vue-router";
import { Files, OfficeBuilding, User } from "@element-plus/icons-vue";

const route = useRoute();

const activeMenu = computed(() => route.path);
</script>

<style lang="scss" scoped>
.el-menu-vertical-demo {
  border-right: none;
}
</style>
