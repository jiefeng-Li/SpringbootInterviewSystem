<template>
  <div class="bind-company-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>绑定公司</span>
        </div>
      </template>

      <el-form
        :model="bindForm"
        :rules="rules"
        ref="formRef"
        label-width="120px"
      >
        <el-form-item label="公司名称" prop="companyId">
          <el-autocomplete
            v-model="companyName"
            :fetch-suggestions="queryCompanies"
            placeholder="请输入公司名称"
            @select="handleCompanySelect"
            :trigger-on-focus="false"
            value-key="companyName"
            style="width: 100%"
          >
            <template #default="{ item }">
              <div class="company-item">
                <div class="name">{{ item.companyName }}</div>
                <div class="info">{{ item.industry }} | {{ item.city }}</div>
              </div>
            </template>
          </el-autocomplete>
        </el-form-item>

        <el-form-item label="申请理由" prop="applicationNotes">
          <el-input
            v-model="bindForm.applicationNotes"
            type="textarea"
            :rows="4"
            placeholder="请输入申请理由"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitting"
            >提交申请</el-button
          >
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 添加绑定申请记录部分 -->
    <el-card class="binding-records-card">
      <template #header>
        <div class="card-header">
          <span>绑定申请记录</span>
          <el-select
            v-model="filterStatus"
            placeholder="全部状态"
            clearable
            @change="handleFilterChange"
            style="width: 120px; margin-left: 20px"
          >
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </div>
      </template>

      <el-scrollbar height="calc(100vh - 500px)" @scroll="handleScroll">
        <div v-loading="loadingRecords">
          <el-empty
            v-if="records.length === 0 && !loadingRecords"
            description="暂无申请记录"
          />

          <div v-else class="records-list">
            <div v-for="record in records" :key="record.id" class="record-item">
              <div class="record-header">
                <span class="company-name">{{ record.companyName }}</span>
                <el-tag :type="getStatusType(record.status)">
                  {{ getStatusText(record.status) }}
                </el-tag>
              </div>
              <div class="record-content">
                <div class="notes">
                  <span class="label">申请理由：</span>
                  <span class="text">{{
                    truncateText(record.applicationNotes, 100)
                  }}</span>
                </div>
                <div v-if="record.status !== 0" class="review-info">
                  <div class="reviewer">
                    <span class="label">审核人：</span>
                    <span class="text">{{ record.reviewedByName }}</span>
                  </div>
                  <div v-if="record.reviewNotes" class="review-notes">
                    <span class="label">审核意见：</span>
                    <span class="text">{{ record.reviewNotes }}</span>
                  </div>
                  <div class="review-time">
                    <span class="label">审核时间：</span>
                    <span class="text">{{
                      formatDate(record.reviewedTime)
                    }}</span>
                  </div>
                </div>
                <div class="record-footer">
                  <span class="create-time"
                    >申请时间：{{ formatDate(record.createTime) }}</span
                  >
                  <div class="actions">
                    <el-button
                      v-if="record.status === 0"
                      type="danger"
                      size="small"
                      @click="cancelApplication(record.id)"
                      :loading="cancellingId === record.id"
                      :disabled="cancellingId !== null"
                    >
                      {{
                        cancellingId === record.id ? "取消中..." : "取消申请"
                      }}
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div v-if="loadingMore" class="loading-more">
            <el-icon class="is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>

          <div v-if="noMoreData && records.length > 0" class="no-more-data">
            没有更多数据了
          </div>
        </div>
      </el-scrollbar>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { getCompanyList } from "@/api/company";
import {
  bindCompany,
  getOwnBindingInfo,
  cancelBindingCompany,
} from "@/api/binding";
import { Loading } from "@element-plus/icons-vue";
import { useUserStore } from "@/stores/user";

const router = useRouter();
const userStore = useUserStore();
const formRef = ref(null);
const companyName = ref("");
const submitting = ref(false);
const cancellingId = ref(null);

// 绑定申请记录相关状态
const records = ref([]);
const loadingRecords = ref(false);
const loadingMore = ref(false);
const noMoreData = ref(false);
const filterStatus = ref(null);
const pageNum = ref(1);
const pageSize = ref(10);
const totalRecords = ref(0);

const bindForm = reactive({
  companyId: null,
  applicationNotes: "",
});

const rules = {
  companyId: [{ required: true, message: "请选择公司", trigger: "change" }],
  applicationNotes: [
    { required: true, message: "请输入申请理由", trigger: "blur" },
    { min: 10, max: 500, message: "长度在 10 到 500 个字符", trigger: "blur" },
  ],
};

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

// 远程搜索公司
const queryCompanies = async (queryString, cb) => {
  if (!queryString || queryString.trim() === "") {
    cb([]);
    return;
  }

  try {
    const res = await getCompanyList({
      companyName: queryString,
      pageNum: 1,
      pageSize: 10,
    });
    if (res.data.code === 200) {
      cb(res.data.data.list || []);
    } else {
      ElMessage.error(res.data.message || "搜索公司失败");
      cb([]);
    }
  } catch (error) {
    console.error("搜索公司失败", error);
    ElMessage.error("搜索公司失败");
    cb([]);
  }
};

// 选择公司
const handleCompanySelect = (item) => {
  bindForm.companyId = item.companyId;
  companyName.value = item.companyName;
};

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        const payload = {
          companyId: bindForm.companyId,
          applicationNotes: bindForm.applicationNotes,
        };

        const res = await bindCompany(payload);
        if (res.data.code === 200) {
          ElMessage.success("绑定申请已提交，请等待审核");
          resetForm();
          // 刷新申请记录
          fetchBindingRecords(true);
        } else {
          ElMessage.error(res.data.message || "提交申请失败");
        }
      } catch (error) {
        console.error("提交申请失败", error);
        ElMessage.error("提交申请失败");
      } finally {
        submitting.value = false;
      }
    }
  });
};

// 重置表单
const resetForm = () => {
  if (!formRef.value) return;
  formRef.value.resetFields();
  companyName.value = "";
  bindForm.companyId = null;
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

// 处理滚动事件
const handleScroll = ({ scrollTop, scrollHeight, clientHeight }) => {
  // 当滚动到底部附近时加载更多数据
  if (
    scrollTop + clientHeight >= scrollHeight - 50 &&
    !loadingMore.value &&
    !noMoreData.value
  ) {
    pageNum.value++;
    fetchBindingRecords();
  }
};

// 处理筛选条件变化
const handleFilterChange = () => {
  fetchBindingRecords(true);
};

// 取消申请
const cancelApplication = async (id) => {
  // 防止重复点击
  if (cancellingId.value === id) return;

  try {
    // 显示确认对话框
    await ElMessageBox.confirm("确定要取消此申请吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    // 设置加载状态
    cancellingId.value = id;

    // 调用取消申请的API
    const res = await cancelBindingCompany(id);

    // 检查API响应状态
    if (res.data.code === 200) {
      // 更新本地状态
      const index = records.value.findIndex((record) => record.id === id);
      if (index !== -1) {
        records.value[index].status = 3; // 设置为已取消
        ElMessage.success("已取消申请");
      }
    } else {
      // API返回错误状态
      ElMessage.error(res.data.message || "取消申请失败");
    }
  } catch (error) {
    // 区分用户取消操作和API调用失败
    if (error === "cancel") {
      // 用户点击了取消按钮，不需要处理
      console.log("用户取消了操作");
    } else {
      // API调用失败或其他错误
      console.error("取消申请失败", error);
      ElMessage.error("取消申请失败：" + (error.message || "未知错误"));
    }
  } finally {
    // 无论成功或失败，都重置加载状态
    cancellingId.value = null;
  }
};

// 组件挂载时获取申请记录
onMounted(() => {
  loadingRecords.value = true;
  fetchBindingRecords();
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

.binding-records-card {
  flex: 1;
  display: flex;
  flex-direction: column;

  :deep(.el-card__body) {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 0;
  }
}

.records-list {
  padding: 10px;
}

.record-item {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 15px;
  margin-bottom: 15px;
  transition: all 0.3s;

  &:hover {
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  }
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;

  .company-name {
    font-size: 16px;
    font-weight: bold;
    color: #303133;
  }
}

.record-content {
  .notes,
  .reviewer,
  .review-notes,
  .review-time {
    margin-bottom: 8px;

    .label {
      color: #909399;
      margin-right: 5px;
    }

    .text {
      color: #606266;
    }
  }

  .review-info {
    margin-top: 10px;
    padding-top: 10px;
    border-top: 1px dashed #ebeef5;
  }
}

.record-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #ebeef5;

  .create-time {
    font-size: 12px;
    color: #909399;
  }
}

.company-item {
  .name {
    font-weight: bold;
    font-size: 14px;
  }

  .info {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
  }
}

.loading-more,
.no-more-data {
  text-align: center;
  padding: 15px 0;
  color: #909399;
  font-size: 14px;

  .el-icon {
    margin-right: 5px;
  }
}
</style>
