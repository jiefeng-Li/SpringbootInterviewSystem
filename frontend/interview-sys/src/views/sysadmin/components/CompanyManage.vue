<template>
  <div class="company-manage">
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="query" @submit.prevent>
        <el-form-item label="公司名称">
          <el-input
            v-model="query.companyName"
            placeholder="输入公司名称"
            clearable
            style="width: 220px"
          />
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
        <el-table-column prop="companyName" label="公司名称" min-width="180" />
        <el-table-column prop="adminName" label="管理员" min-width="120" />
        <el-table-column prop="industry" label="行业" min-width="120" />
        <el-table-column prop="city" label="城市" min-width="100" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{
              statusText(row.status)
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="140" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDetail(row)"
              >查看</el-button
            >
            <el-button type="warning" link @click="openStatusDialog(row)"
              >改状态</el-button
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
      title="公司信息"
      width="760px"
      destroy-on-close
    >
      <template v-if="currentRow">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="公司ID">{{
            currentRow.companyId || "-"
          }}</el-descriptions-item>
          <el-descriptions-item label="公司名称">{{
            currentRow.companyName || "-"
          }}</el-descriptions-item>
          <el-descriptions-item label="管理员">{{
            currentRow.adminName || "-"
          }}</el-descriptions-item>
          <el-descriptions-item label="官网">{{
            currentRow.website || "-"
          }}</el-descriptions-item>
          <el-descriptions-item label="行业">{{
            currentRow.industry || "-"
          }}</el-descriptions-item>
          <el-descriptions-item label="规模">{{
            currentRow.scale || "-"
          }}</el-descriptions-item>
          <el-descriptions-item label="城市">{{
            currentRow.city || "-"
          }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusTagType(currentRow.status)">{{
              statusText(currentRow.status)
            }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="简介" :span="2">{{
            currentRow.introduction || "-"
          }}</el-descriptions-item>
          <el-descriptions-item label="Logo" :span="2">
            <el-image
              v-if="currentRow.logoUrl"
              :src="currentRow.logoUrl"
              style="width: 120px; height: 120px"
              fit="cover"
            />
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="营业执照" :span="2">
            <el-image
              v-if="currentRow.businessLicenseUrl"
              :src="currentRow.businessLicenseUrl"
              style="width: 220px; height: 140px"
              fit="cover"
            />
            <span v-else>-</span>
          </el-descriptions-item>
        </el-descriptions>
      </template>
    </el-dialog>

    <el-dialog
      v-model="statusVisible"
      title="修改公司状态"
      width="420px"
      destroy-on-close
    >
      <el-form :model="statusForm" label-width="90px">
        <el-form-item label="目标状态" required>
          <el-select v-model="statusForm.status" style="width: 220px">
            <el-option
              v-for="item in statusOptions.filter(
                (item) => item.status !== 3 && item.status !== 0,
              )"
              :key="item.status"
              :label="item.text"
              :value="item.status"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusVisible = false">取消</el-button>
        <el-button type="primary" :loading="statusLoading" @click="submitStatus"
          >确认</el-button
        >
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import {
  adminGetCompanyList,
  getCompanyStatusList,
  updateCompanyStatus,
} from "@/api/company";

const query = reactive({
  companyName: "",
  pageNum: 1,
  pageSize: 10,
});

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const statusOptions = ref([]);

const detailVisible = ref(false);
const statusVisible = ref(false);
const statusLoading = ref(false);
const currentRow = ref(null);
const statusForm = reactive({
  status: 1,
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
    const { data } = await getCompanyStatusList();
    statusOptions.value = data?.data || data || [];
  } catch {
    statusOptions.value = [];
  }
};

const fetchList = async () => {
  loading.value = true;
  try {
    const { data } = await adminGetCompanyList({ ...query });
    const page = data?.data || data || {};
    tableData.value = page.list || page.records || [];
    total.value = page.total || 0;
  } catch (e) {
    ElMessage.error(e?.message || "获取公司列表失败");
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  query.pageNum = 1;
  fetchList();
};

const handleReset = () => {
  query.companyName = "";
  query.pageNum = 1;
  query.pageSize = 10;
  fetchList();
};

const handleSizeChange = () => {
  query.pageNum = 1;
  fetchList();
};

const openDetail = (row) => {
  currentRow.value = row;
  detailVisible.value = true;
};

const openStatusDialog = (row) => {
  currentRow.value = row;
  statusForm.status = row.status;
  statusVisible.value = true;
};

const submitStatus = async () => {
  if (!currentRow.value?.companyId) {
    ElMessage.error("公司ID不存在");
    return;
  }

  statusLoading.value = true;
  try {
    await updateCompanyStatus(currentRow.value.companyId, {
      status: statusForm.status,
    });
    ElMessage.success("状态更新成功");
    statusVisible.value = false;
    fetchList();
  } catch (e) {
    ElMessage.error(e?.message || "状态更新失败");
  } finally {
    statusLoading.value = false;
  }
};

onMounted(async () => {
  await fetchStatusOptions();
  fetchList();
});
</script>

<style lang="scss" scoped>
.company-manage {
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
