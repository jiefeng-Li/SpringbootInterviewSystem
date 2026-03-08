<template>
  <div class="register-container">
    <el-card
      class="common-register-card"
      :style="{ zIndex: modeShift ? 0 : 10 }"
    >
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
            ><h2>登录/注册</h2></el-header
          >
          <el-main>
            <el-tabs
              style="
                display: flex;
                align-items: center;
                justify-content: center;
              "
              v-model="commonTab"
              @tab-click="handleClick"
            >
              <el-tab-pane label="我要找工作" name="JOB_SEEKER"></el-tab-pane>
              <el-tab-pane label="我要招聘" name="RECRUITER"></el-tab-pane>
            </el-tabs>
            <el-form
              ref="ruleFormRef"
              class="register-form"
              :model="user"
              :rules="rules"
              label-width="auto"
            >
              <el-form-item label="用户名" prop="username">
                <el-input v-model="user.name" />
              </el-form-item>
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="user.phone" />
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input v-model="user.password" type="password" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="register(user)">
                  提交
                </el-button>
                <el-link
                  type="primary"
                  style="margin-left: auto"
                  @click="switchMode()"
                  >企业注册/登录</el-link
                >
              </el-form-item>
            </el-form>
          </el-main>
        </el-container>
      </el-container>
    </el-card>
    <el-card
      class="admin-register-card"
      :style="{
        zIndex: modeShift ? 10 : 0,
        transform: modeShift ? 'translateX(0)' : 'translateX(100%)',
        opacity: modeShift ? 1 : 0,
      }"
    >
      <el-container style="height: 100%; width: 100%">
        <el-aside
          width="200px"
          style="background-color: rgb(248, 152, 152)"
        ></el-aside>
        <el-container>
          <el-header
            style="
              display: flex;
              justify-content: center;
              align-items: center;
              font-size: 24px;
            "
            ><h2>登录/注册</h2></el-header
          >
          <el-main>
            <el-tabs
              style="
                display: flex;
                align-items: center;
                justify-content: center;
              "
              v-model="adminTab"
              @tab-click="handleClick"
            >
              <el-tab-pane
                label="系统管理"
                name="SYS_ADMIN"
                disabled
              ></el-tab-pane>
              <el-tab-pane
                label="公司管理/注册"
                name="COMP_ADMIN"
              ></el-tab-pane>
            </el-tabs>
            <el-form
              ref="ruleFormRef"
              class="register-form"
              :model="user"
              :rules="rules"
              label-width="auto"
            >
              <el-form-item label="用户名" prop="username">
                <el-input v-model="user.name" />
              </el-form-item>
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="user.phone" />
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input v-model="user.password" type="password" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="register(user)">
                  提交
                </el-button>
                <el-link
                  type="primary"
                  style="margin-left: auto"
                  @click="switchMode()"
                  >企业注册/登录</el-link
                >
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
import request from "@/utils/request";
import { ElMessage } from "element-plus";

const router = useRouter();
const loading = ref(false);
const ruleFormRef = ref();
const modeShift = ref(false);

const user = reactive({
  username: "",
  phone: "",
  password: "",
  registerType: "company",
});

const rules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  phone: [
    { required: true, message: "请输入手机号", trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "手机号格式不正确",
      trigger: "blur",
    },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, message: "密码长度不能少于6位", trigger: "blur" },
  ],
  registerType: [
    { required: true, message: "请选择注册类型", trigger: "change" },
  ],
};

const commonTab = ref("JOB_SEEKER");
const adminTab = ref("COMP_ADMIN");

const handleClick = (tab) => {
  user.registerType = tab.props.name;
  console.log("当前注册类型:", user.registerType);
};

const register = async (user) => {
  try {
    await ruleFormRef.value.validate();
  } catch (error) {
    return;
  }

  loading.value = true;
  const { username, phone, password, registerType } = user;
  const data = { username, phone, password };

  let url = "";
  if (registerType === "SYS_ADMIN") {
    url = "/comp/register";
  } else if (registerType === "COMP_ADMIN") {
    url = "/admin/register";
  } else {
    url = "/register";
    data.role = registerType;
  }

  try {
    const response = await request.post(url, data);
    if (response.data.code === 200) {
      ElMessage.success("注册成功，请登录");
      router.push("/login");
    }
  } catch (error) {
    ElMessage.error("注册失败，请重试");
  } finally {
    loading.value = false;
  }
};

const switchMode = () => {
  // 切换到企业注册/登录模式的逻辑
  console.log("切换到企业注册/登录模式");
  modeShift.value = !modeShift.value;
};
</script>

<style scoped>
.register-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
  float: none;
  z-index: 10;
}

.common-register-card {
  position: absolute;
  width: 600px;
  height: 500px;
  padding: 0;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  float: none;
  z-index: 1;
  transition: all 0.5s ease-in-out;
  transform: translateX(0);
  border-radius: 16px;
}

.admin-register-card {
  position: absolute;
  width: 600px;
  height: 500px;
  padding: 0;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.5s ease-in-out;
  transform: translateX(100%);
  opacity: 0;
  border-radius: 16px;
}

.common-register-card :deep(.el-card__body) {
  padding: 0;
}

.admin-register-card :deep(.el-card__body) {
  padding: 0;
}

.register-form {
  max-width: 600px;
}
.register-container :deep(.el-form-item) {
  padding: 1em;
}
</style>
