<template>
  <div class="login-container">
    <el-card class="login-card">
      <el-container style="height: 100%; width: 100%">
        <el-aside
          width="200px"
          style="background-color: rgb(51, 126, 204)"
        ></el-aside>
        <el-container>
          <el-header
            style="
              display: flex;
              justify-content: center;
              align-items: center;
              font-size: 24px;
            "
            ><h2>登录</h2>
            <el-link
              type="primary"
              style="margin-left: auto"
              @click="router.push('/register')"
              >注册</el-link
            >
          </el-header>
          <el-main>
            <el-form
              ref="formRef"
              class="register-form"
              :model="user"
              :rules="rules"
              label-width="auto"
            >
              <el-form-item label="账号" prop="account">
                <el-input
                  v-model="user.account"
                  placeholder="用户名/手机号/邮箱"
                />
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input v-model="user.password" type="password" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="onSubmit()"> 提交 </el-button>
              </el-form-item>
            </el-form>
          </el-main>
        </el-container>
      </el-container>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/stores/user";
import request from "@/utils/request";
import { getCurrentUserInfo, userLogin } from "@/api/user";

const router = useRouter();
const userStore = useUserStore();

const formRef = ref();
const loading = ref(false);

const user = reactive({
  account: "",
  password: "",
});

const rules = {
  account: [{ required: true, message: "请输入账号", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
};

const onSubmit = async () => {
  try {
    await formRef.value.validate();
    loading.value = true;

    const res = await userLogin(user.account, user.password);

    console.log(res);

    if (res.data.code === 200) {
      // 响应数据包含 token
      const token = res.data.data;
      userStore.setToken(token);
      userStore.isloginned = true;

      // 获取当前用户信息
      try {
        const userInfoRes = await getCurrentUserInfo();
        if (userInfoRes.data.code === 200) {
          const userData = userInfoRes.data.data;
          userStore.setUser(userData);

          ElMessage({
            message: "登录成功",
            type: "success",
            duration: 1000,
          });

          // 根据角色跳转页面
          if (userData.role === "COMP_ADMIN") {
            router.push("/comp");
          } else if (userData.role === "JOB_SEEKER") {
            router.push("/");
          } else if (userData.role === "RECRUITER") {
            router.push("/recruiter");
          } else if (userData.role === "SYS_ADMIN") {
            router.push("/admin");
          } else {
            // 默认跳转到首页
            router.push("/");
          }
        } else {
          ElMessage.error("获取用户信息失败，请重新登录");
          userStore.logout();
          router.push("/login");
        }
      } catch (error) {
        console.error("获取用户信息失败", error);
        ElMessage.error("获取用户信息失败，请重新登录");
        userStore.logout();
        router.push("/login");
      }
    } else {
      ElMessage.error(res.data.message || "登录失败");
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("登录失败");
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}

.login-card {
  position: absolute;
  width: 600px;
  height: 400px;
  padding: 0;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-radius: 16px;
}

.login-card :deep(.el-card__body) {
  padding: 0;
}
.login-container :deep(.el-form-item) {
  padding: 1em;
}
</style>
