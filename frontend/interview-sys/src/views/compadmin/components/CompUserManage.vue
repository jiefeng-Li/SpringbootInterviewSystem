<template>
  <div class="comp-user-manage">
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
            <el-option label="企业管理员" value="COMP_ADMIN" />
            <el-option label="招聘者" value="RECRUITER" />
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
      <el-alert
        type="info"
        :closable="false"
        title="仅展示当前企业已绑定用户，可对列表中的用户执行解绑。"
        class="tip"
      />

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" label="#" width="60" />
        <el-table-column prop="userId" label="用户ID" min-width="90" />
        <el-table-column prop="username" label="用户名" min-width="130" />
        <el-table-column label="角色" min-width="120">
          <template #default="{ row }">
            {{ roleText(row.role) }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" min-width="140" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column label="账号状态" min-width="120">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.accountStatus)">
              {{ row.accountStatus || "-" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" min-width="160" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button
              type="danger"
              link
              :disabled="row.userId === userStore.userId"
              @click="handleUnbind(row)"
            >
              解绑
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
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getUserPage, getUserStatusList } from "@/api/user";
import { unbindCompany } from "@/api/binding";
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();

const query = reactive({
  username: "",
  role: undefined,
  accountStatus: undefined,
  isDeleted: 0,
  companyId: userStore.companyId || undefined,
  pageNum: 1,
  pageSize: 10,
});

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const statusOptions = ref([]);

const roleText = (role) => {
  if (role === "COMP_ADMIN") return "企业管理员";
  if (role === "RECRUITER") return "招聘者";
  if (role === "JOB_SEEKER") return "求职者";
  if (role === "SYS_ADMIN") return "系统管理员";
  return role || "-";
};

const statusTagType = (text) => {
  if (text === "正常") return "success";
  if (text === "禁用" || text === "注销") return "danger";
  if (text === "锁定") return "warning";
  return "info";
};

const fetchMetaOptions = async () => {
  try {
    const res = await getUserStatusList();
    statusOptions.value = res?.data?.data || res?.data || [];
  } catch {
    statusOptions.value = [];
  }
};

const fetchList = async () => {
  if (!userStore.companyId) {
    ElMessage.warning("当前账号未绑定企业");
    tableData.value = [];
    total.value = 0;
    return;
  }

  loading.value = true;
  try {
    query.companyId = userStore.companyId;
    const params = { ...query };
    if (!params.username) delete params.username;
    if (!params.role) delete params.role;
    if (
      params.accountStatus === undefined ||
      params.accountStatus === null ||
      params.accountStatus === ""
    ) {
      delete params.accountStatus;
    }

    const { data } = await getUserPage(params);
    const page = data?.data || data || {};
    tableData.value = page.list || page.records || [];
    total.value = page.total || 0;
  } catch (e) {
    ElMessage.error(e?.message || "获取企业用户列表失败");
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

const handleUnbind = async (row) => {
  if (!row?.userId) {
    ElMessage.error("用户ID不存在");
    return;
  }

  try {
    await ElMessageBox.confirm(
      `确认解绑用户【${row.username || row.userId}】吗？`,
      "解绑确认",
      {
        confirmButtonText: "确认解绑",
        cancelButtonText: "取消",
        type: "warning",
      },
    );

    await unbindCompany(row.userId);
    ElMessage.success("解绑成功");
    fetchList();
  } catch (e) {
    if (e === "cancel" || e === "close") {
      return;
    }
    ElMessage.error(e?.message || "解绑失败");
  }
};

onMounted(async () => {
  await fetchMetaOptions();
  fetchList();
});
</script>

<style lang="scss" scoped>
.comp-user-manage {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .tip {
    margin-bottom: 12px;
  }

  .pager {
    margin-top: 16px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
