<template>
  <BaseLayout>
    <template #header>
      <div class="account-field">
        <el-dropdown placement="bottom-end">
          <el-avatar :icon="UserFilled" :src="userStore.avatarUrl" />
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

    <template #aside>
      <RecruiterAside />
    </template>
    <template #main>
      <router-view />
    </template>
  </BaseLayout>
</template>

<script setup>
import RecruiterAside from "./components/RecruiterAside.vue";
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
  if (!userStore.isloginned) {
    ElMessage({
      message: "请先登录",
      type: "warning",
      duration: 1000,
    });
    router.push("/login");
  } else if (!userStore.companyId) {
    // 如果未绑定公司，重定向到绑定公司页面
    router.push("/recruiter/bind-company");
  }
});
</script>

<style lang="scss" scoped>
.recruiter-container {
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
