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
          v-for="item in 8"
          :key="item"
          class="job-card"
          @click="goToJobDetail(item)"
        >
          <h3>职位 {{ item }}</h3>
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
          v-for="item in 6"
          :key="item"
          class="company-card"
          @click="goToCompanyDetail(item)"
        >
          <h3>公司 {{ item }}</h3>
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
import { ref } from "vue";
import { Search } from "@element-plus/icons-vue";
import { useRouter } from "vue-router";

const router = useRouter();
const jobPositionZIndex = ref(-1);
const text = ref("");

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
        display: inline-block;
        width: 210px;
        height: 190px;
        background-color: white;
        margin: 10px;
        box-sizing: border-box;
        cursor: pointer;
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
        display: inline-block;
        width: 260px;
        height: 230px;
        background-color: white;
        margin: 10px;
        box-sizing: border-box;
        cursor: pointer;
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
