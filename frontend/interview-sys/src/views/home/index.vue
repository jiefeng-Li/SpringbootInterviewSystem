<template>
  <div class="home-container">
    <el-container style="height: 100vh">
      <el-header style="height: 4em; background-color: #409eff; padding: 0">
        <HomeHeader></HomeHeader>
      </el-header>
      <el-main style="flex: 1; padding: 0">
        <el-container style="height: 100%">
          <el-aside>Aside</el-aside>
          <el-main class="main-container"> <router-view /></el-main>
          <el-aside>Aside</el-aside>
        </el-container>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import HomeHeader from "./components/HomeHeader.vue";
</script>

<style lang="scss" scoped>
.home-container {
  width: 100vw;
  height: 100vh;
  background-color: rgb(236, 245, 255);
  position: relative;
  .main-container {
    background-color: #fff;
    min-width: 800px;
    max-width: 960px;
    padding: 0;
    overflow: hidden;
    margin: 0 auto; // 水平居中
  }
}
</style>
