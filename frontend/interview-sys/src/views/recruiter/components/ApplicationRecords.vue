<template>
  <div class="application-records">
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="query" @submit.prevent>
        <el-form-item label="职位">
          <el-input
            v-model="query.jobPositionId"
            placeholder="输入职位ID"
            clearable
            style="width: 180px"
          />
        </el-form-item>

        <el-form-item label="状态">
          <el-select
            v-model="query.status"
            placeholder="全部状态"
            clearable
            style="width: 180px"
          >
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
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
      <el-table
        :data="tableData"
        v-loading="tableLoading"
        border
        stripe
        @row-click="openDetail"
      >
        <el-table-column type="index" label="#" width="60" />
        <el-table-column prop="jobSeekerName" label="求职者" min-width="120" />
        <el-table-column prop="jobTitle" label="职位" min-width="150" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{
              statusLabel(row.status)
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="投递时间" min-width="180" />
        <el-table-column
          prop="remarks"
          label="备注"
          min-width="200"
          show-overflow-tooltip
        />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click.stop="openDetail(row)"
              >查看</el-button
            >
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
      v-model="detailVisible"
      title="投递详情"
      width="80%"
      top="5vh"
      destroy-on-close
    >
      <template v-if="currentRow">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="求职者">{{
            currentRow.jobSeekerName || "-"
          }}</el-descriptions-item>
          <el-descriptions-item label="职位">{{
            currentRow.jobTitle || "-"
          }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusTagType(currentRow.status)">{{
              statusLabel(currentRow.status)
            }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="投递时间">{{
            currentRow.applyTime || "-"
          }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{
            currentRow.remarks || "-"
          }}</el-descriptions-item>
        </el-descriptions>

        <div class="resume-wrap">
          <div class="resume-title">简历预览</div>
          <el-skeleton :loading="resumeLoading" animated :rows="8">
            <div v-if="resumeError" class="resume-error">{{ resumeError }}</div>
            <div v-else class="resume-html" v-html="resumeHtml"></div>
          </el-skeleton>
        </div>

        <div class="review-wrap">
          <div class="review-title">处理投递</div>
          <el-form :model="reviewForm" label-width="80px">
            <el-form-item label="状态">
              <el-select
                v-model="reviewForm.status"
                placeholder="请选择状态"
                style="width: 220px"
              >
                <el-option
                  v-for="item in statusOptions.slice(1, 4)"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="备注">
              <el-input
                v-model="reviewForm.remarks"
                type="textarea"
                :rows="4"
                maxlength="1024"
                show-word-limit
                placeholder="请输入备注"
              />
            </el-form-item>
          </el-form>
        </div>
      </template>

      <template #footer>
        <el-button @click="detailVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="reviewLoading"
          @click="submitReview"
        >
          保存处理结果
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { getJobApplicationList, reviewJobApplication } from "@/api/application";
import { previewResume } from "@/api/resume";
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();

const statusOptions = [
  { label: "待处理", value: 0 },
  { label: "已查看", value: 1 },
  { label: "初筛通过", value: 2 },
  { label: "初筛不通过", value: 3 },
  { label: "面试中", value: 4 },
  { label: "已发 Offer", value: 5 },
  { label: "已录用", value: 6 },
  { label: "已淘汰", value: 7 },
];

const statusMap = computed(() =>
  statusOptions.reduce((acc, cur) => {
    acc[cur.value] = cur.label;
    return acc;
  }, {}),
);

const query = reactive({
  companyId: userStore?.companyId || undefined,
  jobPositionId: undefined,
  status: undefined,
  pageNum: 1,
  pageSize: 10,
});

const tableLoading = ref(false);
const tableData = ref([]);
const total = ref(0);

const detailVisible = ref(false);
const currentRow = ref(null);

const resumeLoading = ref(false);
const resumeHtml = ref("");
const resumeError = ref("");
const reviewLoading = ref(false);
const reviewForm = reactive({
  status: undefined,
  remarks: "",
});

const statusTypeMap = {
  0: "info",
  1: "",
  2: "success",
  3: "danger",
  4: "warning",
  5: "success",
  6: "success",
  7: "danger",
};

const statusLabel = (status) => {
  return statusMap.value?.[status] ?? `未知(${status})`;
};

const statusTagType = (status) => {
  return statusTypeMap[status] ?? "info";
};

const fetchList = async () => {
  if (!query.companyId) {
    ElMessage.warning("未获取到公司ID，请重新登录后重试");
    return;
  }
  tableLoading.value = true;
  try {
    const { data } = await getJobApplicationList({
      companyId: query.companyId,
      jobPositionId: query.jobPositionId || undefined,
      status: query.status,
      pageNum: query.pageNum,
      pageSize: query.pageSize,
    });

    // 常见后端分页结构兼容
    const page = data?.data || data || {};
    console.log("获取投递记录列表成功:", page);
    tableData.value = page.records || page.list || [];
    total.value = page.total || 0;
  } catch (e) {
    ElMessage.error(e?.message || "获取投递记录失败");
  } finally {
    tableLoading.value = false;
  }
};

const handleSearch = () => {
  query.pageNum = 1;
  fetchList();
};

const handleReset = () => {
  query.jobPositionId = undefined;
  query.status = undefined;
  query.pageNum = 1;
  query.pageSize = 10;
  fetchList();
};

const handleSizeChange = () => {
  query.pageNum = 1;
  fetchList();
};

const openDetail = async (row) => {
  currentRow.value = row;
  detailVisible.value = true;
  resumeHtml.value = "";
  resumeError.value = "";
  reviewForm.status = row?.status;
  reviewForm.remarks = row?.remarks || "";

  if (row?.id && row?.status === 0) {
    try {
      await reviewJobApplication({
        id: row.id,
        status: 1,
        remarks: row?.remarks || "",
      });
      row.status = 1;
      if (currentRow.value?.id === row.id) {
        currentRow.value.status = 1;
      }
      reviewForm.status = 1;
    } catch (e) {
      ElMessage.warning(e?.message || "自动标记为已查看失败");
    }
  }

  const resumeId = row?.resumeId;
  if (!resumeId) {
    resumeError.value = "该投递未关联简历ID";
    return;
  }

  resumeLoading.value = true;
  try {
    const { data } = await previewResume(row.resumeId, row.resume.templateId);
    const html = data?.data || data || "";
    resumeHtml.value = typeof html === "string" ? html : "";
    if (!resumeHtml.value) {
      resumeError.value = "简历内容为空";
    }
  } catch (e) {
    resumeError.value = e?.message || "简历预览失败";
  } finally {
    resumeLoading.value = false;
  }
};

const submitReview = async () => {
  if (!currentRow.value?.id) {
    ElMessage.error("投递记录ID不存在");
    return;
  }
  if (reviewForm.status === undefined || reviewForm.status === null) {
    ElMessage.warning("请选择状态");
    return;
  }

  reviewLoading.value = true;
  try {
    await reviewJobApplication({
      id: currentRow.value.id,
      status: reviewForm.status,
      remarks: reviewForm.remarks?.trim() || "",
    });

    currentRow.value.status = reviewForm.status;
    currentRow.value.remarks = reviewForm.remarks?.trim() || "";
    const target = tableData.value.find(
      (item) => item.id === currentRow.value.id,
    );
    if (target) {
      target.status = currentRow.value.status;
      target.remarks = currentRow.value.remarks;
    }

    ElMessage.success("处理结果已保存");
  } catch (e) {
    ElMessage.error(e?.message || "保存处理结果失败");
  } finally {
    reviewLoading.value = false;
  }
};

onMounted(() => {
  fetchList();
});
</script>

<style lang="scss" scoped>
.application-records {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .table-card {
    .pager {
      margin-top: 16px;
      display: flex;
      justify-content: flex-end;
    }
  }

  .resume-wrap {
    margin-top: 16px;

    .resume-title {
      font-size: 15px;
      font-weight: 600;
      margin-bottom: 12px;
    }

    .resume-html {
      border: 1px solid var(--el-border-color);
      border-radius: 8px;
      padding: 16px;
      max-height: 65vh;
      overflow: auto;
      background: #fff;
    }

    .resume-error {
      color: var(--el-color-danger);
      padding: 12px 0;
    }
  }

  .review-wrap {
    margin-top: 16px;

    .review-title {
      font-size: 15px;
      font-weight: 600;
      margin-bottom: 12px;
    }
  }
}
</style>
