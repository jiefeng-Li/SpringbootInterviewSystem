<template>
  <div class="sys-statistics">
    <el-skeleton :loading="loading" animated :rows="6">
      <template #default>
        <div class="cards">
          <el-card shadow="hover" class="kpi-card">
            <div class="kpi-title">今日活跃人数</div>
            <div class="kpi-value">
              {{ overview.todayActiveUserCount || 0 }}
            </div>
          </el-card>
          <el-card shadow="hover" class="kpi-card">
            <div class="kpi-title">本周活跃人数</div>
            <div class="kpi-value">{{ overview.weekActiveUserCount || 0 }}</div>
          </el-card>
          <el-card shadow="hover" class="kpi-card">
            <div class="kpi-title">本周发布职位数</div>
            <div class="kpi-value">
              {{ overview.weekPublishedJobCount || 0 }}
            </div>
          </el-card>
          <el-card shadow="hover" class="kpi-card">
            <div class="kpi-title">今日新增投递数</div>
            <div class="kpi-value">
              {{ overview.todayJobApplicationCount || 0 }}
            </div>
          </el-card>
        </div>

        <el-row :gutter="12">
          <el-col :xs="24" :md="16">
            <el-card shadow="never" class="chart-card">
              <template #header>
                <div class="card-header">公司状态分布</div>
              </template>
              <v-chart class="chart" :option="companyStatusOption" autoresize />
            </el-card>
          </el-col>
          <el-col :xs="24" :md="8">
            <el-card shadow="never" class="summary-card">
              <template #header>
                <div class="card-header">系统概览</div>
              </template>
              <div class="summary-item">
                <span>公司总数</span>
                <b>{{ overview.totalCompanyCount || 0 }}</b>
              </div>
              <div class="summary-item">
                <span>统计范围</span>
                <b
                  >{{ overview.weekStart || "-" }} ~
                  {{ overview.today || "-" }}</b
                >
              </div>
            </el-card>
          </el-col>
        </el-row>
      </template>
    </el-skeleton>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { getSysOverview } from "@/api/statistics";
import VChart from "vue-echarts";
import { use } from "echarts/core";
import { CanvasRenderer } from "echarts/renderers";
import { PieChart } from "echarts/charts";
import { LegendComponent, TooltipComponent } from "echarts/components";

use([CanvasRenderer, PieChart, TooltipComponent, LegendComponent]);

const loading = ref(false);
const overview = reactive({
  todayActiveUserCount: 0,
  weekActiveUserCount: 0,
  weekPublishedJobCount: 0,
  todayJobApplicationCount: 0,
  totalCompanyCount: 0,
  companyStatusStats: [],
  weekStart: "",
  today: "",
});

const companyStatusOption = computed(() => ({
  tooltip: { trigger: "item" },
  legend: { bottom: 0 },
  series: [
    {
      name: "公司状态",
      type: "pie",
      radius: ["45%", "70%"],
      avoidLabelOverlap: false,
      label: {
        show: true,
        formatter: "{b}: {c}",
      },
      data: (overview.companyStatusStats || []).map((item) => ({
        name: item.text,
        value: item.count,
      })),
    },
  ],
}));

const fetchOverview = async () => {
  loading.value = true;
  try {
    const { data } = await getSysOverview();
    const source = data?.data || data || {};
    overview.todayActiveUserCount = source.todayActiveUserCount || 0;
    overview.weekActiveUserCount = source.weekActiveUserCount || 0;
    overview.weekPublishedJobCount = source.weekPublishedJobCount || 0;
    overview.todayJobApplicationCount = source.todayJobApplicationCount || 0;
    overview.totalCompanyCount = source.totalCompanyCount || 0;
    overview.companyStatusStats = source.companyStatusStats || [];
    overview.weekStart = source.weekStart || "";
    overview.today = source.today || "";
  } catch (e) {
    ElMessage.error(e?.message || "获取系统统计数据失败");
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchOverview();
});
</script>

<style lang="scss" scoped>
.sys-statistics {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .cards {
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 12px;
    margin-bottom: 12px;
  }

  .kpi-card {
    .kpi-title {
      color: #606266;
      font-size: 14px;
      margin-bottom: 8px;
    }

    .kpi-value {
      font-size: 32px;
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
      height: 360px;
    }
  }

  .summary-card {
    min-height: 420px;

    .summary-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 10px 0;
      border-bottom: 1px dashed #ebeef5;

      b {
        color: #0f172a;
      }
    }
  }
}

@media (max-width: 900px) {
  .sys-statistics {
    .cards {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
  }
}
</style>
