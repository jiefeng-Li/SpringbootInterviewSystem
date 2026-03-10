<template>
  <div class="info-container">
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
      <!-- <el-descriptions-item label="备注">
        <el-tag size="small">School</el-tag>
      </el-descriptions-item> -->
    </el-descriptions>
    <div id="info-edit-btn">
      <el-button type="primary" round :icon="Edit">编辑</el-button>
    </div>
  </div>
</template>

<script setup>
import { Edit } from "@element-plus/icons-vue";
const url =
  "https://fuss10.elemecdn.com/a/3f/3302e58f9a181d2509f3dc0fa68b0jpeg.jpeg";
const srcList = [url];
</script>

<style lang="scss" scoped>
.info-container {
  width: 100%;
  height: 200px;
  .el-descriptions {
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
  }
  #info-edit-btn {
    width: 100%;
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
