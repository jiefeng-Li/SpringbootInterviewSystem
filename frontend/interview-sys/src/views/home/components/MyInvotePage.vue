<template>
  <div class="invote-page-container">
    <el-container style="height: calc(100vh - 60px)">
      <el-header>
        <el-menu
          :default-active="activeStatus"
          mode="horizontal"
          class="horizontal-menu"
          @select="handleStatusChange"
        >
          <el-menu-item index="-1">全部</el-menu-item>
          <el-menu-item index="0">待处理</el-menu-item>
          <el-menu-item index="1">已查看</el-menu-item>
          <el-menu-item index="2">初筛通过</el-menu-item>
          <el-menu-item index="3">初筛不通过</el-menu-item>
          <el-menu-item index="4">面试中</el-menu-item>
          <el-menu-item index="5">已发Offer</el-menu-item>
          <el-menu-item index="6">已录用</el-menu-item>
          <el-menu-item index="7">已淘汰</el-menu-item>
        </el-menu>
      </el-header>
      <el-main>
        <el-scrollbar @end-reached="handleEndReached">
          <el-card
            class="box-card"
            v-for="item in applications"
            :key="item.id"
            shadow="hover"
          >
            <template #header>
              <div class="card-header">
                <div class="title-wrap">
                  <span class="job-title">{{
                    item.jobTitle || "未命名职位"
                  }}</span>
                  <el-tag size="small" :type="statusTagType(item.status)">
                    {{ statusText(item.status) }}
                  </el-tag>
                </div>
                <div class="apply-time">
                  投递时间：{{ formatDate(item.applyTime) }}
                </div>
              </div>
            </template>

            <div class="item-row">
              <span class="label">简历ID：</span>
              <span>{{ item.resumeId || "-" }}</span>
            </div>

            <div class="item-row" v-if="item.coverLetter">
              <span class="label">求职信：</span>
              <span class="content">{{ item.coverLetter }}</span>
            </div>

            <div class="item-row" v-if="item.remarks">
              <span class="label">HR备注：</span>
              <span class="content">{{ item.remarks }}</span>
            </div>

            <div class="item-row" v-if="item.resume">
              <span class="label">简历信息：</span>
              <span>
                {{
                  item.resume.name ||
                  item.resume.title ||
                  item.resume.city ||
                  "已关联简历"
                }}
              </span>
            </div>

            <div class="action-row" v-if="item.status === 5">
              <el-button
                size="small"
                type="primary"
                plain
                @click="openOfferDialog(item)"
              >
                查看收到的Offer
              </el-button>
            </div>
          </el-card>

          <div class="load-state" v-if="loading">加载中...</div>
          <div class="load-state" v-else-if="noMore && applications.length > 0">
            没有更多了
          </div>
          <el-empty
            v-else-if="!loading && applications.length === 0"
            description="暂无投递记录"
            :image-size="80"
          />

          <div v-else class="load-more-wrap">
            <el-button link type="primary" @click="loadApplications"
              >加载更多</el-button
            >
          </div>
        </el-scrollbar>
      </el-main>
    </el-container>

    <el-dialog
      v-model="offerDialogVisible"
      width="760px"
      title="收到的 Offer"
      destroy-on-close
    >
      <el-skeleton :loading="offerLoading" animated :rows="6">
        <template v-if="offerList.length > 0">
          <el-card
            v-for="offer in offerList"
            :key="offer.id"
            shadow="never"
            class="offer-card"
          >
            <template #header>
              <div class="offer-header">
                <div>
                  <span class="offer-title">{{
                    offer.jobTitle || "Offer"
                  }}</span>
                  <el-tag size="small" :type="offerTagType(offer.status)">
                    {{ offerStatusText(offer.status) }}
                  </el-tag>
                </div>
                <span class="offer-company">{{
                  offer.companyName || "-"
                }}</span>
              </div>
            </template>

            <el-descriptions :column="2" size="small" border>
              <el-descriptions-item label="月薪">
                {{ offer.salaryMonthly ? `${offer.salaryMonthly} 元` : "-" }}
              </el-descriptions-item>
              <el-descriptions-item label="年薪月数">
                {{ offer.salaryMonthCount || "-" }}
              </el-descriptions-item>
              <el-descriptions-item label="试用期(月)">
                {{ offer.probationMonths ?? "-" }}
              </el-descriptions-item>
              <el-descriptions-item label="预计入职">
                {{ formatDate(offer.expectedEntryDate) }}
              </el-descriptions-item>
              <el-descriptions-item label="确认截止">
                {{ formatDate(offer.offerDeadline) }}
              </el-descriptions-item>
              <el-descriptions-item label="处理时间">
                {{ formatDate(offer.respondTime) }}
              </el-descriptions-item>
              <el-descriptions-item label="福利说明" :span="2">
                {{ offer.welfare || "-" }}
              </el-descriptions-item>
              <el-descriptions-item label="备注" :span="2">
                {{ offer.remark || "-" }}
              </el-descriptions-item>
              <el-descriptions-item
                v-if="offer.rejectReason"
                label="拒绝原因"
                :span="2"
              >
                {{ offer.rejectReason }}
              </el-descriptions-item>
            </el-descriptions>

            <div class="offer-actions" v-if="offer.status === 0">
              <el-button
                type="success"
                :loading="
                  offerActionLoadingId === offer.id && offerActionStatus === 1
                "
                @click="handleOfferAction(offer, 1)"
              >
                接受 Offer
              </el-button>
              <el-button
                type="danger"
                plain
                :loading="
                  offerActionLoadingId === offer.id && offerActionStatus === 2
                "
                @click="handleOfferAction(offer, 2)"
              >
                拒绝 Offer
              </el-button>
            </div>
          </el-card>
        </template>

        <el-empty
          v-else
          description="当前投递暂无 Offer 记录"
          :image-size="90"
        />
      </el-skeleton>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, onBeforeUnmount, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getOwnJobApplicationList } from "@/api/application";
import { getOfferListByUser, updateOfferStatus } from "@/api/offer";
import { useUserStore } from "@/stores";

const userStore = useUserStore();
const activeStatus = ref("-1");
const applications = ref([]);
const loading = ref(false);
const noMore = ref(false);
const pageNum = ref(1);
const pageSize = ref(10);

const offerDialogVisible = ref(false);
const offerLoading = ref(false);
const currentApplication = ref(null);
const offerList = ref([]);
const offerActionLoadingId = ref(null);
const offerActionStatus = ref(null);

const statusMap = {
  0: "待处理",
  1: "已查看",
  2: "初筛通过",
  3: "初筛不通过",
  4: "面试中",
  5: "已发Offer",
  6: "已录用",
  7: "已淘汰",
};

const statusText = (status) => statusMap[status] || "未知状态";

const statusTagType = (status) => {
  switch (status) {
    case 0:
      return "info";
    case 1:
      return "";
    case 2:
      return "success";
    case 3:
      return "danger";
    case 4:
      return "warning";
    case 5:
      return "success";
    case 6:
      return "success";
    case 7:
      return "danger";
    default:
      return "info";
  }
};

const offerStatusMap = {
  0: "待确认",
  1: "已接受",
  2: "已拒绝",
  3: "已过期",
  4: "已撤回",
  5: "已取消",
};

const offerStatusText = (status) => offerStatusMap[status] || "未知状态";

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

const formatDate = (dateValue) => {
  if (!dateValue) return "-";
  const date = new Date(dateValue);
  if (Number.isNaN(date.getTime())) return String(dateValue);
  return date.toLocaleDateString("zh-CN");
};

const extractPageData = (res) => {
  const payload = res?.data?.data ?? res?.data ?? {};
  return {
    list: payload?.list || [],
    pages: Number(payload?.pages || 0),
    pageNum: Number(payload?.pageNum || 1),
  };
};

const loadOfferListByApplication = async (applicationId) => {
  offerLoading.value = true;
  try {
    const res = await getOfferListByUser({
      userId: userStore.userId,
      pageNum: 1,
      pageSize: 100,
    });
    const pageData = extractPageData(res);
    offerList.value = (pageData.list || []).filter(
      (item) => item.jobApplicationId === applicationId,
    );
  } catch (error) {
    offerList.value = [];
    ElMessage.error("加载 Offer 记录失败");
  } finally {
    offerLoading.value = false;
  }
};

const loadApplications = async () => {
  if (loading.value || noMore.value) return;
  if (!userStore.userId) {
    ElMessage.warning("未获取到用户信息，请重新登录");
    noMore.value = true;
    return;
  }

  loading.value = true;
  try {
    const params = {
      userId: userStore.userId,
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    };

    if (activeStatus.value !== "-1") {
      params.status = Number(activeStatus.value);
    }

    const res = await getOwnJobApplicationList(params);
    const pageData = extractPageData(res);
    const newItems = pageData.list || [];
    applications.value.push(...newItems);

    if (pageData.pageNum >= pageData.pages || newItems.length === 0) {
      noMore.value = true;
    } else {
      pageNum.value += 1;
    }
  } catch (error) {
    noMore.value = true;
    ElMessage.error("加载投递记录失败");
  } finally {
    loading.value = false;
  }
};

const resetAndLoad = async () => {
  applications.value = [];
  pageNum.value = 1;
  noMore.value = false;
  await loadApplications();
};

const handleStatusChange = async (index) => {
  activeStatus.value = index;
  await resetAndLoad();
};

const handleEndReached = (direction) => {
  if (direction !== "bottom") return;
  loadApplications();
};

const openOfferDialog = async (application) => {
  if (!userStore.userId) {
    ElMessage.warning("未获取到用户信息，请重新登录");
    return;
  }
  currentApplication.value = application;
  offerDialogVisible.value = true;
  await loadOfferListByApplication(application.id);
};

const handleOfferAction = async (offer, status) => {
  let rejectReason = "";
  if (status === 2) {
    try {
      const { value } = await ElMessageBox.prompt(
        "可填写拒绝原因（选填）",
        "拒绝 Offer",
        {
          confirmButtonText: "确认拒绝",
          cancelButtonText: "取消",
          inputPlaceholder: "请输入拒绝原因",
          inputType: "textarea",
        },
      );
      rejectReason = value?.trim() || "";
    } catch {
      return;
    }
  } else {
    try {
      await ElMessageBox.confirm("确认接受该 Offer 吗？", "接受 Offer", {
        type: "warning",
        confirmButtonText: "确认",
        cancelButtonText: "取消",
      });
    } catch {
      return;
    }
  }

  offerActionLoadingId.value = offer.id;
  offerActionStatus.value = status;
  try {
    await updateOfferStatus({
      id: offer.id,
      status,
      rejectReason,
    });

    offer.status = status;
    offer.rejectReason = status === 2 ? rejectReason : offer.rejectReason;
    offer.respondTime = new Date().toISOString();
    ElMessage.success(status === 1 ? "已接受 Offer" : "已拒绝 Offer");

    await loadOfferListByApplication(currentApplication.value.id);
    await resetAndLoad();
  } catch (error) {
    ElMessage.error(error?.message || "操作失败");
  } finally {
    offerActionLoadingId.value = null;
    offerActionStatus.value = null;
  }
};

onMounted(() => {
  document.body.style.overflow = "hidden";
  loadApplications();
});

onBeforeUnmount(() => {
  document.body.style.overflow = "";
});
</script>

<style lang="scss" scoped>
.invote-page-container {
  width: 100%;

  .horizontal-menu {
    border-bottom: none;
  }

  .box-card {
    margin-bottom: 12px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 12px;
  }

  .title-wrap {
    display: flex;
    align-items: center;
    gap: 8px;
    min-width: 0;
  }

  .job-title {
    font-size: 15px;
    font-weight: 600;
    color: var(--el-text-color-primary);
  }

  .apply-time {
    color: var(--el-text-color-secondary);
    font-size: 13px;
    white-space: nowrap;
  }

  .item-row {
    line-height: 1.8;
    color: var(--el-text-color-regular);
  }

  .action-row {
    margin-top: 12px;
    display: flex;
    justify-content: flex-end;
  }

  .label {
    color: var(--el-text-color-secondary);
  }

  .content {
    white-space: pre-line;
  }

  .load-state {
    text-align: center;
    color: var(--el-text-color-secondary);
    padding: 10px 0 16px;
    font-size: 13px;
  }

  .load-more-wrap {
    text-align: center;
    padding: 8px 0 16px;
  }

  .offer-card {
    margin-bottom: 12px;
  }

  .offer-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 8px;
  }

  .offer-title {
    font-size: 15px;
    font-weight: 600;
    margin-right: 8px;
  }

  .offer-company {
    color: var(--el-text-color-secondary);
    font-size: 13px;
  }

  .offer-actions {
    margin-top: 12px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
