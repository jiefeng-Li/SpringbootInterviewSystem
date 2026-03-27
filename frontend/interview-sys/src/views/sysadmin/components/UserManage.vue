<template>
  <div class="user-manage">
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="query" @submit.prevent>
        <el-form-item label="用户名">
          <el-input
            v-model="query.username"
            placeholder="输入用户名"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="角色">
          <el-select
            v-model="query.role"
            placeholder="全部"
            clearable
            style="width: 160px"
          >
            <el-option
              v-for="item in roleOptions"
              :key="item.value"
              :label="item.text"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="账号状态">
          <el-select
            v-model="query.accountStatus"
            placeholder="全部"
            clearable
            style="width: 160px"
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
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" label="#" width="60" />
        <el-table-column prop="userId" label="用户ID" width="90" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="role" label="角色" min-width="120" />
        <el-table-column prop="phone" label="手机号" min-width="140" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="companyName" label="公司" min-width="180" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.accountStatus)">{{
              row.accountStatus || "-"
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
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
      v-model="statusVisible"
      title="修改账号状态"
      width="420px"
      destroy-on-close
    >
      <template v-if="currentRow">
        <el-form :model="statusForm" label-width="90px">
          <el-form-item label="用户">
            <span>{{ currentRow.username }} (ID: {{ currentRow.userId }})</span>
          </el-form-item>
          <el-form-item label="目标状态" required>
            <el-select v-model="statusForm.accountStatus" style="width: 220px">
              <el-option
                v-for="item in statusOptions"
                :key="item.status"
                :label="item.text"
                :value="item.status"
              />
            </el-select>
          </el-form-item>
        </el-form>
      </template>
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
  getUserPage,
  getUserRoleList,
  getUserStatusList,
  updateUserStatus,
} from "@/api/user";

const query = reactive({
  username: "",
  role: undefined,
  accountStatus: undefined,
  isDeleted: 0,
  pageNum: 1,
  pageSize: 10,
});

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const roleOptions = ref([]);
const statusOptions = ref([]);

const statusVisible = ref(false);
const statusLoading = ref(false);
const currentRow = ref(null);
const statusForm = reactive({
  accountStatus: 1,
});

const statusTagType = (text) => {
  if (text === "正常") return "success";
  if (text === "禁用" || text === "注销") return "danger";
  if (text === "锁定") return "warning";
  return "info";
};

const fetchMetaOptions = async () => {
  try {
    const [roleRes, statusRes] = await Promise.all([
      getUserRoleList(),
      getUserStatusList(),
    ]);
    roleOptions.value = roleRes?.data?.data || roleRes?.data || [];
    statusOptions.value = statusRes?.data?.data || statusRes?.data || [];
  } catch {
    roleOptions.value = [];
    statusOptions.value = [];
  }
};

const fetchList = async () => {
  loading.value = true;
  try {
    const params = { ...query };
    if (
      params.accountStatus === undefined ||
      params.accountStatus === null ||
      params.accountStatus === ""
    ) {
      delete params.accountStatus;
    }
    if (!params.role) {
      delete params.role;
    }
    if (!params.username) {
      delete params.username;
    }

    const { data } = await getUserPage(params);
    const page = data?.data || data || {};
    tableData.value = page.list || page.records || [];
    total.value = page.total || 0;
  } catch (e) {
    ElMessage.error(e?.message || "获取用户列表失败");
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  query.pageNum = 1;
  fetchList();
};

const handleReset = () => {
  query.username = "";
  query.role = undefined;
  query.accountStatus = undefined;
  query.pageNum = 1;
  query.pageSize = 10;
  fetchList();
};

const handleSizeChange = () => {
  query.pageNum = 1;
  fetchList();
};

const textToStatus = (text) => {
  const target = statusOptions.value.find((item) => item.text === text);
  return target?.status ?? 1;
};

const openStatusDialog = (row) => {
  currentRow.value = row;
  statusForm.accountStatus = textToStatus(row.accountStatus);
  statusVisible.value = true;
};

const submitStatus = async () => {
  if (!currentRow.value?.userId) {
    ElMessage.error("用户ID不存在");
    return;
  }

  statusLoading.value = true;
  try {
    await updateUserStatus(currentRow.value.userId, {
      accountStatus: statusForm.accountStatus,
    });
    ElMessage.success("账号状态更新成功");
    statusVisible.value = false;
    fetchList();
  } catch (e) {
    ElMessage.error(e?.message || "状态更新失败");
  } finally {
    statusLoading.value = false;
  }
};

onMounted(async () => {
  await fetchMetaOptions();
  fetchList();
});
</script>

<style lang="scss" scoped>
.user-manage {
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
