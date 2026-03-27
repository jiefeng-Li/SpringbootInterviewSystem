<template>
  <div class="job-detail">
    <div class="job-header">
      <h1 class="job-title">{{ jobInfo.title }}</h1>
      <el-button type="primary" @click="handleEdit">编辑</el-button>
    </div>

    <div class="salary-info">
      <span class="salary-text">{{
        formatSalary(jobInfo.minSalary, jobInfo.maxSalary)
      }}</span>
    </div>

    <div class="job-basic-info">
      <div class="info-item">
        <span class="label">公司：</span>
        <span>{{ jobInfo.companyName }}</span>
      </div>
      <div class="info-item">
        <span class="label">工作城市：</span>
        <span>{{ jobInfo.workCity }}</span>
      </div>
      <div class="info-item">
        <span class="label">工作性质：</span>
        <span>{{ jobInfo.jobType }}</span>
      </div>
      <div class="info-item">
        <span class="label">经验要求：</span>
        <span>{{ jobInfo.experience }}</span>
      </div>
      <div class="info-item">
        <span class="label">学历要求：</span>
        <span>{{ jobInfo.education }}</span>
      </div>
      <div class="info-item">
        <span class="label">招聘人数：</span>
        <span>{{ jobInfo.headcount }}人</span>
      </div>
      <div class="info-item">
        <span class="label">负责人：</span>
        <span>{{ jobInfo.hiringManagerName }}</span>
      </div>
      <div class="info-item">
        <span class="label">职位状态：</span>
        <span>{{ jobInfo.status }}</span>
      </div>
      <div class="info-item">
        <span class="label">发布时间：</span>
        <span>{{ formatDate(jobInfo.publishTime) }}</span>
      </div>
    </div>

    <div class="job-tags" v-if="jobInfo.tags && jobInfo.tags.length">
      <el-tag v-for="tag in jobInfo.tags" :key="tag" class="tag-item">{{
        tag
      }}</el-tag>
    </div>

    <div class="job-section">
      <h2 class="section-title">职位描述</h2>
      <div class="section-content">{{ jobInfo.description }}</div>
    </div>

    <div class="job-section">
      <h2 class="section-title">职位要求</h2>
      <div class="section-content">{{ jobInfo.requirement }}</div>
    </div>

    <div class="job-stats">
      <div class="stat-item">
        <span class="stat-label">浏览量：</span>
        <span class="stat-value">{{ jobInfo.viewCount }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">投递量：</span>
        <span class="stat-value">{{ jobInfo.applyCount }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { getJobPositionById } from "@/api/job";

const router = useRouter();
const jobInfo = ref({
  companyId: null,
  companyName: "",
  title: "",
  description: "",
  requirement: "",
  workCity: "",
  jobType: "",
  tags: [],
  minSalary: 0,
  maxSalary: 0,
  experience: "",
  education: "",
  headcount: 0,
  hiringManagerId: null,
  hiringManagerName: "",
  status: "",
  publishTime: null,
  viewCount: 0,
  applyCount: 0,
  createTime: null,
});

// 格式化薪资显示
const formatSalary = (min, max) => {
  if (!min && !max) return "薪资面议";
  if (!max) return `${min}k`;
  return `${min}k-${max}k`;
};

// 格式化日期显示
const formatDate = (date) => {
  if (!date) return "";
  const d = new Date(date);
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
};

// 获取职位详情
const getJobById = async (id) => {
  try {
    // 这里替换为实际的API调用
    const response = await getJobPositionById(id);
    jobInfo.value = response.data.data;
    console.log("获取职位详情，ID:", id);
  } catch (error) {
    console.error("获取职位详情失败:", error);
  }
};

// 处理编辑操作
const handleEdit = () => {
  // 跳转到编辑页面
  router.push({
    path: `/recruiter/job/edit/${jobInfo.value.id}`,
  });
};

// 组件挂载时获取职位信息
onMounted(() => {
  // 从路由参数获取职位ID
  const jobId = router.currentRoute.value.params.id;
  console.log("jobId", jobId);
  if (jobId) {
    getJobById(jobId);
  }
});
</script>

<style lang="scss" scoped>
.job-detail {
  padding: 20px;
  background: #fff;
  border-radius: 4px;
}

.job-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.job-title {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
}

.salary-info {
  margin-bottom: 20px;
}

.salary-text {
  font-size: 20px;
  color: #f56c6c;
  font-weight: bold;
}

.job-basic-info {
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.info-item {
  display: flex;
  align-items: center;
}

.label {
  color: #909399;
  margin-right: 8px;
}

.job-tags {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.tag-item {
  margin: 0;
}

.job-section {
  margin-bottom: 30px;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
}

.section-content {
  line-height: 1.6;
  color: #606266;
}

.job-stats {
  display: flex;
  gap: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.stat-item {
  display: flex;
  align-items: center;
}

.stat-label {
  color: #909399;
}

.stat-value {
  font-weight: bold;
  color: #409eff;
}
</style>
