<template>
  <div class="bind-company-container">
    <!-- 已绑定公司的状态 -->
    <el-card v-if="isCompanyBound" class="bound-company-card">
      <template #header>
        <div class="card-header">
          <span>当前绑定公司</span>
        </div>
      </template>

      <div class="company-info">
        <div class="company-name">{{ boundCompanyInfo.companyName }}</div>
        <div class="company-details">
          <div class="detail-item">
            <span class="label">状态：</span>
            <el-tag type="success">已绑定</el-tag>
          </div>
        </div>
      </div>

      <div class="action-buttons">
        <el-button type="danger" @click="showUnbindDialog">
          解绑公司
        </el-button>
      </div>
    </el-card>
    <el-dialog v-model="unbindDialogVisible" title="确认解绑" width="30%">
      <span>确定要解绑当前公司吗？解绑后将无法继续使用公司相关功能。</span>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="unbindDialogVisible = false">取消</el-button>
          <el-button
            type="danger"
            @click="confirmUnbind"
            :loading="unbindLoading"
            >确定</el-button
          >
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { getOwnBindingInfo, unbindCompany } from "@/api/binding";
import { useUserStore } from "@/stores/user";
import { getCompanyById } from "@/api/company";

const userStore = useUserStore();
const unbindLoading = ref(false);

// 解绑相关状态
const unbindDialogVisible = ref(false);
const boundCompanyInfo = reactive({
  companyName: "",
});

// 绑定申请记录相关状态
const records = ref([]);
const loadingRecords = ref(false);
const loadingMore = ref(false);
const noMoreData = ref(false);
const filterStatus = ref(null);
const pageNum = ref(1);
const pageSize = ref(10);
const totalRecords = ref(0);

// 判断是否已绑定公司
const isCompanyBound = computed(() => {
  return !!userStore.companyId;
});

const bindForm = reactive({
  companyId: null,
  applicationNotes: "",
});

// 状态文本映射
const statusTextMap = {
  0: "待审核",
  1: "已通过",
  2: "已拒绝",
  3: "已取消",
};

// 状态类型映射
const statusTypeMap = {
  0: "warning",
  1: "success",
  2: "danger",
  3: "info",
};

// 获取状态文本
const getStatusText = (status) => {
  return statusTextMap[status] || "未知";
};

// 获取状态类型
const getStatusType = (status) => {
  return statusTypeMap[status] || "";
};

// 截断文本
const truncateText = (text, maxLength) => {
  if (!text) return "";
  return text.length > maxLength ? text.substring(0, maxLength) + "..." : text;
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleString("zh-CN");
};

// 显示解绑对话框
const showUnbindDialog = () => {
  unbindDialogVisible.value = true;
};

const confirmUnbind = async () => {
  unbindLoading.value = true;
  try {
    const res = await unbindCompany(userStore.userId);
    if (res.data.data.code === 200) {
      // 更新store中的companyId为空
      userStore.companyId = null;
      userStore.companyName = null;

      // 如果返回了新的token，更新token
      if (res.data.data && res.data.data.token) {
        userStore.setToken(res.data.data.token);
      }

      ElMessage.success("解绑成功");
      unbindDialogVisible.value = false;

      // 刷新页面以更新状态
      setTimeout(() => {
        window.location.reload();
      }, 1000);
    } else {
      ElMessage.error(res.data.message || "解绑失败");
    }
  } catch (error) {
    console.error("解绑失败", error);
    ElMessage.error("解绑失败");
  } finally {
    unbindLoading.value = false;
  }
};
// 获取已绑定公司的信息
const fetchBoundCompanyInfo = async () => {
  if (!userStore.companyId) return;
  boundCompanyInfo.companyName = userStore.companyName || "公司信息加载中...";
};

// 获取绑定申请记录
const fetchBindingRecords = async (reset = false) => {
  if (reset) {
    pageNum.value = 1;
    records.value = [];
    noMoreData.value = false;
  }

  if (loadingMore.value || noMoreData.value) return;

  loadingMore.value = true;
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      userId: userStore.userId,
    };

    if (filterStatus.value !== null) {
      params.status = filterStatus.value;
    }

    const res = await getOwnBindingInfo(params);
    if (res.data.code === 200) {
      const newRecords = res.data.data.list || [];

      if (newRecords.length === 0) {
        noMoreData.value = true;
      } else {
        records.value = [...records.value, ...newRecords];
        totalRecords.value = res.data.data.total;

        // 检查是否还有更多数据
        if (records.value.length >= totalRecords.value) {
          noMoreData.value = true;
        }
      }
    } else {
      ElMessage.error(res.data.message || "获取申请记录失败");
    }
  } catch (error) {
    console.error("获取申请记录失败", error);
    ElMessage.error("获取申请记录失败");
  } finally {
    loadingMore.value = false;
    loadingRecords.value = false;
  }
};

// 组件挂载时获取申请记录和已绑定公司信息
onMounted(() => {
  loadingRecords.value = true;
  fetchBindingRecords();

  // 如果已绑定公司，获取公司信息
  if (isCompanyBound.value) {
    fetchBoundCompanyInfo();
  }
});
</script>

<style lang="scss" scoped>
.bind-company-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
  display: flex;
  align-items: center;
}

// 已绑定公司卡片样式
.bound-company-card {
  .company-info {
    padding: 20px 0;

    .company-name {
      font-size: 20px;
      font-weight: bold;
      margin-bottom: 15px;
      color: #303133;
    }

    .company-details {
      .detail-item {
        margin-bottom: 10px;

        .label {
          color: #909399;
          margin-right: 5px;
        }

        .value {
          color: #606266;
        }
      }
    }
  }

  .action-buttons {
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #ebeef5;
    text-align: right;
  }
}
</style>
