<template>
  <div class="company-detail-container">
    <div class="detail-header">
      <div class="logo"></div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();

onMounted(() => {
  console.log("公司 ID:", route.params.id);
  // 调用 API 获取公司详情数据
});
</script>

<style scoped>
.company-detail-container {
  width: 100%;
  height: 100%;
  padding-top: 20px;
  padding-bottom: 20px;
  .detail-header {
    width: 100%;
    height: 200px;
    background-color: #f0f0f0;
    .logo {
      width: 80px;
      height: 80px;
      background-color: #000;
    }
  }
}
</style>
