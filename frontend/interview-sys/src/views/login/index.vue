<template>
  <div class="login-container">
    <el-card class="login-card w-96">
      <el-container style="width: 100%; height: 100%">
        <el-aside style="width: 40%; background-color: #f0f0f0"></el-aside>
        <el-container>
          <el-header><h2 class="text-center">登录</h2></el-header>
          <el-main>
            <el-form
              ref="formRef"
              :model="form"
              :rules="rules"
              label-position="top"
              style="width: 100%"
            >
              <el-form-item label="账号" prop="account">
                <el-input v-model="form.account" placeholder="请输入账号" />
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input
                  v-model="form.password"
                  type="password"
                  placeholder="请输入密码"
                />
              </el-form-item>
              <el-form-item>
                <el-button
                  type="primary"
                  class="w-full"
                  @click="onSubmit"
                  :loading="loading"
                >
                  登录
                </el-button>
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

const router = useRouter();
const userStore = useUserStore();

const formRef = ref();
const loading = ref(false);

const form = reactive({
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

    const res = await request.post("/user/login", {
      account: form.account,
      password: form.password,
    });

    if (res.data.code === 200) {
      // 响应数据包含 token
      const { token } = res.data.data;
      userStore.setToken(token);

      ElMessage.success("登录成功");
      router.push("/"); // 跳转到首页
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
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f7f7f7;
  height: 100vh;
}

.login-card {
  display: flex;
  flex-direction: column;
  padding: 20px;
  width: 60vh;
  height: 50vw;
  min-height: max-content;
  min-width: max-content;
  max-width: 600px;
  max-height: 500px;
}
</style>
