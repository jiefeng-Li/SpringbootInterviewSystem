<template>
  <div class="company-detail-container">
    <el-card v-loading="loading" class="company-card">
      <div class="detail-header">
        <div class="logo">
          <img
            v-if="companyData.logoUrl"
            :src="companyData.logoUrl"
            alt="公司logo"
          />
          <div v-else class="logo-placeholder">Logo</div>
        </div>
        <div class="company-info">
          <h1>{{ companyData.companyName || "公司名称" }}</h1>
          <p>管理员: {{ companyData.adminName || "未知" }}</p>
        </div>
      </div>
      <div class="detail-content">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="info-item">
              <strong>行业:</strong> {{ companyData.industry || "未设置" }}
            </div>
            <div class="info-item">
              <strong>规模:</strong> {{ companyData.scale || "未设置" }}
            </div>
            <div class="info-item">
              <strong>城市:</strong> {{ companyData.city || "未设置" }}
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <strong>官网:</strong>
              <a
                v-if="companyData.website"
                :href="companyData.website"
                target="_blank"
                >{{ companyData.website }}</a
              >
              <span v-else>未设置</span>
            </div>
            <div class="info-item">
              <strong>状态:</strong> {{ getStatusText(companyData.status) }}
            </div>
            <div class="info-item">
              <strong>创建时间:</strong>
              {{ formatDate(companyData.createTime) }}
            </div>
          </el-col>
        </el-row>
        <div class="company-detali-menu">
          <!-- <el-menu
            :default-active="4"
            mode="horizontal"
            class="horizontal-menu"
          > -->
          <el-menu
            :default-active="activeMenu"
            mode="horizontal"
            class="horizontal-menu"
            @select="handleMenuSelect"
          >
            <el-menu-item index="1">公司简介</el-menu-item>
            <el-menu-item index="2">招聘职位</el-menu-item>
          </el-menu>
        </div>
        <div v-if="activeMenu === '1'" class="introduction-section">
          <h3>公司简介</h3>
          <p class="introduction-text">
            {{ companyData.introduction || "暂无简介" }}
          </p>
        </div>
        <!--  -->
        <!-- 添加到introduction-section后面 -->
        <div v-if="activeMenu === '2'" class="job-list-section">
          <h3>招聘职位</h3>
          <div v-if="jobList.length > 0">
            <div
              v-for="job in jobList"
              :key="job.id"
              class="job-item"
              @click="goToJobDetail(job.id)"
            >
              <div class="job-title">{{ job.title }}</div>
              <div class="job-tags">
                <el-tag size="small" type="info">{{ job.workCity }}</el-tag>
                <el-tag size="small" type="info">{{ job.jobType }}</el-tag>
                <el-tag size="small" type="info">{{ job.experience }}</el-tag>
                <el-tag size="small" type="info">{{ job.education }}</el-tag>
              </div>
              <div class="job-salary">
                <span class="salary-text"
                  >{{ job.minSalary }}-{{ job.maxSalary }}</span
                >
              </div>
              <div class="job-manager">负责人: {{ job.hiringManagerName }}</div>
            </div>
            <el-pagination
              v-model:current-page="jobPageNum"
              v-model:page-size="jobPageSize"
              :total="jobTotal"
              :page-sizes="[10, 20, 30, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="fetchJobList"
              @current-change="fetchJobList"
              class="pagination"
            />
          </div>
          <el-empty v-else description="暂无职位信息" />
        </div>

        <!--  -->
        <div
          v-if="activeMenu === '1' && companyData.businessLicenseUrl"
          class="license-section"
        >
          <h3>营业执照</h3>
          <el-image
            :src="companyData.businessLicenseUrl"
            alt="营业执照"
            class="license-image"
            :preview-src-list="[companyData.businessLicenseUrl]"
            fit="cover"
          />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import { getCompanyById } from "@/api/company";
import { ElMessage } from "element-plus";
// 添加到import部分
import { getJobPositionList } from "@/api/job";

// 添加到setup中
const activeMenu = ref("1");
const jobList = ref([]);
const jobTotal = ref(0);
const jobPageSize = ref(10);
const jobPageNum = ref(1);

// 添加到setup中
import { useRouter } from "vue-router";
const router = useRouter();

const goToJobDetail = (jobId) => {
  router.push(`/job/${jobId}`);
};

// 获取职位列表方法
const fetchJobList = async () => {
  try {
    const res = await getJobPositionList({
      companyId: route.params.id,
      pageNum: jobPageNum.value,
      pageSize: jobPageSize.value,
      status: 1,
    });
    if (res.data.code === 200) {
      jobList.value = res.data.data.list;
      jobTotal.value = res.data.data.total;
    }
  } catch (error) {
    console.error("获取职位列表失败", error);
    ElMessage.error("获取职位列表失败");
  }
};

// 菜单点击处理
const handleMenuSelect = (index) => {
  activeMenu.value = index;
  if (index === "2") {
    fetchJobList();
  }
};

const route = useRoute();
const companyData = ref({});
const loading = ref(true);

const statusMap = {
  0: "待审",
  1: "正常",
  2: "驳回",
  3: "禁用",
};

const getStatusText = (status) => {
  return statusMap[status] || "未知";
};

const formatDate = (dateString) => {
  if (!dateString) return "未设置";
  const date = new Date(dateString);
  return date.toLocaleDateString("zh-CN");
};

onMounted(async () => {
  const companyId = route.params.id;
  console.log("公司 ID:", companyId);
  try {
    const res = await getCompanyById(companyId);
    if (res.data.code === 200) {
      companyData.value = res.data.data;
      console.log("公司详情:", companyData.value);
    } else {
      ElMessage.error(res.data.message || "获取公司信息失败");
    }
  } catch (error) {
    console.error("获取公司信息失败", error);
    ElMessage.error("获取公司信息失败");
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.job-list-section {
  margin-top: 20px;

  h3 {
    margin-bottom: 20px;
    color: #333;
  }
}

.job-item {
  padding: 20px;
  margin-bottom: 15px;
  border: 1px solid #eee;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    border-color: #409eff;
  }

  .job-title {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 10px;
    color: #333;
  }

  .job-tags {
    margin-bottom: 10px;

    .el-tag {
      margin-right: 8px;
    }
  }

  .job-salary {
    margin-bottom: 8px;

    .salary-text {
      color: #f56c6c;
      font-size: 18px;
      font-weight: bold;
    }
  }

  .job-manager {
    color: #909399;
    font-size: 14px;
  }
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.company-detail-container {
  width: 100%;
  height: 100%;
  padding: 20px;
  box-sizing: border-box;
}

.company-card {
  width: 100%;
  .detail-header {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
    .logo {
      width: 120px;
      height: 120px;
      margin-right: 20px;
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        border-radius: 8px;
      }
      .logo-placeholder {
        width: 100%;
        height: 100%;
        background-color: #f0f0f0;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 8px;
        color: #999;
        font-size: 14px;
      }
    }
    .company-info {
      flex: 1;
      h1 {
        margin: 0 0 10px 0;
        font-size: 24px;
        color: #333;
      }
      .introduction {
        max-height: 100px;
        overflow-y: auto;
        margin-bottom: 10px;
        p {
          margin: 0;
          color: #666;
          line-height: 1.5;
        }
      }
      p {
        margin: 5px 0;
        color: #666;
        line-height: 1.5;
      }
    }
  }
  .detail-content {
    .company-detali-menu {
      height: 5em;
      width: 90%;
      margin: 0 auto;

      /* background-color: #0f0f0f; */
      .el-menu-item {
        background-color: #fff;
      }
    }
    .introduction-section {
      margin-bottom: 20px;
      h3 {
        margin-bottom: 10px;
        color: #333;
        font-size: 16px;
      }
      .introduction-text {
        color: #333;
        line-height: 1.6;
        margin: 0;
        white-space: pre-line;
      }
    }
    .info-item {
      margin-bottom: 15px;
      strong {
        display: inline-block;
        width: 80px;
        color: #333;
      }
    }
    .license-section {
      margin-top: 30px;
      h3 {
        margin-bottom: 10px;
        color: #333;
      }
      .license-image {
        max-width: 300px;
        max-height: 200px;
        cursor: pointer;
      }
    }
  }
}
</style>
