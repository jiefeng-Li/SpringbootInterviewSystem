<template>
  <div class="job-page-container">
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
    <div class="job-conditions-form-box">
      <el-collapse expand-icon-position="left" style="width: 100%">
        <el-collapse-item title="筛选" name="1">
          <el-form :model="jobConditions" label-width="auto">
            <el-form-item label="职位类型">
              <el-radio-group
                v-model="jobConditions.jobType"
                text-color="#fff"
                fill="#409eff"
              >
                <el-radio-button>不限</el-radio-button>
                <el-radio-button value="0">全职</el-radio-button>
                <el-radio-button value="1">兼职</el-radio-button>
                <el-radio-button value="2">实习</el-radio-button>
                <el-radio-button value="3">远程</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="薪资待遇">
              <el-row style="margin-bottom: 18px">
                <el-radio-group
                  v-model="jobConditions.salary"
                  text-color="#fff"
                  fill="#409eff"
                  @change="handleSalaryChange"
                >
                  <el-radio-button value="不限">不限</el-radio-button>
                  <el-radio-button value="0">3K以下</el-radio-button>
                  <el-radio-button value="1">3-5K</el-radio-button>
                  <el-radio-button value="2">5-10K</el-radio-button>
                  <el-radio-button value="3">10-20K</el-radio-button>
                  <el-radio-button value="4">20-50K</el-radio-button>
                  <el-radio-button value="5">50K以上</el-radio-button>
                </el-radio-group>
              </el-row>
              <el-row>
                <el-col :span="11"
                  ><el-input-number
                    style="width: 100%"
                    v-model="jobConditions.salaryMin"
                    :min="1"
                  >
                  </el-input-number
                ></el-col>
                <el-col class="text-center" :span="1" style="margin: 0 0.5rem"
                  >-</el-col
                >
                <el-col :span="11">
                  <el-input-number
                    style="width: 100%"
                    v-model="jobConditions.salaryMax"
                    :min="1"
                  >
                  </el-input-number
                ></el-col>
              </el-row>
            </el-form-item>
            <!-- <el-form-item label=""> </el-form-item> -->
            <el-form-item label="工作经验">
              <el-radio-group
                v-model="jobConditions.experience"
                text-color="#fff"
                fill="#409eff"
              >
                <el-radio-button>不限</el-radio-button>
                <el-radio-button value="在校生">在校生</el-radio-button>
                <el-radio-button value="应届生">应届生</el-radio-button>
                <el-radio-button value="经验不限">经验不限</el-radio-button>
                <el-radio-button value="一年以内">一年以内</el-radio-button>
                <el-radio-button value="1-3年">1-3年</el-radio-button>
                <el-radio-button value="3-5年">3-5年</el-radio-button>
                <el-radio-button value="5-10年">5-10年</el-radio-button>
                <el-radio-button value="10年以上">10年以上</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="学历要求">
              <el-radio-group
                v-model="jobConditions.education"
                text-color="#fff"
                fill="#409eff"
              >
                <el-radio-button>不限</el-radio-button>
                <el-radio-button value="初中以以下">初中以以下</el-radio-button>
                <el-radio-button value="中专/中技">中专/中技</el-radio-button>
                <el-radio-button value="高中">高中</el-radio-button>
                <el-radio-button value="大专">大专</el-radio-button>
                <el-radio-button value="本科">本科</el-radio-button>
                <el-radio-button value="硕士">硕士</el-radio-button>
                <el-radio-button value="博士">博士</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="公司规模">
              <el-radio-group
                v-model="jobConditions.scale"
                text-color="#fff"
                fill="#409eff"
              >
                <el-radio-button>不限</el-radio-button>
                <el-radio-button value="0-20人">0-20人</el-radio-button>
                <el-radio-button value="20-99人">20-99人</el-radio-button>
                <el-radio-button value="100-499人">100-499人</el-radio-button>
                <el-radio-button value="500-999人">500-999人</el-radio-button>
                <el-radio-button value="1000-9999人"
                  >1000-9999人</el-radio-button
                >
                <el-radio-button value="10000人以上"
                  >10000人以上</el-radio-button
                >
              </el-radio-group>
            </el-form-item>
          </el-form>
        </el-collapse-item>
      </el-collapse>
    </div>
    <div class="job-list-container">
      <el-container>
        <el-aside>
          <el-scrollbar>
            <p
              v-for="item in 40"
              :key="item"
              class="scrollbar-demo-item"
              @click="handleJobDetail(item)"
            >
              {{ item }}
            </p>
          </el-scrollbar>
        </el-aside>
        <el-main>
          <el-card class="job-detail" shadow="always"
            >job detail index : {{ jobDetail.index }}</el-card
          >
        </el-main>
      </el-container>
    </div>
    <el-backtop :right="100" :bottom="100" />
  </div>
</template>

<script setup>
import { ref } from "vue";
import { Search } from "@element-plus/icons-vue";

const jobDetail = ref({
  index: 0,
});

const jobConditions = ref({
  jobType: undefined,
  salary: undefined,
  salaryMin: undefined,
  salaryMax: undefined,
  experience: undefined,
  education: undefined,
  scale: undefined,
});

const handleSalaryChange = (value) => {
  switch (value) {
    case "不限":
      jobConditions.value.salaryMin = undefined;
      jobConditions.value.salaryMax = undefined;
      break;
    case "0": // 3K以下
      jobConditions.value.salaryMin = undefined;
      jobConditions.value.salaryMax = 3000;
      break;
    case "1": // 3-5K
      jobConditions.value.salaryMin = 3000;
      jobConditions.value.salaryMax = 5000;
      break;
    case "2": // 5-10K
      jobConditions.value.salaryMin = 5000;
      jobConditions.value.salaryMax = 10000;
      break;
    case "3": // 10-20K
      jobConditions.value.salaryMin = 10000;
      jobConditions.value.salaryMax = 20000;
      break;
    case "4": // 20-50K
      jobConditions.value.salaryMin = 20000;
      jobConditions.value.salaryMax = 50000;
      break;
    case "5": // 50K以上
      jobConditions.value.salaryMin = 50000;
      jobConditions.value.salaryMax = undefined;
      break;
    default:
      jobConditions.value.salaryMin = undefined;
      jobConditions.value.salaryMax = undefined;
  }
};

const keyword = ref("");

const queryAndJump = () => {
  console.log(keyword.value);
};

const querySearchAsync = (queryString, cb) => {
  console.log(queryString);
  console.log(jobConditions.value);
};

const handleJobDetail = (item) => {
  jobDetail.value.index = item;
};
</script>

<style lang="scss" scoped>
.job-page-container {
  height: 100%;
  width: 100%;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  .search-box {
    width: 80%;
    height: 5em;
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 0 auto;
  }
  .job-conditions-form-box {
    width: 80%;
    display: flex;
    margin: 0 auto;
    //flex-shrink: 0;
    :deep(.el-collapse-item__content) {
      padding-bottom: 0;
    }
  }
  .job-list-container {
    width: 100%;
    // background-color: #3b3b3b;
    margin-top: 18px;
    flex: 1;
    overflow: auto;
    display: flex;
    .scrollbar-demo-item {
      display: flex;
      align-items: center;
      justify-content: center;
      height: 50px;
      margin: 10px;
      text-align: center;
      border-radius: 4px;
      background: var(--el-color-primary-light-9);
      color: var(--el-color-primary);
    }
    .job-detail {
      height: 100%;
      width: 100%;
    }
  }
}
</style>
