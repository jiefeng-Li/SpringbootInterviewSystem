<template>
  <el-card class="register-company-card">
    <template #header>
      <div class="card-header">
        <span>注册企业</span>
      </div>
    </template>

    <el-form
      ref="formRef"
      :model="registerForm"
      :rules="rules"
      label-width="110px"
    >
      <el-form-item label="企业名称" prop="companyName">
        <el-input v-model="registerForm.companyName" />
      </el-form-item>

      <el-form-item label="官网" prop="website">
        <el-input v-model="registerForm.website" />
      </el-form-item>

      <el-form-item label="行业" prop="industry">
        <el-select
          v-model="registerForm.industry"
          placeholder="行业类型"
          style="width: 240px; margin-left: 24px"
        >
          <el-option
            v-for="item in industrys"
            :key="item.label"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="规模" prop="scale">
        <el-select
          v-model="registerForm.scale"
          placeholder="公司规模"
          style="width: 240px; margin-left: 24px"
        >
          <el-option
            v-for="item in scales"
            :key="item.label"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="城市" prop="city">
        <!-- <el-input v-model="registerForm.city" /> -->
        <el-cascader
          v-model="registerForm.city"
          :options="cityOptions"
          :props="cityProps"
          placeholder="请选择工作城市"
          style="width: 100%"
        />
      </el-form-item>

      <el-form-item label="简介" prop="introduction">
        <el-input
          v-model="registerForm.introduction"
          type="textarea"
          :rows="3"
          maxlength="1024"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="Logo">
        <el-upload
          class="logo-uploader"
          action=""
          :auto-upload="false"
          :show-file-list="false"
          :on-change="handleLogoChange"
          :before-upload="beforeLogoUpload"
        >
          <img v-if="logoUrl" :src="logoUrl" class="logo" />
          <el-icon v-else class="logo-uploader-icon"><Plus /></el-icon>
        </el-upload>
      </el-form-item>

      <el-form-item label="营业执照">
        <el-upload
          class="license-uploader"
          action=""
          :auto-upload="false"
          :show-file-list="false"
          :on-change="handleLicenseChange"
          :before-upload="beforeLicenseUpload"
        >
          <img v-if="licenseUrl" :src="licenseUrl" class="license" />
          <el-icon v-else class="license-uploader-icon"><Plus /></el-icon>
        </el-upload>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submitRegister" :loading="submitting">
          提交注册申请
        </el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, reactive } from "vue";
import { ElMessage } from "element-plus";
import { registerCompany } from "@/api/company";
import { useUserStore } from "@/stores/user";
import { regionData } from "element-china-area-data";
import { Plus } from "@element-plus/icons-vue";

const userStore = useUserStore();
const formRef = ref(null);
const submitting = ref(false);
const cityOptions = ref(regionData);

const logoUrl = ref("");
const licenseUrl = ref("");

// 城市选项配置
const cityProps = {
  expandTrigger: "hover",
  value: "label",
  label: "label",
  children: "children",
};

const registerForm = reactive({
  companyName: "",
  website: "",
  introduction: "",
  industry: "",
  scale: "",
  city: "",
});

const scales = [
  {
    value: undefined,
    label: "不限",
  },
  {
    value: "0-20人",
    label: "0-20人",
  },
  {
    value: "20-99人",
    label: "20-99人",
  },
  {
    value: "100-499人",
    label: "100-499人",
  },
  {
    value: "500-999人",
    label: "500-999人",
  },
  {
    value: "1000-9999人",
    label: "1000-9999人",
  },
  {
    value: "10000人以上",
    label: "10000人以上",
  },
];

const industrys = [
  { value: "互联网", label: "互联网" },
  { value: "电子商务", label: "电子商务" },
  { value: "软件开发", label: "软件开发" },
  { value: "硬件制造", label: "硬件制造" },
  { value: "半导体", label: "半导体" },
  { value: "人工智能", label: "人工智能" },
  { value: "大数据", label: "大数据" },
  { value: "云计算", label: "云计算" },
  { value: "物联网", label: "物联网" },
  { value: "金融科技", label: "金融科技" },
  { value: "教育培训", label: "教育培训" },
  { value: "文化传媒", label: "文化传媒" },
  { value: "广告营销", label: "广告营销" },
  { value: "医疗健康", label: "医疗健康" },
  { value: "生物技术", label: "生物技术" },
  { value: "制药", label: "制药" },
  { value: "医疗器械", label: "医疗器械" },
  { value: "房地产", label: "房地产" },
  { value: "建筑工程", label: "建筑工程" },
  { value: "物业管理", label: "物业管理" },
  { value: "制造业", label: "制造业" },
  { value: "汽车制造", label: "汽车制造" },
  { value: "新能源", label: "新能源" },
  { value: "环保", label: "环保" },
  { value: "化工", label: "化工" },
  { value: "消费品", label: "消费品" },
  { value: "零售", label: "零售" },
  { value: "批发", label: "批发" },
  { value: "餐饮", label: "餐饮" },
  { value: "酒店旅游", label: "酒店旅游" },
  { value: "物流运输", label: "物流运输" },
  { value: "供应链", label: "供应链" },
  { value: "咨询服务", label: "咨询服务" },
  { value: "法律服务", label: "法律服务" },
  { value: "会计审计", label: "会计审计" },
  { value: "人力资源", label: "人力资源" },
  { value: "保险", label: "保险" },
  { value: "证券基金", label: "证券基金" },
  { value: "银行", label: "银行" },
  { value: "信托", label: "信托" },
  { value: "农业", label: "农业" },
  { value: "林业", label: "林业" },
  { value: "畜牧业", label: "畜牧业" },
  { value: "渔业", label: "渔业" },
  { value: "采矿业", label: "采矿业" },
  { value: "能源", label: "能源" },
  { value: "电力", label: "电力" },
  { value: "水利", label: "水利" },
  { value: "电信", label: "电信" },
  { value: "广播电视", label: "广播电视" },
  { value: "邮政", label: "邮政" },
  { value: "体育", label: "体育" },
  { value: "娱乐", label: "娱乐" },
  { value: "非营利组织", label: "非营利组织" },
  { value: "政府机构", label: "政府机构" },
  { value: "国际组织", label: "国际组织" },
];

const logoFile = ref(null);
const licenseFile = ref(null);

const rules = {
  companyName: [{ required: true, message: "请输入企业名称", trigger: "blur" }],
  industry: [{ required: true, message: "请输入行业", trigger: "blur" }],
  scale: [{ required: true, message: "请输入规模", trigger: "blur" }],
  city: [{ required: true, message: "请输入城市", trigger: "change" }],
  introduction: [
    { required: true, message: "请输入简介", trigger: "blur" },
    { max: 1024, message: "公司简介过长", trigger: "blur" },
  ],
};

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

  logoFile.value = file.raw;
  // 创建图片预览URL
  logoUrl.value = URL.createObjectURL(file.raw);
  return true;
};

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

// 移除Logo
const handleLogoRemove = () => {
  logoFile.value = null;
  logoUrl.value = "";
  URL.revokeObjectURL(logoUrl.value);
};

const beforeLicenseUpload = (file) => {
  const isJPG = file.type === "image/jpeg" || file.type === "image/png";
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPG) {
    ElMessage.error("上传营业执照只能是JPG/PNG格式!");
  }
  if (!isLt2M) {
    ElMessage.error("上传营业执照大小不能超过2MB!");
  }
  return isJPG && isLt2M;
};

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

  licenseFile.value = file.raw;
  // 创建图片预览URL
  licenseUrl.value = URL.createObjectURL(file.raw);
  return true;
};

// 移除营业执照
const handleLicenseRemove = () => {
  licenseFile.value = null;
  licenseUrl.value = "";
  URL.revokeObjectURL(licenseUrl.value);
};

// 提交注册
const submitRegister = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      const cityStr = Array.isArray(registerForm.city)
        ? registerForm.city.join("/")
        : registerForm.city;
      registerForm.city = cityStr;
      try {
        const formData = new FormData();
        formData.append("companyName", registerForm.companyName);
        formData.append("website", registerForm.website);
        formData.append("introduction", registerForm.introduction);
        formData.append("industry", registerForm.industry);
        formData.append("scale", registerForm.scale);
        formData.append("city", registerForm.city);

        if (logoFile.value) {
          formData.append("logo", logoFile.value);
        }

        if (licenseFile.value) {
          formData.append("businessLicense", licenseFile.value);
        }
        const res = await registerCompany(formData);
        if (res.data.code === 200) {
          ElMessage.success("企业注册申请已提交");
          if (res.data.data?.companyId) {
            userStore.companyId = res.data.data.companyId;
            // 通知父组件刷新数据
            emit("registerSubmitted");
          }
        } else {
          ElMessage.error(res.data.message || "注册失败");
        }
      } catch (error) {
        console.error("注册失败", error);
        ElMessage.error("注册失败");
      } finally {
        submitting.value = false;
      }
    }
  });
};

// 定义事件
const emit = defineEmits(["registerSubmitted"]);
</script>

<style scoped>
.register-company-card {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
  display: flex;
  align-items: center;
}

.upload-demo {
  width: 100%;
}

.file-name {
  margin-top: 8px;
  color: #606266;
}

.image-preview {
  margin-top: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  padding: 10px;
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
