<template>
  <div class="home-page-container">
    <div class="search-box">
      <el-autocomplete
        v-model="keyword"
        :fetch-suggestions="querySearchAsync"
        :trigger-on-focus="false"
        clearable
        placeholder="搜索职位、公司等"
      >
        <template #suffix>
          <el-icon
            ><Search @click="queryAndJump" style="cursor: pointer"
          /></el-icon>
        </template>
      </el-autocomplete>
    </div>
    <div class="banner-container">
      <div class="banner-menu">
        <el-scrollbar>
          <el-menu>
            <el-menu-item
              v-for="item in 20"
              :index="item.toString()"
              :key="item"
              @mouseenter="handleMouseEnter(item)"
              @mouseleave="handleMouseLeave(item)"
            >
              <el-icon><setting /></el-icon>
              <span>分类 {{ item }}</span>
            </el-menu-item>
          </el-menu>
        </el-scrollbar>
      </div>
      <div class="banner-main">
        <el-carousel trigger="click">
          <el-carousel-item v-for="item in 4" :key="item">
            <h3 class="small justify-center" text="2xl">{{ item }}</h3>
          </el-carousel-item>
        </el-carousel>
        <div class="job-position-box">{{ text }}</div>
      </div>
    </div>
    <div class="popular-job-container">
      <h2 style="text-align: center">热门职位</h2>
      <div class="job-card-box">
        <div
          v-for="item in jobList"
          :key="item.id"
          class="job-card"
          @click="goToJobDetail(item.id)"
        >
          <div class="job-card-header">
            <h3>{{ item.title || "未命名职位" }}</h3>
            <p class="salary">
              {{
                item.minSalary && item.maxSalary
                  ? `${item.minSalary}-${item.maxSalary} 元`
                  : "薪资面议"
              }}
            </p>
          </div>
          <p class="job-company">{{ item.companyName || "未知公司" }}</p>
          <div class="job-meta">
            <span>{{ item.workCity || "城市待定" }}</span>
            <span>{{ item.experience || "经验不限" }}</span>
          </div>
          <div class="job-manager">
            <el-avatar :size="24" :src="item.hiringManagerAvatar">
              {{ (item.hiringManagerName || "招").slice(0, 1) }}
            </el-avatar>
            <span>负责人：{{ item.hiringManagerName || "未设置" }}</span>
          </div>
        </div>
      </div>
      <div style="display: flex; justify-content: center">
        <el-button
          type="primary"
          round
          class="more-info-btn"
          @click="router.push('/job')"
          >更多职位</el-button
        >
      </div>
    </div>
    <div class="popular-company-container">
      <h2 style="text-align: center">热门企业</h2>
      <div class="company-card-box">
        <div
          v-for="item in companyList"
          :key="item.companyId"
          class="company-card"
          @click="goToCompanyDetail(item.companyId)"
        >
          <div class="company-head">
            <el-avatar :size="52" :src="item.logoUrl">
              {{ (item.companyName || "企").slice(0, 1) }}
            </el-avatar>
            <div class="company-title">
              <h3>{{ item.companyName || "未命名公司" }}</h3>
              <p>{{ item.city || "城市待定" }}</p>
            </div>
          </div>
          <div class="company-tags">
            <el-tag size="small" type="primary">{{
              item.industry || "行业待完善"
            }}</el-tag>
            <el-tag size="small" type="success">{{
              item.scale || "规模待完善"
            }}</el-tag>
          </div>
          <p class="company-intro">
            {{ item.introduction || "该公司暂未完善简介" }}
          </p>
        </div>
      </div>
      <div style="display: flex; justify-content: center">
        <el-button
          type="primary"
          round
          class="more-info-btn"
          @click="router.push('/company')"
          >更多企业</el-button
        >
      </div>
    </div>
    <el-backtop :right="100" :bottom="100" />
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { Search } from "@element-plus/icons-vue";
import { useRouter } from "vue-router";
import { getJobPositionList } from "@/api/job";
import { getCompanyList } from "@/api/company";

const router = useRouter();
const jobPositionZIndex = ref(-1);
const text = ref("");
const jobList = ref([]);
const companyList = ref([]);
const homeJobPageSize = 8;
const homeCompanyPageSize = 6;

const keyword = ref("");
const querySearchAsync = (queryString, cb) => {
  const results = queryString
    ? links.value.filter(createFilter(queryString))
    : links.value;

  clearTimeout(timeout);
  timeout = setTimeout(() => {
    cb(results);
  }, 3000 * Math.random());
};

const queryAndJump = () => {
  console.log(keyword.value);
  router.push(`/search?keyword=${keyword.value}`);
};

const goToJobDetail = (jobId) => {
  router.push({ name: "JobDetail", params: { id: jobId } });
};

const goToCompanyDetail = (companyId) => {
  router.push({ name: "CompanyDetail", params: { id: companyId } });
};

const handleMouseEnter = (item) => {
  jobPositionZIndex.value = 1;
  text.value = item;
};

const handleMouseLeave = (item) => {
  jobPositionZIndex.value = -1;
};

const fetchHomeJobs = async () => {
  try {
    const res = await getJobPositionList({
      pageNum: 1,
      pageSize: homeJobPageSize,
    });
    jobList.value = res?.data?.data?.list || [];
  } catch (error) {
    console.error("获取首页职位失败:", error);
    jobList.value = [];
  }
};

const fetchHomeCompanies = async () => {
  try {
    const res = await getCompanyList({
      pageNum: 1,
      pageSize: homeCompanyPageSize,
    });
    companyList.value = res?.data?.data?.list || [];
  } catch (error) {
    console.error("获取首页公司失败:", error);
    companyList.value = [];
  }
};

onMounted(() => {
  fetchHomeJobs();
  fetchHomeCompanies();
});
</script>

<style lang="scss" scoped>
.home-page-container {
  height: 100%;
  width: 100%;
  .search-box {
    width: 80%;
    height: 5em;
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 0 auto;
    .el-autocomplete {
      border-radius: 5px;
      border: 1px solid #dcdfe6;
      padding: 0 1em;
      font-size: 1em;
      &:focus {
        border-color: #409eff;
        outline: 0;
      }
    }
  }
  .banner-container {
    height: 25em;
    width: 100%;
    background-color: #f5f5f5;
    display: flex;
    .banner-menu {
      height: 100%;
      width: 30%;
      .el-menu-item {
        height: 3.5em;
        line-height: 2em;
      }
    }
    .banner-main {
      height: 100%;
      width: 70%;
      position: relative;
      .el-carousel {
        height: 100%;
      }
      .job-position-box {
        height: 100%;
        width: 100%;
        position: absolute;
        top: 0;
        left: 0;
        background-color: white;
        z-index: v-bind("jobPositionZIndex");
      }
    }
  }
  .popular-job-container {
    margin-top: 30px;
    height: 480px;
    width: 100%;
    background-color: #f5f5f5;
    // align-items: center;
    padding: 10px;
    display: flex;
    flex-direction: column;
    .job-card-box {
      display: flex;
      justify-content: center;
      align-items: center;
      flex-wrap: wrap;
      flex: auto;
      width: 100%;
      padding: 10px;
      box-sizing: border-box;
      .job-card {
        display: inline-flex;
        flex-direction: column;
        justify-content: space-between;
        width: 210px;
        height: 190px;
        background-color: white;
        margin: 10px;
        padding: 14px;
        border-radius: 10px;
        border: 1px solid #ebeef5;
        box-sizing: border-box;
        cursor: pointer;
        transition: all 0.2s ease;
        .job-card-header {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          gap: 8px;
          h3 {
            margin: 0;
            color: #303133;
            font-size: 16px;
            font-weight: 600;
            line-height: 1.35;
          }
          .salary {
            margin: 0;
            color: #f56c6c;
            font-size: 13px;
            white-space: nowrap;
          }
        }
        .job-company {
          margin: 8px 0 4px;
          color: #606266;
          font-size: 13px;
        }
        .job-meta {
          display: flex;
          gap: 8px;
          color: #909399;
          font-size: 12px;
        }
        .job-manager {
          margin-top: 10px;
          display: flex;
          align-items: center;
          gap: 8px;
          color: #606266;
          font-size: 12px;
        }
        &:hover {
          border-color: #b3d8ff;
          box-shadow: 0 8px 20px rgba(64, 158, 255, 0.15);
          transform: translateY(-2px);
        }
      }
    }
  }
  .popular-company-container {
    margin-top: 40px;
    height: 560px;
    width: 100%;
    background-color: #f5f5f5;
    // align-items: center;
    padding: 10px;
    display: flex;
    flex-direction: column;
    .company-card-box {
      display: flex;
      justify-content: center;
      align-items: center;
      flex-wrap: wrap;
      flex: auto;
      width: 100%;
      padding: 10px;
      box-sizing: border-box;
      .company-card {
        display: inline-flex;
        flex-direction: column;
        width: 260px;
        height: 230px;
        background-color: white;
        margin: 10px;
        padding: 16px;
        border-radius: 12px;
        border: 1px solid #ebeef5;
        box-sizing: border-box;
        cursor: pointer;
        transition: all 0.2s ease;
        .company-head {
          display: flex;
          align-items: center;
          gap: 12px;
          .company-title {
            min-width: 0;
            h3 {
              margin: 0;
              font-size: 16px;
              color: #303133;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
            p {
              margin: 6px 0 0;
              color: #909399;
              font-size: 13px;
            }
          }
        }
        .company-tags {
          margin-top: 12px;
          display: flex;
          gap: 8px;
          flex-wrap: wrap;
        }
        .company-intro {
          margin-top: 12px;
          color: #606266;
          font-size: 13px;
          line-height: 1.5;
          overflow: hidden;
          display: -webkit-box;
          line-clamp: 3;
          -webkit-line-clamp: 3;
          -webkit-box-orient: vertical;
        }
        &:hover {
          border-color: #c6e2ff;
          box-shadow: 0 10px 24px rgba(64, 158, 255, 0.14);
          transform: translateY(-2px);
        }
      }
    }
  }
  .more-info-btn {
    width: 240px;
    height: 3em;
    background-color: #fff;
    color: #409eff;
  }
  .more-info-btn:hover {
    background-color: #409eff;
    color: #fff;
  }
}
</style>
