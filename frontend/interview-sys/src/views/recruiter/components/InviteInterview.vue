<template>
  <div class="invite-interview">
    <el-card shadow="never" class="tip-card">
      <el-alert
        title="当前仅展示状态为“初筛通过”的投递记录，发送面试通知后将自动更新为“面试中”。"
        type="info"
        :closable="false"
      />
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
        <el-table-column prop="jobTitle" label="职位" min-width="180" />
        <el-table-column prop="applyTime" label="投递时间" min-width="170" />
        <el-table-column
          prop="remarks"
          label="备注"
          min-width="220"
          show-overflow-tooltip
        />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click.stop="openDetail(row)">
              查看并邀约
            </el-button>
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
      title="投递详情与面试邀约"
      width="760px"
      destroy-on-close
    >
      <template v-if="currentRow">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="投递ID">{{
            currentRow.id || "-"
          }}</el-descriptions-item>
          <el-descriptions-item label="求职者">{{
            currentRow.jobSeekerName || "-"
          }}</el-descriptions-item>
          <el-descriptions-item label="职位">{{
            currentRow.jobTitle || "-"
          }}</el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag type="success">初筛通过</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="投递时间">{{
            currentRow.applyTime || "-"
          }}</el-descriptions-item>
          <el-descriptions-item label="投递备注">{{
            currentRow.remarks || "-"
          }}</el-descriptions-item>
        </el-descriptions>

        <div class="invite-form-wrap">
          <div class="section-title">填写面试信息</div>
          <el-form :model="inviteForm" label-width="100px">
            <el-form-item label="面试时间" required>
              <el-date-picker
                v-model="inviteForm.interviewTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择面试日期"
                style="width: 220px"
              />
            </el-form-item>
            <el-form-item label="面试地址" required>
              <el-input
                v-model="inviteForm.interviewAddress"
                placeholder="请输入详细面试地址"
                maxlength="200"
                show-word-limit
              />
            </el-form-item>
            <el-form-item label="备注">
              <el-input
                v-model="inviteForm.comment"
                type="textarea"
                :rows="4"
                maxlength="500"
                show-word-limit
                placeholder="可填写面试说明、联系人等信息"
              />
            </el-form-item>
          </el-form>
        </div>
      </template>

      <template #footer>
        <el-button @click="detailVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="submitLoading"
          @click="submitInvite"
        >
          发送面试通知
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { getJobApplicationList, reviewJobApplication } from "@/api/application";
import { addInterviewNotice } from "@/api/interview";
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();

const query = reactive({
  companyId: userStore?.companyId || undefined,
  status: 2,
  pageNum: 1,
  pageSize: 10,
});

const tableLoading = ref(false);
const tableData = ref([]);
const total = ref(0);

const detailVisible = ref(false);
const currentRow = ref(null);
const submitLoading = ref(false);

const inviteForm = reactive({
  interviewTime: "",
  interviewAddress: "",
  comment: "",
});

const fetchList = async () => {
  if (!query.companyId) {
    ElMessage.warning("未获取到公司ID，请重新登录后重试");
    return;
  }

  tableLoading.value = true;
  try {
    const { data } = await getJobApplicationList({
      companyId: query.companyId,
      status: 2,
      pageNum: query.pageNum,
      pageSize: query.pageSize,
    });

    const page = data?.data || data || {};
    tableData.value = page.records || page.list || [];
    total.value = page.total || 0;
  } catch (e) {
    ElMessage.error(e?.message || "获取初筛通过投递记录失败");
  } finally {
    tableLoading.value = false;
  }
};

const handleSizeChange = () => {
  query.pageNum = 1;
  fetchList();
};

const resetInviteForm = () => {
  inviteForm.interviewTime = "";
  inviteForm.interviewAddress = "";
  inviteForm.comment = "";
};

const openDetail = (row) => {
  currentRow.value = row;
  resetInviteForm();
  detailVisible.value = true;
};

const submitInvite = async () => {
  if (!currentRow.value?.id) {
    ElMessage.error("投递记录ID不存在");
    return;
  }
  if (!currentRow.value?.userId) {
    ElMessage.error("投递记录缺少面试者ID，无法发送通知");
    return;
  }
  if (!inviteForm.interviewTime) {
    ElMessage.warning("请选择面试时间");
    return;
  }
  if (!inviteForm.interviewAddress?.trim()) {
    ElMessage.warning("请输入面试地址");
    return;
  }

  submitLoading.value = true;
  try {
    await addInterviewNotice({
      jobApplicationId: currentRow.value.id,
      intervieweeId: currentRow.value.userId,
      companyId: query.companyId,
      interviewTime: inviteForm.interviewTime,
      interviewAddress: inviteForm.interviewAddress.trim(),
      comment: inviteForm.comment?.trim() || "",
    });

    try {
      await reviewJobApplication({
        id: currentRow.value.id,
        status: 4,
        remarks: currentRow.value?.remarks || "",
      });
    } catch (e) {
      ElMessage.warning(
        e?.message ||
          "面试通知已发送，但更新投递状态为“面试中”失败，请到投递记录中手动处理",
      );
      return;
    }

    ElMessage.success("面试通知发送成功，投递状态已更新为面试中");
    detailVisible.value = false;
    fetchList();
  } catch (e) {
    ElMessage.error(e?.message || "发送面试通知失败");
  } finally {
    submitLoading.value = false;
  }
};

onMounted(() => {
  fetchList();
});
</script>

<style lang="scss" scoped>
.invite-interview {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .tip-card {
    :deep(.el-card__body) {
      padding: 12px;
    }
  }

  .table-card {
    .pager {
      margin-top: 16px;
      display: flex;
      justify-content: flex-end;
    }
  }

  .invite-form-wrap {
    margin-top: 16px;

    .section-title {
      font-size: 15px;
      font-weight: 600;
      margin-bottom: 12px;
    }
  }
}
</style>
