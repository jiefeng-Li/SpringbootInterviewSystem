<template>
  <el-tabs v-model="activeName" class="home-tabs" @tab-click="handleClick">
    <el-tab-pane label="home" name="home">首页</el-tab-pane>
    <el-tab-pane label="job" name="job">职位</el-tab-pane>
    <el-tab-pane label="company" name="company">公司</el-tab-pane>
    <el-tab-pane label="resume" name="resume">简历</el-tab-pane>
    <el-tab-pane label="my-invote" name="my-invote">我的投递</el-tab-pane>
  </el-tabs>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const activeName = ref("home");

const handleClick = (tab) => {
  console.log(tab);
  // 根据选中的tab名称跳转到对应的路由
  router.push(`/${tab.props.name}`);
};
</script>

<style lang="scss" scoped>
.home-tabs {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  :deep(.el-tabs__item.is-active) {
    color: #ffffff;
  }
  :deep(.el-tabs__active-bar) {
    background-color: #ffffff;
  }
  :deep(.el-tabs__nav-wrap::after) {
    background-color: #409eff !important;
  }
  :deep(.el-tabs__item:hover) {
    color: #ffffff;
  }
}
</style>
