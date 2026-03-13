<template>
  <div class="home-container">
    <el-container style="height: 100%">
      <el-header
        style="
          height: 4em;
          background-color: #409eff;
          padding: 0;
          display: flex;
          width: 100%;
          align-items: center;
          min-width: 1400px;
          float: left;
          position: fixed;
          z-index: 100;
        "
      >
        <HomeHeader></HomeHeader>
      </el-header>
      <el-main style="flex: 1; padding: 0; margin-top: 4em">
        <el-container style="height: 100%">
          <el-aside></el-aside>
          <el-main class="main-container"> <router-view /></el-main>
          <el-aside></el-aside>
        </el-container>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import HomeHeader from "./components/HomeHeader.vue";
import { useUserStore } from "@/stores/user";
import { ref, onMounted } from "vue";

const isLogin = ref(false);

// const router = useRouter();
const userStore = useUserStore();
onMounted(() => {
  // 检查用户是否已登录
  isLogin.value = userStore.isloginned;
  if (isLogin.value) {
    // 如果已登录，可以在这里获取用户信息或执行其他操作

    console.log("用户已登录");
  } else {
    console.log("用户未登录");
  }
});
</script>

<style lang="scss" scoped>
.home-container {
  width: 100vw;
  height: 1800px;
  background-color: rgb(236, 245, 255);
  position: relative;

  .main-container {
    background-color: #fff;
    min-width: 960px;
    max-width: 960px;
    height: 100%;
    padding: 0;
    margin: 0 auto; // 水平居中
  }
}
</style>
