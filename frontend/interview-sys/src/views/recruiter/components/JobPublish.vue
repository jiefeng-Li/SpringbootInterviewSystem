<template>
  <div class="job-publish-container">
    <el-card class="publish-card">
      <template #header>
        <div class="card-header">
          <span>发布新职位</span>
        </div>
      </template>

      <el-form
        ref="jobFormRef"
        :model="jobForm"
        :rules="formRules"
        label-width="120px"
        class="job-form"
      >
        <!-- 职位基本信息 -->
        <div class="form-section">
          <h3 class="section-title">基本信息</h3>

          <el-form-item label="职位名称" prop="title">
            <el-input
              v-model="jobForm.title"
              placeholder="请输入职位名称"
              maxlength="50"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="职位描述" prop="description">
            <el-input
              v-model="jobForm.description"
              type="textarea"
              :rows="6"
              placeholder="请输入职位描述"
              maxlength="1000"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="职位要求" prop="requirement">
            <el-input
              v-model="jobForm.requirement"
              type="textarea"
              :rows="6"
              placeholder="请输入职位要求"
              maxlength="1000"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="工作城市" prop="workCity">
            <el-cascader
              v-model="jobForm.workCity"
              :options="cityOptions"
              :props="cityProps"
              placeholder="请选择工作城市"
              style="width: 100%"
            />
          </el-form-item>

          <el-form-item label="工作性质" prop="jobType">
            <el-radio-group v-model="jobForm.jobType">
              <el-radio :label="0">全职</el-radio>
              <el-radio :label="1">兼职</el-radio>
              <el-radio :label="2">实习</el-radio>
              <el-radio :label="3">远程</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="职位标签" prop="tags">
            <el-select
              v-model="jobForm.tags"
              multiple
              filterable
              allow-create
              placeholder="请选择或输入职位标签"
              style="width: 100%"
            >
              <el-option
                v-for="item in tagOptions"
                :key="item"
                :label="item"
                :value="item"
              />
            </el-select>
          </el-form-item>
        </div>

        <!-- 薪资待遇 -->
        <div class="form-section">
          <h3 class="section-title">薪资待遇</h3>

          <el-form-item label="薪资范围" prop="minSalary">
            <el-col :span="11">
              <el-input-number
                v-model="jobForm.minSalary"
                :min="0"
                :step="1000"
                placeholder="最低薪资"
                style="width: 100%"
              />
            </el-col>
            <el-col :span="2" style="text-align: center">-</el-col>
            <el-col :span="11">
              <el-input-number
                v-model="jobForm.maxSalary"
                :min="0"
                :step="1000"
                placeholder="最高薪资"
                style="width: 100%"
              />
            </el-col>
          </el-form-item>
        </div>

        <!-- 任职要求 -->
        <div class="form-section">
          <h3 class="section-title">任职要求</h3>

          <el-form-item label="经验要求" prop="experience">
            <el-select
              v-model="jobForm.experience"
              placeholder="请选择经验要求"
            >
              <el-option>不限</el-option>
              <el-option value="在校生">在校生</el-option>
              <el-option value="应届生">应届生</el-option>
              <el-option value="经验不限">经验不限</el-option>
              <el-option value="一年以内">一年以内</el-option>
              <el-option value="1-3年">1-3年</el-option>
              <el-option value="3-5年">3-5年</el-option>
              <el-option value="5-10年">5-10年</el-option>
              <el-option value="10年以上">10年以上</el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="学历要求" prop="education">
            <el-select v-model="jobForm.education" placeholder="请选择学历要求">
              <el-option label="不限" value="不限" />
              <el-option label="初中及以下" value="初中及以下" />
              <el-option label="中专/中技" value="中专/中技" />
              <el-option label="高中" value="高中" />
              <el-option label="大专" value="大专" />
              <el-option label="本科" value="本科" />
              <el-option label="硕士" value="硕士" />
              <el-option label="博士" value="博士" />
            </el-select>
          </el-form-item>

          <el-form-item label="招聘人数" prop="headcount">
            <el-input-number
              v-model="jobForm.headcount"
              :min="1"
              :max="999"
              placeholder="请输入招聘人数"
              style="width: 100%"
            />
          </el-form-item>
        </div>

        <!-- 操作按钮 -->
        <div class="form-actions">
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="info" @click="handleSaveDraft">暂存为草稿</el-button>
          <el-button type="primary" @click="handlePublish"
            >完成并发布</el-button
          >
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { ElMessage } from "element-plus";
import { regionData } from "element-china-area-data";
import { getAllJobTags, addJobPosition } from "@/api/job";
import { onMounted } from "vue";

const isEdit = ref(false);
const jobFormRef = ref(null);

// 表单数据
const jobForm = reactive({
  title: "",
  description: "",
  requirement: "",
  workCity: [],
  jobType: 0,
  tags: [],
  minSalary: null,
  maxSalary: null,
  experience: "",
  education: "",
  headcount: 1,
  status: 0, // 默认为草稿状态
});

// 表单验证规则
// 表单验证规则
const formRules = {
  title: [
    { required: true, message: "请输入职位名称", trigger: "blur" },
    { min: 2, max: 50, message: "长度在 2 到 50 个字符", trigger: "blur" },
  ],
  description: [
    { required: true, message: "请输入职位描述", trigger: "blur" },
    {
      min: 10,
      max: 1000,
      message: "长度在 10 到 1000 个字符",
      trigger: "blur",
    },
  ],
  workCity: [{ required: true, message: "请选择工作城市", trigger: "change" }],
  jobType: [{ required: true, message: "请选择工作性质", trigger: "change" }],
  minSalary: [
    { required: true, message: "请输入最低薪资", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (value === null || value === undefined) {
          callback(new Error("请输入最低薪资"));
        } else if (jobForm.maxSalary !== null && value > jobForm.maxSalary) {
          callback(new Error("最低薪资不能高于最高薪资"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
  maxSalary: [
    { required: true, message: "请输入最高薪资", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (value === null || value === undefined) {
          callback(new Error("请输入最高薪资"));
        } else if (jobForm.minSalary !== null && value < jobForm.minSalary) {
          callback(new Error("最高薪资不能低于最低薪资"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
  experience: [
    { required: true, message: "请选择经验要求", trigger: "change" },
  ],
  education: [{ required: true, message: "请选择学历要求", trigger: "change" }],
  headcount: [{ required: true, message: "请输入招聘人数", trigger: "blur" }],
};

const cityOptions = ref(regionData);

// 城市选项配置
const cityProps = {
  expandTrigger: "hover",
  value: "label",
  label: "label",
  children: "children",
};
// 职位标签选项
const tagOptions = ref([]);

// 暂存为草稿
const handleSaveDraft = async () => {
  try {
    // 设置状态为草稿
    jobForm.status = 0;
    // 这里调用API保存职位信息
    await saveJob(jobForm);
    ElMessage.success("已保存为草稿");
  } catch (error) {
    ElMessage.error("保存失败：" + error.message);
  }
};

const saveJob = async (jobData) => {
  const workCityString = Array.isArray(jobData.workCity)
    ? jobData.workCity.join("/")
    : jobData.workCity;
  jobData.workCity = workCityString;
  console.log("保存职位数据：", jobData);

  // 这里应该替换为实际的API调用
  addJobPosition(jobData);
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve();
    }, 500);
  });
};

// 发布职位
const handlePublish = async () => {
  try {
    // 验证表单
    await jobFormRef.value.validate();
    // 设置状态为招聘中
    jobForm.status = 1;
    // 这里调用API保存职位信息
    await saveJob(jobForm);
    ElMessage.success("职位发布成功");
  } catch (error) {
    if (error !== false) {
      ElMessage.error("发布失败：" + error.message);
    }
  }
};

// 取消操作
const handleCancel = () => {
  jobFormRef.value.resetFields();
};

// 模拟保存职位的API调用
// 模拟保存职位的API调用

onMounted(() => {
  getAllJobTags().then((res) => {
    tagOptions.value = res.data.data;
  });
});
</script>

<style lang="scss" scoped>
.job-publish-container {
  padding: 20px;

  .publish-card {
    max-width: 900px;
    margin: 0 auto;

    .card-header {
      font-size: 18px;
      font-weight: bold;
    }

    .job-form {
      .form-section {
        margin-bottom: 30px;

        .section-title {
          font-size: 16px;
          color: #303133;
          margin-bottom: 20px;
          padding-bottom: 10px;
          border-bottom: 1px solid #ebeef5;
        }
      }

      .form-actions {
        margin-top: 40px;
        text-align: center;

        .el-button {
          margin: 0 10px;
          min-width: 120px;
        }
      }
    }
  }
}
</style>
