<template>
  <div class="my-page">
    <el-descriptions title="用户信息" border>
      <el-descriptions-item
        :rowspan="2"
        :width="140"
        label="头像"
        align="center"
      >
        <el-image
          style="width: 100px; height: 100px"
          :src="url"
          :zoom-rate="1.2"
          :max-scale="7"
          :min-scale="0.2"
          :preview-src-list="srcList"
          show-progress
          fit="cover"
        >
          <template>
            <div class="image-slot viewer-error">
              <el-icon><icon-picture /></el-icon>
            </div>
          </template>
        </el-image>
      </el-descriptions-item>
      <el-descriptions-item label="用户名">kooriookami</el-descriptions-item>
      <el-descriptions-item label="账号状态">正常</el-descriptions-item>
      <el-descriptions-item label="电话">18100000000</el-descriptions-item>
      <el-descriptions-item label="邮箱"
        >3121707529@qq.com</el-descriptions-item
      >
      <el-descriptions-item label="个人简介"
        >hello everyone! I am a student from Suzhou. I want to learn more about
        web development.</el-descriptions-item
      >
    </el-descriptions>
    <el-container>
      <el-main>
        <el-container style="height: 100%">
          <el-header>
            <el-menu
              :default-active="4"
              mode="horizontal"
              class="horizontal-menu"
            >
              <el-menu-item index="1">沟通过</el-menu-item>
              <el-menu-item index="2">已投递</el-menu-item>
              <el-menu-item index="3">面试</el-menu-item>
              <el-menu-item index="4">感兴趣</el-menu-item>
            </el-menu>
          </el-header>
          <el-main><router-view /></el-main>
        </el-container>
      </el-main>
      <el-aside>
        <el-card
          style="height: 400px; width: 80%; margin: 0 auto; margin-top: 20px"
        >
          <p v-for="o in 4" :key="o" class="text item">
            {{ "List item " + o }}
          </p>
        </el-card></el-aside
      >
    </el-container>
  </div>
</template>

<script setup></script>

<style lang="scss" scoped>
.my-page {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  .el-container {
    flex: 1;
    .el-aside {
      border-left: 3px solid #eee;
    }
    .horizontal-menu {
      .el-menu-item {
        border-radius: 12px;
        margin: 0 10px;
        padding: 0 10px;
      }
    }
  }
}
</style>
