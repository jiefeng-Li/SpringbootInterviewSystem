<template>
  <BaseLayout>
    <template #header>
      <div class="account-field">
        <el-dropdown placement="bottom-end">
          <el-avatar :icon="UserFilled" />
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>
                <el-icon><User /></el-icon>个人信息
              </el-dropdown-item>
              <el-dropdown-item>
                <el-icon><Bell /></el-icon>消息通知
                <el-badge class="mark" :value="12" />
              </el-dropdown-item>
              <el-dropdown-item>
                <el-icon><QuestionFilled /></el-icon>帮助中心
              </el-dropdown-item>
              <el-dropdown-item>
                <el-icon><Comment /></el-icon>系统反馈
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

    <template #aside>
      <CompAdminAside />
    </template>
    <template #main>
      <router-view />
    </template>
  </BaseLayout>
</template>

<script setup>
import CompAdminAside from "./components/CompAdminAside.vue";
import BaseLayout from "../layout/index.vue";
import { useRouter } from "vue-router";
import {
  UserFilled,
  CloseBold,
  User,
  Bell,
  QuestionFilled,
  Comment,
} from "@element-plus/icons-vue";
import { useUserStore } from "@/stores/user";
import { onMounted } from "vue";
import { ElMessage } from "element-plus";

const router = useRouter();
const userStore = useUserStore();

const handleLogout = () => {
  useUserStore().logout();
  router.push("/");
  ElMessage({
    message: "登出成功",
    type: "success",
    duration: 1000,
  });
};

onMounted(() => {
  console.log("userStore.isloginned: ", userStore.isloginned);
  // 检查用户是否已登录
  if (userStore.isloginned) {
    // 如果已登录，可以在这里获取用户信息或执行其他操作
    console.log("用户已登录");
  } else {
    console.log("用户未登录");
    ElMessage({
      message: "请先登录",
      type: "warning",
      duration: 1000,
    });
    // router.push("/login");
  }
});
</script>

<style lang="scss" scoped>
.comp-container {
  .el-header {
    height: 4em;
    position: relative;
    background-color: #409eff;
    color: var(--el-text-color-primary);
    display: flex;
    justify-content: flex-end;
  }
}
.el-aside {
  background: rgb(217, 236, 255);
}

.account-field {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
</style>
