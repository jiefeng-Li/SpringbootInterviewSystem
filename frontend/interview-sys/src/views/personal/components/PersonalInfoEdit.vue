<template>
  <div class="personal-info-edit">
    <el-card class="edit-card">
      <template #header>
        <div class="card-header">
          <span>编辑个人信息</span>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
        class="edit-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="头像" prop="avatar">
          <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :auto-upload="false"
            :on-change="handleAvatarChange"
          >
            <img
              v-if="formData.avatarUrl"
              :src="formData.avatarUrl"
              class="avatar"
            />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="个人简介" prop="profile">
          <el-input
            v-model="formData.profile"
            type="textarea"
            :rows="4"
            placeholder="请输入个人简介"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm">完成</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import { useUserStore } from "@/stores/user";
import { updateOneUserInfo } from "@/api/user";

const router = useRouter();
const userStore = useUserStore();
const formRef = ref(null);

// 表单数据
const formData = reactive({
  username: "",
  avatar: null,
  avatarUrl: "",
  profile: "",
});

// 表单验证规则
const rules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 3, max: 20, message: "长度在 3 到 20 个字符", trigger: "blur" },
  ],
};

// 处理头像上传
const handleAvatarChange = (file) => {
  const isJPGOrPNG =
    file.raw.type === "image/jpeg" || file.raw.type === "image/png";
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPGOrPNG) {
    ElMessage.error("上传头像图片只能是 JPG/PNG 格式!");
    return false;
  }
  if (!isLt2M) {
    ElMessage.error("上传头像图片大小不能超过 2MB!");
    return false;
  }

  formData.avatar = file.raw;
  formData.avatarUrl = URL.createObjectURL(file.raw);
  return true;
};

// 提交表单
// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 创建FormData对象用于文件上传
        const submitData = new FormData();

        // 添加基本字段
        submitData.append("username", formData.username);
        submitData.append("profile", formData.profile || "");

        // 添加头像文件（如果有）
        if (formData.avatar) {
          submitData.append("avatar", formData.avatar);
        }

        // 调用API更新用户信息
        const response = await updateOneUserInfo(userStore.userId, submitData);

        userStore.setUser(response.data.data);

        // 返回上一页
        router.back();
      } catch (error) {
        console.error("更新用户信息失败:", error);
        ElMessage.error(error.message || "更新个人信息失败，请稍后重试");
      }
    } else {
      ElMessage.warning("请检查表单填写是否正确");
      return false;
    }
  });
};

// 重置表单
const resetForm = () => {
  if (!formRef.value) return;
  formRef.value.resetFields();
  // 重置头像
  formData.avatar = null;
  formData.avatarUrl = userStore.avatarUrl || "";
};

// 初始化表单数据
onMounted(() => {
  // 从store中获取用户信息填充表单
  formData.username = userStore.username || "";
  formData.profile = userStore.profile || "";
  formData.avatarUrl = userStore.avatarUrl || "";
});
</script>

<style lang="scss" scoped>
.personal-info-edit {
  padding: 20px;

  .edit-card {
    max-width: 800px;
    margin: 0 auto;

    .card-header {
      font-size: 18px;
      font-weight: bold;
    }

    .edit-form {
      margin-top: 20px;
    }
  }

  .avatar-uploader {
    .avatar {
      width: 178px;
      height: 178px;
      display: block;
      border-radius: 6px;
    }

    :deep(.el-upload) {
      border: 1px dashed var(--el-border-color);
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: var(--el-transition-duration-fast);

      &:hover {
        border-color: var(--el-color-primary);
      }
    }

    .avatar-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 178px;
      height: 178px;
      text-align: center;
      line-height: 178px;
    }
  }
}
</style>
