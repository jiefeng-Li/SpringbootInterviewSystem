<template>
  <div class="user-certification-container">
    <!-- 筛选区域 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="状态">
          <el-select
            v-model="filterForm.status"
            placeholder="全部状态"
            clearable
          >
            <el-option label="待审" :value="0" />
            <el-option label="通过" :value="1" />
            <el-option label="拒绝" :value="2" />
            <el-option label="取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleFilter">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 认证记录列表 -->
    <el-card class="table-card">
      <el-table :data="bindingList" v-loading="loading" border stripe>
        <el-table-column prop="username" label="申请人" width="120" />
        <el-table-column prop="userPhone" label="联系电话" width="150" />
        <el-table-column
          prop="applicationNotes"
          label="申请理由"
          show-overflow-tooltip
        />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" v-if="!isExpired(row)">
              {{ getStatusText(row.status) }}
            </el-tag>
            <el-tag type="info" v-else>失效</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="180">
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
          v-model:current-page="filterForm.pageNum"
          v-model:page-size="filterForm.pageSize"
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
      <el-form :model="detailForm" label-width="100px" v-if="currentRecord">
        <el-form-item label="申请人">
          <span>{{ currentRecord.username }}</span>
        </el-form-item>
        <el-form-item label="联系电话">
          <span>{{ currentRecord.userPhone }}</span>
        </el-form-item>
        <el-form-item label="申请理由">
          <span>{{ currentRecord.applicationNotes }}</span>
        </el-form-item>
        <el-form-item label="申请时间">
          <span>{{ formatDate(currentRecord.createTime) }}</span>
        </el-form-item>
        <el-form-item label="失效时间">
          <span>{{ formatDate(currentRecord.expiresAt) }}</span>
        </el-form-item>

        <template v-if="currentRecord.status !== 0 || isExpired(currentRecord)">
          <el-form-item label="审核状态">
            <el-tag
              :type="getStatusType(currentRecord.status)"
              v-if="!isExpired(currentRecord)"
            >
              {{ getStatusText(currentRecord.status) }}
            </el-tag>
            <el-tag type="info" v-else>失效</el-tag>
          </el-form-item>
          <el-form-item label="审核人" v-if="currentRecord.reviewedByName">
            <span>{{ currentRecord.reviewedByName }}</span>
          </el-form-item>
          <el-form-item label="审核时间" v-if="currentRecord.reviewedTime">
            <span>{{ formatDate(currentRecord.reviewedTime) }}</span>
          </el-form-item>
          <el-form-item label="审核意见" v-if="currentRecord.reviewNotes">
            <span>{{ currentRecord.reviewNotes }}</span>
          </el-form-item>
        </template>

        <template v-else>
          <el-form-item label="审核意见">
            <el-input
              v-model="detailForm.reviewNotes"
              type="textarea"
              :rows="4"
              placeholder="请输入审核意见"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="success"
              @click="handleApprove"
              :loading="submitting"
            >
              通过
            </el-button>
            <el-button
              type="danger"
              @click="handleReject"
              :loading="submitting"
            >
              拒绝
            </el-button>
            <el-button @click="detailDialogVisible = false">取消</el-button>
          </el-form-item>
        </template>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/stores/user";
import { getBindingList, approveBinding, rejectBinding } from "@/api/binding";

const userStore = useUserStore();

// 筛选表单
const filterForm = reactive({
  pageNum: 1,
  pageSize: 10,
  companyId: userStore.companyId,
  status: null,
  start: null,
  end: null,
});

// 日期范围
const dateRange = ref([]);

// 认证记录列表
const bindingList = ref([]);
const loading = ref(false);
const total = ref(0);

// 详情弹窗相关
const detailDialogVisible = ref(false);
const currentRecord = ref(null);
const detailForm = reactive({
  reviewNotes: "",
});
const submitting = ref(false);

// 状态文本映射
const statusTextMap = {
  0: "待审",
  1: "通过",
  2: "拒绝",
  3: "取消",
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

// 判断记录是否失效
const isExpired = (record) => {
  if (!record.expiresAt || record.status !== 0) return false;
  return new Date(record.expiresAt) < new Date();
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return "未设置";
  const date = new Date(dateString);
  return date.toLocaleString("zh-CN");
};

// 获取认证记录列表
const fetchBindingList = async () => {
  if (!userStore.companyId) {
    ElMessage.error("未绑定公司");
    return;
  }

  loading.value = true;
  try {
    const params = {
      ...filterForm,
      companyId: userStore.companyId,
    };

    // 处理日期范围
    if (dateRange.value && dateRange.value.length === 2) {
      params.start = dateRange.value[0];
      params.end = dateRange.value[1];
    } else {
      params.start = null;
      params.end = null;
    }

    const res = await getBindingList(params);
    if (res.data.code === 200) {
      bindingList.value = res.data.data.list || [];
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

// 处理筛选
const handleFilter = () => {
  filterForm.pageNum = 1;
  fetchBindingList();
};

// 重置筛选
const resetFilter = () => {
  filterForm.status = null;
  dateRange.value = [];
  filterForm.pageNum = 1;
  fetchBindingList();
};

// 处理页码变化
const handleCurrentChange = (val) => {
  filterForm.pageNum = val;
  fetchBindingList();
};

// 处理每页数量变化
const handleSizeChange = (val) => {
  filterForm.pageSize = val;
  filterForm.pageNum = 1;
  fetchBindingList();
};

// 查看详情
const handleViewDetail = (record) => {
  currentRecord.value = record;
  detailForm.reviewNotes = "";
  detailDialogVisible.value = true;
};

// 通过审核
const handleApprove = async () => {
  if (!detailForm.reviewNotes.trim()) {
    ElMessage.warning("请输入审核意见");
    return;
  }

  submitting.value = true;
  try {
    const res = await approveBinding(
      currentRecord.value.id,
      detailForm.reviewNotes,
    );
    if (res.data.code === 200) {
      ElMessage.success("审核通过");
      detailDialogVisible.value = false;
      fetchBindingList();
    } else {
      ElMessage.error(res.data.message || "审核失败");
    }
  } catch (error) {
    console.error("审核失败", error);
    ElMessage.error("审核失败");
  } finally {
    submitting.value = false;
  }
};

// 拒绝审核
const handleReject = async () => {
  if (!detailForm.reviewNotes.trim()) {
    ElMessage.warning("请输入审核意见");
    return;
  }

  submitting.value = true;
  try {
    const res = await rejectBinding(currentRecord.value.id, {
      reviewNotes: detailForm.reviewNotes,
    });
    if (res.data.code === 200) {
      ElMessage.success("已拒绝");
      detailDialogVisible.value = false;
      fetchBindingList();
    } else {
      ElMessage.error(res.data.message || "操作失败");
    }
  } catch (error) {
    console.error("操作失败", error);
    ElMessage.error("操作失败");
  } finally {
    submitting.value = false;
  }
};

// 组件挂载时获取数据
onMounted(() => {
  fetchBindingList();
});
</script>
<style lang="scss" scoped>
.user-certification-container {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.filter-card {
  .filter-form {
    margin-bottom: 0;
  }
}

.table-card {
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
