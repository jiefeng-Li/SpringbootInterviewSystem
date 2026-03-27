<template>
  <div class="company-edit">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>修改企业信息</span>
        </div>
      </template>

      <el-form
        :model="companyForm"
        :rules="rules"
        ref="formRef"
        label-width="120px"
      >
        <el-form-item label="公司名称" prop="companyName">
          <el-input
            v-model="companyForm.companyName"
            placeholder="请输入公司名称"
          ></el-input>
        </el-form-item>
        <el-form-item label="公司Logo" prop="logo">
          <el-upload
            class="logo-uploader"
            action="/api/upload"
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleLogoChange"
            :before-upload="beforeLogoUpload"
          >
            <img v-if="companyForm.logo" :src="companyForm.logo" class="logo" />
            <el-icon v-else class="logo-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="营业执照" prop="businessLicense">
          <el-upload
            class="license-uploader"
            action="/api/upload"
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleLicenseChange"
            :before-upload="beforeLicenseUpload"
          >
            <img
              v-if="companyForm.businessLicense"
              :src="companyForm.businessLicense"
              class="license"
            />
            <el-icon v-else class="license-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="官网链接" prop="website">
          <el-input
            v-model="companyForm.website"
            placeholder="请输入官网链接"
          ></el-input>
        </el-form-item>

        <el-form-item label="公司简介" prop="introduction">
          <el-input
            v-model="companyForm.introduction"
            type="textarea"
            :rows="10"
            placeholder="请输入公司简介"
          >
          </el-input>
        </el-form-item>

        <el-form-item label="公司行业" prop="industry">
          <el-input
            v-model="companyForm.industry"
            placeholder="请输入公司行业"
          ></el-input>
        </el-form-item>
        <el-form-item label="所在城市" prop="workCity">
          <el-cascader
            v-model="companyForm.city"
            :options="cityOptions"
            :props="cityProps"
            placeholder="请选择所在城市"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="公司规模" prop="scale">
          <el-select v-model="companyForm.scale" placeholder="请选择公司规模">
            <el-option label="0-20人" value="0-20人"></el-option>
            <el-option label="20-99人" value="20-99人"></el-option>
            <el-option label="100-499人" value="100-499人"></el-option>
            <el-option label="500-999人" value="500-999人"></el-option>
            <el-option label="1000-9999人" value="1000-9999人"></el-option>
            <el-option label="10000人以上" value="10000人以上"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="绑定管理员" prop="adminId">
          <el-autocomplete
            v-model="adminName"
            :fetch-suggestions="queryAdmins"
            placeholder="请输入管理员姓名"
            @select="handleAdminSelect"
            :trigger-on-focus="false"
            value-key="username"
          ></el-autocomplete>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm">保存修改</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import { getCompanyById, updateCompany } from "@/api/company";
import { getUserPage, getOneUser } from "@/api/user";
import { useUserStore } from "@/stores";
import { regionData } from "element-china-area-data";

const userStore = useUserStore();
const router = useRouter();
const route = useRoute();
const formRef = ref(null);
const adminName = ref("");

const cityOptions = ref(regionData);
const cityProps = {
  expandTrigger: "hover",
  value: "label",
  label: "label",
  children: "children",
};

// 获取公司ID
const companyId = route.query.companyId || localStorage.getItem("companyId");

// 表单数据
const companyForm = reactive({
  companyId: 0,
  companyName: "",
  logo: "",
  website: "",
  introduction: "",
  industry: "",
  scale: "",
  adminId: "",
  businessLicense: "",
  city: "",
});

// 表单验证规则
const rules = {
  companyName: [{ required: true, message: "请输入公司名称", trigger: "blur" }],
  website: [{ type: "url", message: "请输入正确的网址", trigger: "blur" }],
  industry: [{ required: true, message: "请输入公司行业", trigger: "blur" }],
  scale: [{ required: true, message: "请选择公司规模", trigger: "change" }],
  city: [{ required: true, message: "请选择所在城市", trigger: "change" }],
};

// 获取公司信息
const fetchCompanyInfo = async () => {
  try {
    const res = (await getCompanyById(userStore.companyId)).data;
    if (res.code === 200) {
      let compCity = [];
      if (res.data.city) {
        // 如果workCity是字符串，尝试分割为数组
        if (typeof res.data.city === "string") {
          compCity = res.data.city.split("/");
        } else if (Array.isArray(res.data.city)) {
          compCity = res.data.city;
        }
      }
      res.data.city = compCity;
      Object.assign(companyForm, res.data);
      // 如果有管理员ID，获取管理员姓名
      if (res.data.adminId) {
        // 这里需要调用获取用户信息的接口，假设有一个getUserById方法
        const admin = await getOneUser({ userId: res.data.adminId });
        adminName.value = admin.data.data.username;
      }
    } else {
      ElMessage.error(res.message || "获取公司信息失败");
    }
  } catch (error) {
    console.error("获取公司信息失败:", error);
    ElMessage.error("获取公司信息失败");
  }
};

// 查询管理员列表
const queryAdmins = async (queryString, cb) => {
  try {
    const res = (
      await getUserPage({
        username: queryString,
        companyId,
        pageNum: 1,
        pageSize: 10,
        role: "COMP_ADMIN",
      })
    ).data;
    console.log(res);
    if (res.code === 200 && res.data.list) {
      const results = res.data.list;
      cb(results);
    } else {
      cb([]);
    }
  } catch (error) {
    console.error("查询管理员失败:", error);
    cb([]);
  }
};

// 选择管理员
const handleAdminSelect = (item) => {
  companyForm.adminId = item.userId;
  adminName.value = item.username;
};

// 处理营业执照文件选择
const handleLicenseChange = (file) => {
  const isJPG = file.raw.type === "image/jpeg" || file.raw.type === "image/png";
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPG) {
    ElMessage.error("上传营业执照只能是JPG/PNG格式!");
    return false;
  }
  if (!isLt2M) {
    ElMessage.error("上传营业执照大小不能超过2MB!");
    return false;
  }

  // 预览图片
  companyForm.businessLicense = URL.createObjectURL(file.raw);
  return true;
};

// 处理Logo文件选择
const handleLogoChange = (file) => {
  const isJPG = file.raw.type === "image/jpeg" || file.raw.type === "image/png";
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPG) {
    ElMessage.error("上传Logo只能是JPG/PNG格式!");
    return false;
  }
  if (!isLt2M) {
    ElMessage.error("上传Logo大小不能超过2MB!");
    return false;
  }

  // 预览图片
  companyForm.logo = URL.createObjectURL(file.raw);
  return true;
};

// Logo上传前校验
const beforeLogoUpload = (file) => {
  const isJPG = file.type === "image/jpeg" || file.type === "image/png";
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPG) {
    ElMessage.error("上传Logo只能是JPG/PNG格式!");
  }
  if (!isLt2M) {
    ElMessage.error("上传Logo大小不能超过2MB!");
  }
  return isJPG && isLt2M;
};

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 创建FormData对象
        const formData = new FormData();

        const compCity = Array.isArray(companyForm.city)
          ? companyForm.city.join("/")
          : companyForm.city;
        companyForm.city = compCity;

        // 添加基本字段
        formData.append("companyId", companyForm.companyId);
        formData.append("adminId", companyForm.adminId);
        formData.append("companyName", companyForm.companyName);
        formData.append("website", companyForm.website);
        formData.append("introduction", companyForm.introduction);
        formData.append("industry", companyForm.industry);
        formData.append("scale", companyForm.scale);
        formData.append("city", companyForm.city);

        // 添加logo文件
        const logoInput = document.querySelector(
          '.logo-uploader input[type="file"]',
        );
        if (logoInput && logoInput.files[0]) {
          formData.append("logo", logoInput.files[0]);
        }

        // 添加营业执照文件
        const licenseInput = document.querySelector(
          '.license-uploader input[type="file"]',
        );
        if (licenseInput && licenseInput.files[0]) {
          formData.append("businessLicense", licenseInput.files[0]);
        }

        // 调用更新接口
        const res = await updateCompany(companyForm.companyId, formData);
        console.log("updateCompany res: ", res);
        if (res.data.code === 200) {
          ElMessage.success("修改成功");
          router.back();
        }
      } catch (error) {
        console.error("修改失败:", error);
        ElMessage.error("修改失败");
      }
    } else {
      ElMessage.warning("请填写完整信息");
      return false;
    }
  });
};

// 重置表单
const resetForm = () => {
  if (!formRef.value) return;
  formRef.value.resetFields();
  fetchCompanyInfo();
};

// 页面加载时获取公司信息
onMounted(() => {
  fetchCompanyInfo();
});
</script>

<style scoped>
.company-edit {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo-uploader,
.license-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 178px;
  height: 178px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-uploader:hover,
.license-uploader:hover {
  border-color: #409eff;
}

.logo-uploader-icon,
.license-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.logo,
.license {
  width: 100%;
  height: 100%;
  object-fit: contain;
}
</style>
