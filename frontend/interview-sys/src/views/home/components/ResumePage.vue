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
      <el-main>
        <el-container style="height: 100%">
          <el-main style="padding: 0">
            <div
              style="
                height: 4em;
                width: 100%;
                display: flex;
                justify-content: flex-end;
                align-items: center;
                gap: 10px;
              "
            >
              <el-button type="primary" :icon="Edit">新建</el-button>
              <el-button type="primary" :icon="Upload">上传</el-button>
            </div>
            <div style="height: calc(100% - 120px)">
              <el-scrollbar>
                <transition-group name="list" tag="div">
                  <div
                    v-for="item in resumes"
                    :key="item"
                    class="scrollbar-resume-item"
                  >
                    <div class="resuem-info">{{ item }}</div>
                    <div class="buttons">
                      <el-tooltip
                        class="box-item"
                        effect="light"
                        content="设为默认"
                        placement="top"
                        :disabled="item == defaultIndex"
                      >
                        <el-button
                          type="info"
                          :icon="Star"
                          circle
                          @click="setDefalutResume(item)"
                          v-if="item != defaultIndex"
                        />
                        <el-button
                          type="success"
                          :icon="Check"
                          circle
                          v-if="item == defaultIndex"
                        />
                      </el-tooltip>

                      <el-button type="primary" :icon="Edit" circle />
                      <el-popconfirm
                        class="box-item"
                        title="是否确认删除？"
                        placement="top"
                        @confirm="deleteResume(item)"
                      >
                        <template #reference>
                          <el-button type="danger" :icon="Delete" circle />
                        </template>
                      </el-popconfirm>
                    </div>
                  </div>
                </transition-group>
              </el-scrollbar>
            </div>
          </el-main>
          <el-aside></el-aside>
        </el-container>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import {
  ChatLineRound,
  Male,
  Edit,
  Upload,
  Delete,
  Star,
  Check,
} from "@element-plus/icons-vue";
import { onMounted, onBeforeUnmount } from "vue";
import { ElMessage } from "element-plus";
import { ref } from "vue";

const resumes = ref(Array.from({ length: 40 }, (_, i) => i + 1));
const defaultIndex = ref(resumes.value[0]);

const setDefalutResume = (index) => {
  if (!resumes.value.includes(index)) return;

  // 移除该项
  resumes.value = resumes.value.filter((item) => item !== index);

  // 添加到开头
  resumes.value.unshift(index);

  // 设置默认
  defaultIndex.value = index;
};

const deleteResume = (index) => {
  const isDefault = index === defaultIndex.value;
  if (isDefault) {
    ElMessage.warning("删除默认简历");
  }

  resumes.value = resumes.value.filter((item) => item !== index);

  if (isDefault) {
    defaultIndex.value = resumes.value[0] ?? 1;
  }

  ElMessage.success("删除成功");
};

onMounted(() => {
  document.body.style.overflow = "hidden";
});

onBeforeUnmount(() => {
  document.body.style.overflow = "";
});
</script>

<style lang="scss" scoped>
.resume-page-container {
  width: 100%;
  // height: 100%;
  height: 100vh;
  display: flex;
  overflow: hidden;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
  .el-container {
    .el-header {
      background-color: #b3c0d1;
      width: 100%;
      height: 120px;
      display: flex;
      align-items: center;
      .states-data {
        width: 75%;
      }
    }
  }
  .scrollbar-resume-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 50px;
    margin: 10px;
    text-align: center;
    border-radius: 4px;
    background: var(--el-color-primary-light-9);
    color: var(--el-color-primary);
  }
  .buttons {
    display: flex;
    gap: 5px;
    //禁用选中
  }
  .list-move {
    transition: transform 0.3s ease;
  }
  .list-enter-active,
  .list-leave-active {
    transition: all 0.3s;
  }
  .list-enter-from,
  .list-leave-to {
    opacity: 0;
    transform: translateX(30px);
  }
  .tooltip-base-box .box-item {
    width: 110px;
    margin-top: 10px;
  }
}
</style>
