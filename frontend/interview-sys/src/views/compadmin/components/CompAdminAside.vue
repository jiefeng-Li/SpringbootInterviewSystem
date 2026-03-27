<template>
  <el-scrollbar>
    <el-menu
      :default-active="activeMenu"
      class="el-menu-vertical-demo"
      @open="handleOpen"
      @close="handleClose"
      router
    >
      <el-sub-menu index="/comp">
        <template #title>
          <el-icon><OfficeBuilding /></el-icon>
          <span>企业管理</span>
        </template>
        <el-menu-item index="/comp/info">企业信息</el-menu-item>
        <el-menu-item index="/comp/comp-certification">企业认证</el-menu-item>
        <el-menu-item index="/comp/user-certification">用户认证</el-menu-item>
      </el-sub-menu>
      <!-- <el-menu-item index="/comp/statetistic">
        <el-icon><TrendCharts /></el-icon>
        <span>数据统计</span>
      </el-menu-item> -->
      <el-menu-item :index="hasCompany ? '/comp/unbind' : '/comp/bind'">
        <el-icon><Link /></el-icon>
        <span>{{ hasCompany ? "解绑企业" : "绑定企业" }}</span>
      </el-menu-item>
      <el-menu-item index="/comp/setting">
        <el-icon><setting /></el-icon>
        <span>设置</span>
      </el-menu-item>
    </el-menu>
  </el-scrollbar>
</template>

<script setup>
import { useRoute } from "vue-router";
import { useUserStore } from "@/stores/user";
import { computed } from "vue";
import {
  Document,
  TrendCharts,
  OfficeBuilding,
  Setting,
  Link,
} from "@element-plus/icons-vue";

const userStore = useUserStore();
const route = useRoute();

const hasCompany = computed(() => !!userStore.companyId);

const activeMenu = computed(() => {
  return route.path;
});
</script>
