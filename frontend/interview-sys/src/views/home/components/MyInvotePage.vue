<template>
  <div class="invote-page-container">
    <el-container style="height: calc(100vh - 60px)">
      <el-header>
        <el-menu :default-active="1" mode="horizontal" class="horizontal-menu">
          <el-menu-item index="1">全部</el-menu-item>
          <el-menu-item index="2">未查看</el-menu-item>
          <el-menu-item index="3">已查看</el-menu-item>
          <el-menu-item index="4">感兴趣</el-menu-item>
          <el-menu-item index="5">约面试</el-menu-item>
        </el-menu>
      </el-header>
      <el-main>
        <el-card class="box-card" v-for="item in 24" :key="item">
          <div slot="header" class="clearfix">
            <span>职位名称</span>
            <el-button style="float: right; padding: 3px 0" type="text"
              >查看</el-button
            >
          </div>
          <div v-for="o in 4" :key="o" class="text item">
            {{ "职位描述 " + o }}
          </div>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { onMounted, onBeforeUnmount } from "vue";

onMounted(() => {
  document.body.style.overflow = "hidden";
});

onBeforeUnmount(() => {
  document.body.style.overflow = "";
});
</script>

<style lang="scss" scoped>
.invote-page-container {
  width: 100%;
}
</style>
