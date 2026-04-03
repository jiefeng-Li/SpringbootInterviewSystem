<template>
  <div class="offer-manage">
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="query" @submit.prevent>
        <el-form-item label="公司ID">
          <el-input
            v-model.number="query.companyId"
            placeholder="输入公司ID"
            clearable
            style="width: 160px"
          />
        </el-form-item>
        <el-form-item label="招聘者ID">
          <el-input
            v-model.number="query.recruiterId"
            placeholder="输入招聘者ID"
            clearable
            style="width: 160px"
          />
        </el-form-item>
        <el-form-item label="Offer状态">
          <el-select
            v-model="query.status"
            placeholder="请选择状态"
            clearable
            style="width: 160px"
          >
            <el-option
              v-for="item in offerStatusOptions"
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
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" label="#" width="60" />
        <el-table-column prop="id" label="OfferID" min-width="90" />
        <el-table-column prop="jobTitle" label="职位" min-width="160" />
        <el-table-column prop="companyName" label="公司" min-width="140" />
        <el-table-column prop="jobSeekerName" label="求职者" min-width="120" />
        <el-table-column prop="recruiterName" label="招聘者" min-width="120" />
        <el-table-column label="月薪" min-width="100">
          <template #default="{ row }">
            {{ formatSalary(row.salaryMonthly, row.salaryMonthCount) }}
          </template>
        </el-table-column>
        <el-table-column
          prop="offerDeadline"
          label="截止日期"
          min-width="120"
        />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="offerTagType(row.status)">{{
              offerStatusText(row.status)
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发送时间" min-width="120" />
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
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { getSentOfferList } from "@/api/offer";

const query = reactive({
  recruiterId: undefined,
  companyId: undefined,
  status: undefined,
  pageNum: 1,
  pageSize: 10,
});

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);

const offerStatusOptions = [
  { label: "待确认", value: 0 },
  { label: "已接受", value: 1 },
  { label: "已拒绝", value: 2 },
  { label: "已过期", value: 3 },
  { label: "已撤回", value: 4 },
  { label: "已取消", value: 5 },
];

const offerStatusMap = {
  0: "待确认",
  1: "已接受",
  2: "已拒绝",
  3: "已过期",
  4: "已撤回",
  5: "已取消",
};

const offerStatusText = (status) => offerStatusMap[status] || `状态${status}`;

const offerTagType = (status) => {
  switch (status) {
    case 0:
      return "warning";
    case 1:
      return "success";
    case 2:
      return "danger";
    case 3:
      return "info";
    case 4:
      return "info";
    case 5:
      return "info";
    default:
      return "info";
  }
};

const formatSalary = (salaryMonthly, salaryMonthCount) => {
  if (salaryMonthly == null) {
    return "-";
  }
  if (salaryMonthCount == null) {
    return `${salaryMonthly}/月`;
  }
  return `${salaryMonthly} x ${salaryMonthCount}`;
};

const fetchList = async () => {
  loading.value = true;
  try {
    const { data } = await getSentOfferList({ ...query });
    const page = data?.data || data || {};
    tableData.value = page.records || page.list || [];
    total.value = page.total || 0;
  } catch (e) {
    ElMessage.error(e?.message || "获取Offer记录失败");
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  query.pageNum = 1;
  fetchList();
};

const handleReset = () => {
  query.recruiterId = undefined;
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

onMounted(() => {
  fetchList();
});
</script>

<style lang="scss" scoped>
.offer-manage {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .pager {
    margin-top: 16px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
