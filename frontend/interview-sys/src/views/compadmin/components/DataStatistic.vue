<template>
  <div class="comp-statistics">
    <el-skeleton :loading="loading" animated :rows="6">
      <template #default>
        <div class="cards">
          <el-card shadow="hover" class="kpi-card">
            <div class="kpi-title">今日发布职位</div>
            <div class="kpi-value">
              {{ overview.todayPublishedJobCount || 0 }}
            </div>
          </el-card>
          <el-card shadow="hover" class="kpi-card">
            <div class="kpi-title">本周发布职位</div>
            <div class="kpi-value">
              {{ overview.weekPublishedJobCount || 0 }}
            </div>
          </el-card>
          <el-card shadow="hover" class="kpi-card">
            <div class="kpi-title">今日投递数</div>
            <div class="kpi-value">
              {{ overview.todayApplicationCount || 0 }}
            </div>
          </el-card>
          <el-card shadow="hover" class="kpi-card">
            <div class="kpi-title">本周投递数</div>
            <div class="kpi-value">
              {{ overview.weekApplicationCount || 0 }}
            </div>
          </el-card>
          <el-card shadow="hover" class="kpi-card">
            <div class="kpi-title">今日发送 Offer</div>
            <div class="kpi-value">{{ overview.todayOfferCount || 0 }}</div>
          </el-card>
          <el-card shadow="hover" class="kpi-card">
            <div class="kpi-title">本周发送 Offer</div>
            <div class="kpi-value">{{ overview.weekOfferCount || 0 }}</div>
          </el-card>
          <el-card shadow="hover" class="kpi-card">
            <div class="kpi-title">公司用户数</div>
            <div class="kpi-value">{{ overview.companyUserCount || 0 }}</div>
          </el-card>
        </div>

        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">本周与今日关键指标对比</div>
          </template>
          <v-chart class="chart" :option="compareOption" autoresize />
          <div class="range-text">
            统计范围：{{ overview.weekStart || "-" }} ~
            {{ overview.today || "-" }}
          </div>
        </el-card>
      </template>
    </el-skeleton>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { getCompanyOverview } from "@/api/statistics";
import VChart from "vue-echarts";
import { use } from "echarts/core";
import { CanvasRenderer } from "echarts/renderers";
import { BarChart } from "echarts/charts";
import {
  GridComponent,
  LegendComponent,
  TooltipComponent,
} from "echarts/components";

use([
  CanvasRenderer,
  BarChart,
  GridComponent,
  TooltipComponent,
  LegendComponent,
]);

const loading = ref(false);
const overview = reactive({
  todayPublishedJobCount: 0,
  weekPublishedJobCount: 0,
  todayApplicationCount: 0,
  weekApplicationCount: 0,
  todayOfferCount: 0,
  weekOfferCount: 0,
  companyUserCount: 0,
  weekStart: "",
  today: "",
});

const compareOption = computed(() => ({
  tooltip: { trigger: "axis" },
  legend: { top: 0 },
  grid: { left: 20, right: 20, bottom: 20, top: 40, containLabel: true },
  xAxis: {
    type: "category",
    data: ["职位发布", "投递记录", "Offer发送"],
  },
  yAxis: {
    type: "value",
    minInterval: 1,
  },
  series: [
    {
      name: "今日",
      type: "bar",
      data: [
        overview.todayPublishedJobCount || 0,
        overview.todayApplicationCount || 0,
        overview.todayOfferCount || 0,
      ],
      itemStyle: { color: "#10b981" },
    },
    {
      name: "本周",
      type: "bar",
      data: [
        overview.weekPublishedJobCount || 0,
        overview.weekApplicationCount || 0,
        overview.weekOfferCount || 0,
      ],
      itemStyle: { color: "#2563eb" },
    },
  ],
}));

const fetchOverview = async () => {
  loading.value = true;
  try {
    const { data } = await getCompanyOverview();
    const source = data?.data || data || {};
    overview.todayPublishedJobCount = source.todayPublishedJobCount || 0;
    overview.weekPublishedJobCount = source.weekPublishedJobCount || 0;
    overview.todayApplicationCount = source.todayApplicationCount || 0;
    overview.weekApplicationCount = source.weekApplicationCount || 0;
    overview.todayOfferCount = source.todayOfferCount || 0;
    overview.weekOfferCount = source.weekOfferCount || 0;
    overview.companyUserCount = source.companyUserCount || 0;
    overview.weekStart = source.weekStart || "";
    overview.today = source.today || "";
  } catch (e) {
    ElMessage.error(e?.message || "获取企业统计数据失败");
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchOverview();
});
</script>

<style lang="scss" scoped>
.comp-statistics {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .cards {
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 12px;
  }

  .kpi-card {
    .kpi-title {
      color: #606266;
      font-size: 14px;
      margin-bottom: 8px;
    }

    .kpi-value {
      font-size: 30px;
      line-height: 1;
      font-weight: 700;
      color: #0f172a;
    }
  }

  .card-header {
    font-weight: 600;
  }

  .chart-card {
    .chart {
      height: 380px;
    }

    .range-text {
      margin-top: 8px;
      color: #909399;
      font-size: 13px;
    }
  }
}

@media (max-width: 1200px) {
  .comp-statistics {
    .cards {
      grid-template-columns: repeat(3, minmax(0, 1fr));
    }
  }
}

@media (max-width: 900px) {
  .comp-statistics {
    .cards {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
  }
}
</style>
