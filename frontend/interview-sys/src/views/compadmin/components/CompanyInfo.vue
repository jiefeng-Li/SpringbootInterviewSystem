<template>
  <div class="comp-main">
    <div v-if="hasCompany">
      <el-card v-loading="loadingCompany" class="company-info-card">
        <div class="company-header">
          <div class="company-name">
            {{ companyInfo?.companyName || "企业信息" }}
          </div>
          <el-button size="small" type="primary" @click="loadCompanyInfo"
            >刷新</el-button
          >
          <el-button type="primary" @click="goToEditPage"
            >修改企业信息</el-button
          >
        </div>

        <div class="company-detail">
          <div class="detail-row">
            <span class="detail-label">公司ID</span>
            <span class="detail-value">{{ companyInfo?.companyId }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">管理员</span>
            <span class="detail-value">{{ companyInfo?.adminName }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">行业</span>
            <span class="detail-value">{{ companyInfo?.industry }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">规模</span>
            <span class="detail-value">{{ companyInfo?.scale }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">城市</span>
            <span class="detail-value">{{ companyInfo?.city }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">官网</span>
            <span class="detail-value">
              <a
                v-if="companyInfo?.website"
                :href="companyInfo.website"
                target="_blank"
              >
                {{ companyInfo.website }}
              </a>
              <span v-else>未设置</span>
            </span>
          </div>
          <div class="detail-row">
            <span class="detail-label">状态</span>
            <span class="detail-value">{{
              getStatusText(companyInfo?.status)
            }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">公司Logo</span>
            <span class="detail-value">
              <el-image
                v-if="companyInfo?.logoUrl"
                :src="companyInfo.logoUrl"
                style="width: 100px; height: 100px"
                fit="contain"
                :preview-src-list="[companyInfo.logoUrl]"
              />
              <span v-else>未设置</span>
            </span>
          </div>
          <div class="detail-row">
            <span class="detail-label">营业执照</span>
            <span class="detail-value">
              <el-image
                v-if="companyInfo?.businessLicenseUrl"
                :src="companyInfo.businessLicenseUrl"
                style="width: 100px; height: 100px"
                fit="contain"
                :preview-src-list="[companyInfo.businessLicenseUrl]"
              />
              <span v-else>未设置</span>
            </span>
          </div>

          <div class="detail-row">
            <span class="detail-label">注册时间</span>
            <span class="detail-value">{{
              formatDate(companyInfo?.createTime)
            }}</span>
          </div>
        </div>

        <div class="company-intro">
          <h4>公司简介</h4>
          <p class="introduction-text">
            {{ companyInfo?.introduction || "暂无简介" }}
          </p>
        </div>
      </el-card>
    </div>

    <div v-else>
      <!-- 使用新创建的注册企业组件 -->
      <RegisterCompanyForm @register-submitted="handleRegisterSubmitted" />
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch, onMounted } from "vue";
import { useUserStore } from "@/stores/user";
import { ElMessage } from "element-plus";
import { getCompanyById } from "@/api/company";
import { useRouter } from "vue-router";
import RegisterCompanyForm from "./RegisterCompanyForm.vue"; // 导入注册企业组件

const router = useRouter();
const userStore = useUserStore();

const hasCompany = computed(() => !!userStore.companyId);

const companyInfo = ref(null);
const loadingCompany = ref(false);
const statusMap = {
  0: "待审",
  1: "正常",
  2: "驳回",
  3: "禁用",
};

const getStatusText = (status) => statusMap[status] || "未知";

const formatDate = (dateString) => {
  if (!dateString) return "未设置";
  const date = new Date(dateString);
  return date.toLocaleDateString("zh-CN");
};

const loadCompanyInfo = async () => {
  if (!userStore.companyId) return;
  loadingCompany.value = true;
  try {
    const res = await getCompanyById(userStore.companyId);
    if (res.data.code === 200) {
      companyInfo.value = res.data.data;
    } else {
      ElMessage.error(res.data.message || "获取企业信息失败");
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("获取企业信息失败");
  } finally {
    loadingCompany.value = false;
  }
};

// 处理绑定提交成功事件
const handleBindSubmitted = async () => {
  ElMessage.success("绑定申请已提交，请等待审核");
  // 切换到企业信息标签页，等待审核结果
  // 或者可以刷新页面以更新状态
};

// 处理注册提交成功事件
const handleRegisterSubmitted = async () => {
  ElMessage.success("企业注册申请已提交");
  // 刷新公司信息
  await loadCompanyInfo();
};

const goToEditPage = () => {
  router.push(`/company/edit/${userStore.companyId}`);
};

watch(hasCompany, (val) => {
  if (val) {
    loadCompanyInfo();
  }
});

onMounted(() => {
  if (userStore.isloginned && hasCompany.value) {
    loadCompanyInfo();
  }
});
</script>

<style scoped>
.comp-main {
  padding: 20px;
}

.company-info-card {
  width: 100%;
}

.company-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  .company-name {
    font-size: 22px;
    font-weight: 700;
    color: #303133;
  }
}

.company-detail {
  display: grid;
  grid-template-columns: 160px 1fr;
  row-gap: 12px;
  column-gap: 24px;
  padding: 12px 0;
  border-top: 1px solid #ebeef5;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 20px;

  .detail-row {
    display: contents;
  }

  .detail-label {
    text-align: right;
    padding-right: 12px;
    color: #606266;
    font-weight: 500;
  }

  .detail-value {
    color: #303133;
    word-break: break-word;
  }
  margin: 24px 0 16px;
}

.company-intro {
  h4 {
    font-size: 18px;
    margin: 0 0 10px;
    color: #303133;
  }
  p {
    white-space: pre-line;
    line-height: 1.8;
    color: #444;
    margin: 0;
  }
}

.detail-value .el-image {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.detail-value .el-image:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style>
