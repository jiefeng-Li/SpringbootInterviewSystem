<template>
  <div class="job-list-container">
    <!-- 筛选条件区域 -->
    <div class="filter-section">
      <el-collapse expand-icon-position="left" style="width: 100%">
        <el-collapse-item title="筛选条件" name="1">
          <el-form :model="filterForm" label-width="auto">
            <el-form-item label="工作城市">
              <!-- <el-input
                v-model="filterForm.workCity"
                placeholder="请输入工作城市"
                clearable
              /> -->
              <el-cascader
                v-model="filterForm.workCity"
                :options="cityOptions"
                :props="cityProps"
                placeholder="请选择工作城市"
              />
            </el-form-item>

            <el-form-item label="职位类型">
              <el-radio-group v-model="filterForm.jobType">
                <el-radio-button :label="undefined">不限</el-radio-button>
                <el-radio-button :label="0">全职</el-radio-button>
                <el-radio-button :label="1">兼职</el-radio-button>
                <el-radio-button :label="2">实习</el-radio-button>
                <el-radio-button :label="3">远程</el-radio-button>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="薪资范围">
              <el-row>
                <el-col :span="11">
                  <el-input-number
                    v-model="filterForm.minSalary"
                    :min="0"
                    placeholder="最低薪资"
                    style="width: 100%"
                  />
                </el-col>
                <el-col class="text-center" :span="2">-</el-col>
                <el-col :span="11">
                  <el-input-number
                    v-model="filterForm.maxSalary"
                    :min="0"
                    placeholder="最高薪资"
                    style="width: 100%"
                  />
                </el-col>
              </el-row>
            </el-form-item>

            <el-form-item label="工作经验">
              <el-radio-group v-model="filterForm.experience">
                <el-radio-button :label="undefined">不限</el-radio-button>
                <el-radio-button label="在校生">在校生</el-radio-button>
                <el-radio-button label="应届生">应届生</el-radio-button>
                <el-radio-button label="经验不限">经验不限</el-radio-button>
                <el-radio-button label="一年以内">一年以内</el-radio-button>
                <el-radio-button label="1-3年">1-3年</el-radio-button>
                <el-radio-button label="3-5年">3-5年</el-radio-button>
                <el-radio-button label="5-10年">5-10年</el-radio-button>
                <el-radio-button label="10年以上">10年以上</el-radio-button>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="学历要求">
              <el-radio-group v-model="filterForm.education">
                <el-radio-button :label="undefined">不限</el-radio-button>
                <el-radio-button label="初中及以下">初中及以下</el-radio-button>
                <el-radio-button label="中专/中技">中专/中技</el-radio-button>
                <el-radio-button label="高中">高中</el-radio-button>
                <el-radio-button label="大专">大专</el-radio-button>
                <el-radio-button label="本科">本科</el-radio-button>
                <el-radio-button label="硕士">硕士</el-radio-button>
                <el-radio-button label="博士">博士</el-radio-button>
              </el-radio-group>
            </el-form-item>

            <!-- <el-form-item label="公司规模">
              <el-radio-group v-model="filterForm.scale">
                <el-radio-button :label="undefined">不限</el-radio-button>
                <el-radio-button label="0-20人">0-20人</el-radio-button>
                <el-radio-button label="20-99人">20-99人</el-radio-button>
                <el-radio-button label="100-499人">100-499人</el-radio-button>
                <el-radio-button label="500-999人">500-999人</el-radio-button>
                <el-radio-button label="1000-9999人"
                  >1000-9999人</el-radio-button
                >
                <el-radio-button label="10000人以上"
                  >10000人以上</el-radio-button
                >
              </el-radio-group>
            </el-form-item> -->

            <el-form-item label="职位状态">
              <el-select
                v-model="filterForm.status"
                placeholder="请选择职位状态"
                clearable
                style="width: 100%"
              >
                <el-option label="全部" :value="undefined" />
                <el-option label="招聘中" :value="1" />
                <el-option label="已暂停" :value="0" />
              </el-select>
            </el-form-item>

            <el-form-item label="排序方式">
              <el-checkbox-group v-model="sortOptions">
                <el-checkbox label="descByCreateTime"
                  >按创建时间倒序</el-checkbox
                >
                <el-checkbox label="descByPublishTime"
                  >按发布时间倒序</el-checkbox
                >
                <el-checkbox label="descByHeadCount">按浏览量倒序</el-checkbox>
              </el-checkbox-group>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="resetFilter">重置</el-button>
            </el-form-item>
          </el-form>
        </el-collapse-item>
      </el-collapse>
    </div>

    <!-- 职位列表区域 -->
    <div class="job-list-section">
      <div v-loading="loading" class="job-list">
        <div
          v-for="job in jobList"
          :key="job.id"
          class="job-item"
          @click="handleJobDetail(job)"
        >
          <div class="job-title">{{ job.title }}</div>
          <div class="job-info">
            <span class="job-salary"
              >{{ job.minSalary }}-{{ job.maxSalary }}K</span
            >
            <span class="job-city">{{ job.workCity }}</span>
            <span class="job-experience">{{ job.experience }}</span>
            <span class="job-education">{{ job.education }}</span>
          </div>
          <div class="job-tags">
            <el-tag
              v-for="(tag, index) in job.tags"
              :key="index"
              size="small"
              type="success"
            >
              {{ tag }}
            </el-tag>
          </div>
          <div class="job-meta">
            <span
              class="job-status"
              :class="job.status === '招聘中' ? 'active' : 'inactive'"
            >
              {{ job.status }}
            </span>
            <span class="job-publish-time">发布于 {{ job.publishTime }}</span>
          </div>
        </div>

        <div v-if="!jobList.length && !loading" class="empty-data">
          <el-empty description="暂无职位数据" />
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 30, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from "vue";
import { useUserStore } from "@/stores/user";
import { getJobPositionList } from "@/api/job"; // 假设有一个获取职位列表的API
import { regionData } from "element-china-area-data";
import { useRouter } from "vue-router";

const userStore = useUserStore();
const router = useRouter();
const cityOptions = ref(regionData);

// 城市选项配置
const cityProps = {
  expandTrigger: "hover",
  value: "label",
  label: "label",
  children: "children",
};

// 筛选表单
const filterForm = reactive({
  companyId: userStore.companyId, // 从store中获取公司ID
  workCity: undefined,
  jobType: undefined,
  tags: [],
  minSalary: undefined,
  maxSalary: undefined,
  experience: undefined,
  education: undefined,
  hiringManagerId: undefined,
  status: undefined,
  scale: undefined,
  descByCreateTime: true,
  descByPublishTime: true,
  descByHeadCount: false,
});

// 排序选项
const sortOptions = computed({
  get() {
    const options = [];
    if (filterForm.descByCreateTime) options.push("descByCreateTime");
    if (filterForm.descByPublishTime) options.push("descByPublishTime");
    if (filterForm.descByHeadCount) options.push("descByHeadCount");
    return options;
  },
  set(value) {
    filterForm.descByCreateTime = value.includes("descByCreateTime");
    filterForm.descByPublishTime = value.includes("descByPublishTime");
    filterForm.descByHeadCount = value.includes("descByHeadCount");
  },
});

// 分页参数
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
});

// 职位列表
const jobList = ref([]);
const loading = ref(false);

// 获取职位列表
const fetchJobList = async () => {
  loading.value = true;
  try {
    // 构建查询参数
    const params = {
      ...filterForm,
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
    };

    // 调用API获取数据
    const res = await getJobPositionList(params);
    console.log(res.data.data.list);
    jobList.value = res.data.data.list || [];
    pagination.total = res.data.data.total || 0;
  } catch (error) {
    console.error("获取职位列表失败:", error);
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1;
  fetchJobList();
};

// 重置筛选条件
const resetFilter = () => {
  filterForm.workCity = "";
  filterForm.jobType = undefined;
  filterForm.tags = [];
  filterForm.minSalary = undefined;
  filterForm.maxSalary = undefined;
  filterForm.experience = undefined;
  filterForm.education = undefined;
  filterForm.status = undefined;
  filterForm.scale = undefined;
  filterForm.descByCreateTime = true;
  filterForm.descByPublishTime = true;
  filterForm.descByHeadCount = false;

  handleSearch();
};

// 分页大小改变
const handleSizeChange = (val) => {
  pagination.pageSize = val;
  fetchJobList();
};

// 当前页改变
const handleCurrentChange = (val) => {
  pagination.currentPage = val;
  fetchJobList();
};

// 查看职位详情
const handleJobDetail = (job) => {
  // 跳转到职位详情页或打开详情对话框
  router.push(`/recruiter/job/${job.id}`);
  // 这里可以根据实际需求实现跳转或弹窗逻辑
};

// 页面加载时获取数据
onMounted(() => {
  fetchJobList();
});
</script>

<style lang="scss" scoped>
.job-list-container {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
  padding: 20px;
  box-sizing: border-box;

  .filter-section {
    margin-bottom: 20px;

    :deep(.el-collapse-item__content) {
      padding-bottom: 0;
    }
  }

  .job-list-section {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;

    .job-list {
      flex: 1;
      overflow-y: auto;

      .job-item {
        padding: 16px;
        margin-bottom: 12px;
        border: 1px solid #ebeef5;
        border-radius: 4px;
        background-color: #fff;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
          border-color: #c6e2ff;
          background-color: #ecf5ff;
        }

        .job-title {
          font-size: 18px;
          font-weight: bold;
          margin-bottom: 8px;
          color: #303133;
        }

        .job-info {
          margin-bottom: 8px;
          color: #606266;

          span {
            margin-right: 16px;
          }

          .job-salary {
            color: #ff6600;
            font-weight: bold;
          }
        }

        .job-tags {
          margin-bottom: 8px;

          .el-tag {
            margin-right: 8px;
          }
        }

        .job-meta {
          display: flex;
          justify-content: space-between;
          color: #909399;
          font-size: 14px;

          .job-status {
            &.active {
              color: #67c23a;
            }

            &.inactive {
              color: #f56c6c;
            }
          }
        }
      }

      .empty-data {
        padding: 40px 0;
      }
    }

    .pagination-container {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style>
