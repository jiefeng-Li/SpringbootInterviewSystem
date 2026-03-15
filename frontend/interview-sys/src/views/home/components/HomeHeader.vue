<template>
  <div class="logo-field" @click="router.push('/')"></div>
  <el-tabs v-model="activeName" class="home-tabs" @tab-click="handleClick">
    <el-tab-pane label="首页" name="home"></el-tab-pane>
    <el-tab-pane label="职位" name="job"></el-tab-pane>
    <el-tab-pane label="公司" name="company"></el-tab-pane>
    <el-tab-pane label="简历" name="resume"></el-tab-pane>
    <el-tab-pane label="我的投递" name="my-invote"></el-tab-pane>
  </el-tabs>
  <div class="account-field">
    <el-link href="/login" v-show="!isLogin">登录</el-link>
    <el-link href="/register" v-show="!isLogin">注册</el-link>
    <el-dropdown placement="bottom-end">
      <el-avatar :icon="UserFilled" />
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="router.push('/personal/my')">
            <el-icon><User /></el-icon>个人信息
          </el-dropdown-item>
          <el-dropdown-item @click="router.push('/personal/message')">
            <el-icon><Bell /></el-icon>消息通知
            <el-badge class="mark" :value="12" />
          </el-dropdown-item>
          <el-dropdown-item @click="router.push('/personal/settings')">
            <el-icon><Comment /></el-icon>系统设置
          </el-dropdown-item>
          <el-dropdown-item @click="router.push('/personal/help')">
            <el-icon><QuestionFilled /></el-icon>帮助中心
          </el-dropdown-item>
          <el-divider style="margin: 12px 0" />
          <el-dropdown-item @click="handleLogout"
            ><el-icon><CloseBold /></el-icon
            ><span style="color: #f56c6c">登出</span></el-dropdown-item
          >
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup>
import { useRouter, useRoute } from "vue-router";
import { UserFilled, CloseBold } from "@element-plus/icons-vue";
import { useUserStore } from "@/stores/user";
import { ref, onMounted, watch } from "vue";
import { ElMessage } from "element-plus";

const router = useRouter();
const route = useRoute();

const activeName = ref("home");

const getActiveNameFromRoute = (route) => {
  const path = route.path;
  if (path.startsWith("/job")) return "job";
  if (path.startsWith("/company")) return "company";
  if (path.startsWith("/resume")) return "resume";
  if (path.startsWith("/my-invote")) return "my-invote";
  return "home";
};

watch(
  () => route.path,
  () => {
    activeName.value = getActiveNameFromRoute(route);
    // 路由变化时滚动到页面顶部
    window.scrollTo({ top: 0, left: 0, behavior: "auto" });
  },
  { immediate: true },
);

const handleClick = (tab) => {
  // 根据选中的tab名称跳转到对应的路由
  router.push(`/${tab.props.name}`);
};
const handleLogout = () => {
  useUserStore().logout();
  isLogin.value = false;
  router.push("/");
  ElMessage({
    message: "登出成功",
    type: "success",
    duration: 1000,
  });
};

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
.home-tabs {
  width: 100%;
  height: 100%;
  display: flex;
  text-align: center;
  align-items: center;
  justify-content: center;
  position: relative;

  :deep(.el-tabs__item) {
    padding-left: 20px !important;
    padding-right: 20px !important;
    position: relative;
    transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);

    &::after {
      content: "";
      position: absolute;
      left: 0;
      bottom: 0;
      width: 100%;
      height: 2px;
      background-color: #ffffff;
      transform: scaleX(0);
      transition: transform 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
    }

    &:hover {
      color: #ffffff;
      background-color: rgb(236, 245, 255, 0.3);

      &::after {
        transform: scaleX(1);
      }
    }
  }

  :deep(.el-tabs__item.is-active) {
    border-radius: 5px;
    color: #ffffff;
    background-color: rgb(236, 245, 255, 0.3);

    &::after {
      transform: scaleX(1);
    }
  }

  :deep(.el-tabs__active-bar) {
    background-color: #ffffff;
  }

  :deep(.el-tabs__nav-wrap::after) {
    background-color: #409eff !important;
  }

  :deep(.el-tabs__header) {
    position: absolute;
    bottom: 0;
    margin-bottom: 2px;
  }
}
.account-field {
  height: 100%;
  width: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  .el-link:hover {
    color: #fff;
  }
}
.logo-field {
  height: 100%;
  width: 200px;
  background-color: rgba(0, 0, 0, 0.3);
}
</style>
