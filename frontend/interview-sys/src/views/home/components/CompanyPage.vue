<template>
  <div class="company-page-container">
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
    <div class="company-select-box">
      <el-select
        v-model="companyConditions.city"
        placeholder="公司地点"
        style="width: 240px"
      >
        <el-option
          v-for="item in citys"
          :key="item.label"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <el-select
        v-model="companyConditions.industry"
        placeholder="行业类型"
        style="width: 240px; margin-left: 24px"
      >
        <el-option
          v-for="item in industrys"
          :key="item.label"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <el-select
        v-model="companyConditions.scale"
        placeholder="公司规模"
        style="width: 240px; margin-left: 24px"
      >
        <el-option
          v-for="item in scales"
          :key="item.label"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
    </div>
    <div class="company-list-box">
      <el-container>
        <el-header>
          <div class="center-pagination">
            <el-pagination
              background
              layout="prev, pager, next"
              :total="total"
              :current-page="currentPage"
              @current-change="handlePageChange"
            />
          </div>
        </el-header>
        <el-main>
          <div class="comp-card-container">
            <div class="company-card-box">
              <el-card
                v-for="item in companyList"
                :key="item.companyId"
                class="company-card"
                shadow="hover"
                @click="goToCompanyDetail(item.companyId)"
              >
                <div class="company-logo-wrap">
                  <img
                    v-if="item.logoUrl"
                    :src="item.logoUrl"
                    alt="company-logo"
                    class="company-logo"
                  />
                  <div v-else class="company-logo company-logo-fallback">
                    {{ (item.companyName || "企").slice(0, 1) }}
                  </div>
                </div>
                <div class="company-name">
                  {{ item.companyName || "未命名公司" }}
                </div>
                <div class="company-meta">
                  {{ item.city || "城市待定" }} ·
                  {{ item.industry || "行业待定" }}
                </div>
                <div class="company-meta">
                  规模：{{ item.scale || "未填写" }}
                </div>
                <!-- <div class="company-intro">
                  {{ item.introduction || "暂无公司简介" }}
                </div> -->
              </el-card>
            </div>
            <el-empty
              v-if="!loading && companyList.length === 0"
              description="暂无公司数据"
            />
          </div>
        </el-main>
        <el-footer>
          <div class="center-pagination">
            <el-pagination
              background
              layout="prev, pager, next"
              :total="total"
              :current-page="currentPage"
              @current-change="handlePageChange"
            />
          </div>
        </el-footer>
      </el-container>
    </div>
    <el-backtop :right="100" :bottom="100" />
  </div>
</template>

<script setup>
import { Search } from "@element-plus/icons-vue";
import { onMounted, ref, watch } from "vue";
import { useRouter } from "vue-router";
import { getCompanyList } from "@/api/company";

const router = useRouter();

// 同步分页页码
const currentPage = ref(1);
const pageSize = ref(12);
const total = ref(0);
const loading = ref(false);
const companyList = ref([]);

const handlePageChange = (page) => {
  currentPage.value = page;
  fetchCompanyList();
};

const keyword = ref("");

const companyConditions = ref({
  scale: undefined,
  industry: undefined,
  city: undefined,
});

const value = ref("");
const queryAndJump = () => {
  currentPage.value = 1;
  fetchCompanyList();
};

const querySearchAsync = (queryString, cb) => {
  cb([]);
};

const fetchCompanyList = async () => {
  loading.value = true;
  try {
    const params = {
      companyName: keyword.value || undefined,
      city: companyConditions.value.city,
      industry: companyConditions.value.industry,
      scale: companyConditions.value.scale,
      pageNum: currentPage.value,
      pageSize: pageSize.value,
    };
    const res = await getCompanyList(params);
    const data = res?.data.data;
    companyList.value = data?.list || [];
    total.value = data?.total || 0;
  } catch (error) {
    console.error("获取公司列表失败:", error);
    companyList.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

const goToCompanyDetail = (companyId) => {
  router.push(`/company/${companyId}`);
};

const industrys = [
  { value: undefined, label: "不限" },
  { value: "互联网", label: "互联网" },
  { value: "电子商务", label: "电子商务" },
  { value: "软件开发", label: "软件开发" },
  { value: "硬件制造", label: "硬件制造" },
  { value: "半导体", label: "半导体" },
  { value: "人工智能", label: "人工智能" },
  { value: "大数据", label: "大数据" },
  { value: "云计算", label: "云计算" },
  { value: "物联网", label: "物联网" },
  { value: "金融科技", label: "金融科技" },
  { value: "教育培训", label: "教育培训" },
  { value: "文化传媒", label: "文化传媒" },
  { value: "广告营销", label: "广告营销" },
  { value: "医疗健康", label: "医疗健康" },
  { value: "生物技术", label: "生物技术" },
  { value: "制药", label: "制药" },
  { value: "医疗器械", label: "医疗器械" },
  { value: "房地产", label: "房地产" },
  { value: "建筑工程", label: "建筑工程" },
  { value: "物业管理", label: "物业管理" },
  { value: "制造业", label: "制造业" },
  { value: "汽车制造", label: "汽车制造" },
  { value: "新能源", label: "新能源" },
  { value: "环保", label: "环保" },
  { value: "化工", label: "化工" },
  { value: "消费品", label: "消费品" },
  { value: "零售", label: "零售" },
  { value: "批发", label: "批发" },
  { value: "餐饮", label: "餐饮" },
  { value: "酒店旅游", label: "酒店旅游" },
  { value: "物流运输", label: "物流运输" },
  { value: "供应链", label: "供应链" },
  { value: "咨询服务", label: "咨询服务" },
  { value: "法律服务", label: "法律服务" },
  { value: "会计审计", label: "会计审计" },
  { value: "人力资源", label: "人力资源" },
  { value: "保险", label: "保险" },
  { value: "证券基金", label: "证券基金" },
  { value: "银行", label: "银行" },
  { value: "信托", label: "信托" },
  { value: "农业", label: "农业" },
  { value: "林业", label: "林业" },
  { value: "畜牧业", label: "畜牧业" },
  { value: "渔业", label: "渔业" },
  { value: "采矿业", label: "采矿业" },
  { value: "能源", label: "能源" },
  { value: "电力", label: "电力" },
  { value: "水利", label: "水利" },
  { value: "电信", label: "电信" },
  { value: "广播电视", label: "广播电视" },
  { value: "邮政", label: "邮政" },
  { value: "体育", label: "体育" },
  { value: "娱乐", label: "娱乐" },
  { value: "非营利组织", label: "非营利组织" },
  { value: "政府机构", label: "政府机构" },
  { value: "国际组织", label: "国际组织" },
];

const citys = [
  { value: undefined, label: "不限" },
  { value: "北京", label: "北京" },
  { value: "上海", label: "上海" },
  { value: "广州", label: "广州" },
  { value: "深圳", label: "深圳" },
  { value: "杭州", label: "杭州" },
  { value: "成都", label: "成都" },
  { value: "重庆", label: "重庆" },
  { value: "武汉", label: "武汉" },
  { value: "西安", label: "西安" },
  { value: "南京", label: "南京" },
  { value: "苏州", label: "苏州" },
  { value: "天津", label: "天津" },
  { value: "郑州", label: "郑州" },
  { value: "长沙", label: "长沙" },
  { value: "东莞", label: "东莞" },
  { value: "佛山", label: "佛山" },
  { value: "宁波", label: "宁波" },
  { value: "青岛", label: "青岛" },
  { value: "沈阳", label: "沈阳" },
  { value: "昆明", label: "昆明" },
  { value: "大连", label: "大连" },
  { value: "厦门", label: "厦门" },
  { value: "无锡", label: "无锡" },
  { value: "福州", label: "福州" },
  { value: "济南", label: "济南" },
  { value: "哈尔滨", label: "哈尔滨" },
  { value: "长春", label: "长春" },
  { value: "石家庄", label: "石家庄" },
  { value: "合肥", label: "合肥" },
  { value: "南昌", label: "南昌" },
  { value: "南宁", label: "南宁" },
  { value: "贵阳", label: "贵阳" },
  { value: "海口", label: "海口" },
  { value: "太原", label: "太原" },
  { value: "乌鲁木齐", label: "乌鲁木齐" },
  { value: "兰州", label: "兰州" },
  { value: "银川", label: "银川" },
  { value: "西宁", label: "西宁" },
  { value: "呼和浩特", label: "呼和浩特" },
  { value: "拉萨", label: "拉萨" },
  { value: "香港", label: "香港" },
  { value: "澳门", label: "澳门" },
  { value: "台北", label: "台北" },
  { value: "高雄", label: "高雄" },
  { value: "台中", label: "台中" },
];

const scales = [
  {
    value: undefined,
    label: "不限",
  },
  {
    value: "0-20人",
    label: "0-20人",
  },
  {
    value: "20-99人",
    label: "20-99人",
  },
  {
    value: "100-499人",
    label: "100-499人",
  },
  {
    value: "500-999人",
    label: "500-999人",
  },
  {
    value: "1000-9999人",
    label: "1000-9999人",
  },
  {
    value: "10000人以上",
    label: "10000人以上",
  },
];

watch(
  companyConditions,
  () => {
    currentPage.value = 1;
    fetchCompanyList();
  },
  { deep: true },
);

onMounted(() => {
  fetchCompanyList();
});
</script>

<style lang="scss" scoped>
.company-page-container {
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
  .company-select-box {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 1em;
  }
  .company-list-box {
    flex: 1;
    padding-top: 1em;
    padding-bottom: 5em;
    display: flex;
    .center-pagination {
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100%;
    }
  }
  .comp-card-container {
    height: 100%;
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
        min-height: 170px;
        background-color: white;
        margin: 10px;
        box-sizing: border-box;
        cursor: pointer;
        padding: 14px 14px 12px;

        .company-header {
          display: flex;
          align-items: flex-start;
          margin-bottom: 12px;
        }

        .company-logo-wrap {
          flex-shrink: 0;
          margin-right: 10px; // 与名称拉开距离
        }

        .company-logo {
          width: 48px;
          height: 48px;
          border-radius: 10px;
          object-fit: cover;
          border: 1px solid #f0f0f0;
          background: #fff;
        }

        .company-logo-fallback {
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 18px;
          font-weight: 700;
          color: #1677ff;
          background: #eef5ff;
        }

        .company-name {
          flex: 1;
          font-size: 17px;
          font-weight: 700;
          color: #222;
          line-height: 1.35;
          margin-top: 2px;
          word-break: break-all;
        }

        .company-info-line {
          font-size: 13px;
          color: #666;
          line-height: 1.7;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }
    }
  }
}
</style>
