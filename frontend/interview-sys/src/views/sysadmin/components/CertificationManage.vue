<template>
  <div class="certification-manage">
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="query" @submit.prevent>
        <el-form-item label="公司ID">
          <el-input
            v-model="query.companyId"
            placeholder="输入公司ID"
            clearable
            style="width: 160px"
          />
        </el-form-item>

        <el-form-item label="审核状态">
          <el-select
            v-model="query.status"
            placeholder="全部状态"
            clearable
            style="width: 180px"
          >
            <el-option
              v-for="item in statusOptions"
              :key="item.status"
              :label="item.text"
              :value="item.status"
            />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="table-card">
      <el-table :data="tableData" v-loading="tableLoading" border stripe>
        <el-table-column type="index" label="#" width="60" />
        <el-table-column prop="companyName" label="公司" min-width="160" />
        <el-table-column prop="adminName" label="管理员" min-width="120" />
        <el-table-column prop="contactName" label="联系人" min-width="120" />
        <el-table-column prop="contactPhone" label="联系电话" min-width="140" />
        <el-table-column label="审核状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="reviewNotes"
          label="审核意见"
          min-width="220"
          show-overflow-tooltip
        />
        <el-table-column prop="createTime" label="提交时间" min-width="140" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              type="primary"
              link
              @click="openReviewDialog(row)"
            >
              审核
            </el-button>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :page-sizes="[10, 20, 50]"
          @current-change="fetchList"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="reviewVisible"
      title="审核企业认证"
      width="520px"
      destroy-on-close
    >
      <template v-if="currentRow">
        <el-descriptions :column="1" border size="small" class="review-top">
          <el-descriptions-item label="公司">{{
            currentRow.companyName || "-"
          }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{
            currentRow.contactName || "-"
          }}</el-descriptions-item>
        </el-descriptions>

        <el-form :model="reviewForm" label-width="90px" class="review-form">
          <el-form-item label="审核结果" required>
            <el-radio-group v-model="reviewForm.status">
              <el-radio :value="1">通过</el-radio>
              <el-radio :value="2">驳回</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="审核意见">
            <el-input
              v-model="reviewForm.reviewNotes"
              type="textarea"
              :rows="4"
              maxlength="500"
              show-word-limit
              placeholder="请输入审核意见"
            />
          </el-form-item>
        </el-form>
      </template>

      <template #footer>
        <el-button @click="reviewVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="submitLoading"
          @click="submitReview"
        >
          确认提交
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import {
  getCompanyCertificationList,
  getCompanyCertificationStatus,
  reviewCompanyCertification,
} from "@/api/certification";

const query = reactive({
  companyId: undefined,
  status: undefined,
  isDeleted: 0,
  pageNum: 1,
  pageSize: 10,
});

const tableLoading = ref(false);
const tableData = ref([]);
const total = ref(0);
const statusOptions = ref([]);

const reviewVisible = ref(false);
const submitLoading = ref(false);
const currentRow = ref(null);
const reviewForm = reactive({
  status: 1,
  reviewNotes: "",
});

const statusText = (status) => {
  const target = statusOptions.value.find((item) => item.status === status);
  return target?.text || `状态${status}`;
};

const statusTagType = (status) => {
  switch (status) {
    case 0:
      return "warning";
    case 1:
      return "success";
    case 2:
      return "danger";
    case 3:
      return "info";
    default:
      return "info";
  }
};

const fetchStatusOptions = async () => {
  try {
    const { data } = await getCompanyCertificationStatus();
    statusOptions.value = data?.data || data || [];
  } catch {
    statusOptions.value = [];
  }
};

const fetchList = async () => {
  tableLoading.value = true;
  try {
    const { data } = await getCompanyCertificationList({
      ...query,
      companyId: query.companyId ? Number(query.companyId) : undefined,
    });
    const page = data?.data || data || {};
    tableData.value = page.list || page.records || [];
    total.value = page.total || 0;
  } catch (e) {
    ElMessage.error(e?.message || "获取认证申请失败");
  } finally {
    tableLoading.value = false;
  }
};

const handleSearch = () => {
  query.pageNum = 1;
  fetchList();
};

const handleReset = () => {
  query.companyId = undefined;
  query.status = undefined;
  query.pageNum = 1;
  query.pageSize = 10;
  fetchList();
};

const handleSizeChange = () => {
  query.pageNum = 1;
  fetchList();
};

const openReviewDialog = (row) => {
  currentRow.value = row;
  reviewForm.status = 1;
  reviewForm.reviewNotes = "";
  reviewVisible.value = true;
};

const submitReview = async () => {
  if (!currentRow.value?.id) {
    ElMessage.error("认证记录ID不存在");
    return;
  }

  submitLoading.value = true;
  try {
    await reviewCompanyCertification(currentRow.value.id, {
      id: currentRow.value.id,
      status: reviewForm.status,
      reviewNotes: reviewForm.reviewNotes?.trim() || "",
    });
    ElMessage.success("审核成功");
    reviewVisible.value = false;
    fetchList();
  } catch (e) {
    ElMessage.error(e?.message || "审核失败");
  } finally {
    submitLoading.value = false;
  }
};

onMounted(async () => {
  await fetchStatusOptions();
  fetchList();
});
</script>

<style lang="scss" scoped>
.certification-manage {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .pager {
    margin-top: 16px;
    display: flex;
    justify-content: flex-end;
  }

  .review-top {
    margin-bottom: 12px;
  }
}
</style>
