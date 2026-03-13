<template>
  <div class="resume-page-container">
    <el-container>
      <el-header>
        <el-image
          style="width: 100px; height: 100px"
          src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
        />
        <div class="states-data">
          <el-row :gutter="16">
            <el-col :xs="24" :sm="12" :md="6" class="text-center mb-4">
              <el-statistic title="Daily active users" :value="268500" />
            </el-col>
            <el-col :xs="24" :sm="12" :md="6" class="text-center mb-4">
              <el-statistic :value="138">
                <template #title>
                  <div style="display: inline-flex; align-items: center">
                    Ratio of men to women
                    <el-icon style="margin-left: 4px" :size="12">
                      <Male />
                    </el-icon>
                  </div>
                </template>
                <template #suffix>/100</template>
              </el-statistic>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6" class="text-center mb-4">
              <el-statistic title="Total Transactions" :value="outputValue" />
            </el-col>
            <el-col :xs="24" :sm="12" :md="6" class="text-center mb-4">
              <el-statistic title="Feedback number" :value="562">
                <template #suffix>
                  <el-icon style="vertical-align: -0.125em">
                    <ChatLineRound />
                  </el-icon>
                </template>
              </el-statistic>
            </el-col>
          </el-row>
        </div>
      </el-header>
      <el-main style="display: flex">
        <el-container>
          <el-main> resume </el-main>
          <el-aside style="width: 200px"> aside </el-aside>
        </el-container>
      </el-main>
    </el-container>
  </div>
</template>

<script setup></script>

<style lang="scss" scoped>
.resume-page-container {
  width: 100%;
  height: 100vh;
  display: flex;
  overflow: hidden;
  .el-container {
    .el-header {
      background-color: #b3c0d1;
      height: 120px;
      display: flex;
      align-items: center;
      .states-data {
        width: 75%;
      }
    }
  }
}
</style>
