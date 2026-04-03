<template>
  <div class="job-manage">
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="query" @submit.prevent>
        <el-form-item label="关键词">
          <el-input
            v-model="query.keyword"
            placeholder="输入职位关键词"
            clearable
            style="width: 220px"
          />
        </el-form-item>
        <el-form-item label="职位状态">
          <el-select
            v-model="query.status"
            placeholder="请选择状态"
            clearable
            style="width: 160px"
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
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" label="#" width="60" />
        <el-table-column prop="title" label="职位名称" min-width="180" />
        <el-table-column prop="companyName" label="公司名称" min-width="160" />
        <el-table-column prop="workCity" label="工作城市" min-width="120" />
        <el-table-column label="薪资" min-width="120">
          <template #default="{ row }">
            {{ formatSalary(row.minSalary, row.maxSalary) }}
          </template>
        </el-table-column>
        <el-table-column prop="experience" label="经验要求" min-width="120" />
        <el-table-column prop="education" label="学历要求" min-width="120" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{
              row.status || "-"
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" min-width="170" />
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
import { getJobPositionList } from "@/api/job";

const query = reactive({
  keyword: "",
  status: undefined,
  pageNum: 1,
  pageSize: 10,
});

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);

const statusOptions = [
  { label: "草稿", value: 0 },
  { label: "招聘中", value: 1 },
  { label: "已暂停", value: 2 },
  { label: "已招满", value: 3 },
  { label: "已关闭", value: 4 },
];

const statusTagType = (status) => {
  switch (status) {
    case "招聘中":
      return "success";
    case "草稿":
      return "info";
    case "已暂停":
      return "warning";
    case "已招满":
      return "danger";
    case "已关闭":
      return "danger";
    default:
      return "info";
  }
};

const formatSalary = (minSalary, maxSalary) => {
  if (minSalary == null && maxSalary == null) {
    return "-";
  }
  return `${minSalary || 0}-${maxSalary || 0}K`;
};

const fetchList = async () => {
  loading.value = true;
  try {
    const params = {
      ...query,
    };
    const { data } = await getJobPositionList(params);
    const page = data?.data || data || {};
    tableData.value = page.list || page.records || [];
    total.value = page.total || 0;
  } catch (e) {
    ElMessage.error(e?.message || "获取职位列表失败");
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  query.pageNum = 1;
  fetchList();
};

const handleReset = () => {
  query.keyword = "";
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
.job-manage {
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
