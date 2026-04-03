<template>
  <div class="comp-certification-container">
    <!-- 企业认证表单 -->
    <el-card class="form-card">
      <template #header>
        <div class="card-header">
          <span>企业认证</span>
        </div>
      </template>

      <el-form
        :model="certForm"
        :rules="rules"
        ref="formRef"
        label-width="120px"
        v-if="canSubmitCertification"
      >
        <el-form-item label="联系人" prop="contactName">
          <el-input
            v-model="certForm.contactName"
            placeholder="请输入联系人姓名"
          />
        </el-form-item>

        <el-form-item label="联系电话" prop="contactPhone">
          <el-input
            v-model="certForm.contactPhone"
            placeholder="请输入联系电话"
          />
        </el-form-item>

        <el-form-item label="联系邮箱" prop="contactEmail">
          <el-input
            v-model="certForm.contactEmail"
            placeholder="请输入联系邮箱"
          />
        </el-form-item>

        <el-form-item label="联系人职位" prop="contactPosition">
          <el-input
            v-model="certForm.contactPosition"
            placeholder="请输入联系人职位"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitting">
            提交认证
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
      <div v-else class="already-certified">
        <el-tag :type="!userStore.companyId ? 'info' : 'success'">
          {{ !userStore.companyId ? "未绑定公司" : "不可提交" }}
        </el-tag>
        <span style="margin-left: 10px">
          {{
            !userStore.companyId
              ? "请先注册并绑定公司后再提交认证申请。"
              : "仅公司状态为待审时可提交认证申请。"
          }}
        </span>
      </div>
    </el-card>

    <!-- 认证记录列表 -->
    <el-card class="list-card">
      <template #header>
        <div class="card-header">
          <span>认证记录</span>
          <el-select
            v-model="filterStatus"
            placeholder="全部状态"
            clearable
            @change="handleFilterChange"
            style="width: 120px"
          >
            <el-option label="待审" :value="0" />
            <el-option label="通过" :value="1" />
            <el-option label="驳回" :value="2" />
          </el-select>
        </div>
      </template>

      <el-table :data="certRecords" v-loading="loading" border stripe>
        <el-table-column prop="contactName" label="联系人" width="120" />
        <el-table-column prop="contactPhone" label="联系电话" width="150" />
        <el-table-column prop="contactEmail" label="联系邮箱" width="180" />
        <el-table-column
          prop="contactPosition"
          label="联系人职位"
          width="120"
        />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewDetail(row)">
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="认证详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="1" border v-if="currentRecord">
        <el-descriptions-item label="联系人">
          {{ currentRecord.contactName }}
        </el-descriptions-item>
        <el-descriptions-item label="联系电话">
          {{ currentRecord.contactPhone }}
        </el-descriptions-item>
        <el-descriptions-item label="联系邮箱">
          {{ currentRecord.contactEmail }}
        </el-descriptions-item>
        <el-descriptions-item label="联系人职位">
          {{ currentRecord.contactPosition }}
        </el-descriptions-item>
        <el-descriptions-item label="提交时间">
          {{ formatDate(currentRecord.createTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag :type="getStatusType(currentRecord.status)">
            {{ getStatusText(currentRecord.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item
          label="审核人"
          v-if="currentRecord.reviewedByName"
        >
          {{ currentRecord.reviewedByName }}
        </el-descriptions-item>
        <el-descriptions-item
          label="审核时间"
          v-if="currentRecord.reviewedTime"
        >
          {{ formatDate(currentRecord.reviewedTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="审核意见" v-if="currentRecord.reviewNotes">
          {{ currentRecord.reviewNotes }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from "vue";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/stores/user";
import { getCompanyById } from "@/api/company";
import {
  getCompanyCertificationList,
  addCompanyCertification,
} from "@/api/certification";

const userStore = useUserStore();
const formRef = ref(null);
const submitting = ref(false);
const companyStatus = ref(null);

const canSubmitCertification = computed(() => {
  return !!userStore.companyId && companyStatus.value === 0;
});

// 认证表单数据
const certForm = reactive({
  contactName: "",
  contactPhone: "",
  contactEmail: "",
  contactPosition: "",
});

// 表单验证规则
const rules = {
  contactName: [
    { required: true, message: "请输入联系人姓名", trigger: "blur" },
  ],
  contactPhone: [
    { required: true, message: "请输入联系电话", trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号码",
      trigger: "blur",
    },
  ],
  contactEmail: [
    { required: true, message: "请输入联系邮箱", trigger: "blur" },
    {
      type: "email",
      message: "请输入正确的邮箱地址",
      trigger: "blur",
    },
  ],
  contactPosition: [
    { required: true, message: "请输入联系人职位", trigger: "blur" },
  ],
};

// 认证记录列表
const certRecords = ref([]);
const loading = ref(false);
const total = ref(0);
const filterStatus = ref(null);

// 分页参数
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
});

// 详情弹窗相关
const detailDialogVisible = ref(false);
const currentRecord = ref(null);

// 状态文本映射
const statusTextMap = {
  0: "待审",
  1: "通过",
  2: "驳回",
};

// 状态类型映射
const statusTypeMap = {
  0: "warning",
  1: "success",
  2: "danger",
};

// 获取状态文本
const getStatusText = (status) => {
  return statusTextMap[status] || "未知";
};

// 获取状态类型
const getStatusType = (status) => {
  return statusTypeMap[status] || "";
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return "未设置";
  const date = new Date(dateString);
  return date.toLocaleString("zh-CN");
};

// 提交认证表单
const submitForm = async () => {
  if (!canSubmitCertification.value) {
    ElMessage.warning("仅公司状态为待审时可提交认证申请");
    return;
  }

  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        const res = await addCompanyCertification(certForm);
        if (res.data.code === 200) {
          ElMessage.success("认证申请已提交");
          resetForm();
          // 刷新认证记录列表
          fetchCertRecords();
        } else {
          ElMessage.error(res.data.message || "提交失败");
        }
      } catch (error) {
        console.error("提交认证失败", error);
        ElMessage.error("提交认证失败");
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
};

// 获取认证记录列表
const fetchCertRecords = async () => {
  if (!userStore.companyId) {
    certRecords.value = [];
    total.value = 0;
    return;
  }

  loading.value = true;
  try {
    const params = {
      companyId: userStore.companyId,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
    };

    if (filterStatus.value !== null) {
      params.status = filterStatus.value;
    }

    const res = await getCompanyCertificationList(params);
    if (res.data.code === 200) {
      certRecords.value = res.data.data.list || [];
      total.value = res.data.data.total || 0;
    } else {
      ElMessage.error(res.data.message || "获取认证记录失败");
    }
  } catch (error) {
    console.error("获取认证记录失败", error);
    ElMessage.error("获取认证记录失败");
  } finally {
    loading.value = false;
  }
};

// 获取公司状态
const fetchCompanyStatus = async () => {
  if (!userStore.companyId) {
    companyStatus.value = null;
    return;
  }

  try {
    const res = await getCompanyById(userStore.companyId);
    if (res.data.code === 200) {
      companyStatus.value = res.data.data?.status ?? null;
    } else {
      companyStatus.value = null;
      ElMessage.error(res.data.message || "获取公司状态失败");
    }
  } catch (error) {
    companyStatus.value = null;
    console.error("获取公司状态失败", error);
    ElMessage.error("获取公司状态失败");
  }
};

// 处理筛选条件变化
const handleFilterChange = () => {
  pagination.pageNum = 1;
  fetchCertRecords();
};

// 处理页码变化
const handleCurrentChange = (val) => {
  pagination.pageNum = val;
  fetchCertRecords();
};

// 处理每页数量变化
const handleSizeChange = (val) => {
  pagination.pageSize = val;
  pagination.pageNum = 1;
  fetchCertRecords();
};

// 查看详情
const handleViewDetail = (record) => {
  currentRecord.value = record;
  detailDialogVisible.value = true;
};

// 组件挂载时获取认证记录
onMounted(() => {
  fetchCompanyStatus();
  fetchCertRecords();
});
</script>

<style lang="scss" scoped>
.comp-certification-container {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.form-card {
  width: 100%;
}

.list-card {
  flex: 1;
  display: flex;
  flex-direction: column;

  :deep(.el-card__body) {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 0;
  }

  .el-table {
    flex: 1;
  }

  .pagination-container {
    padding: 15px 20px;
    display: flex;
    justify-content: flex-end;
    border-top: 1px solid #ebeef5;
  }
}
</style>
