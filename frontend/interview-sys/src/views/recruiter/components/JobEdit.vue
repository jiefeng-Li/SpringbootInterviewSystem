<template>
  <div class="job-edit">
    <el-page-header @back="goBack" content="编辑职位" class="page-header" />

    <el-form
      ref="jobFormRef"
      :model="jobForm"
      :rules="formRules"
      label-width="120px"
      class="job-form"
    >
      <el-form-item label="职位标题" prop="title">
        <el-input v-model="jobForm.title" placeholder="请输入职位标题" />
      </el-form-item>

      <el-form-item label="公司名称" prop="companyName">
        <el-input
          v-model="jobForm.companyName"
          placeholder="请输入公司名称"
          disabled
        />
      </el-form-item>

      <el-form-item label="薪资范围" prop="salary">
        <el-col :span="11">
          <el-input
            v-model.number="jobForm.minSalary"
            placeholder="最低薪资"
            type="number"
          />
        </el-col>
        <el-col :span="2" class="text-center">-</el-col>
        <el-col :span="11">
          <el-input
            v-model.number="jobForm.maxSalary"
            placeholder="最高薪资"
            type="number"
          />
        </el-col>
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
        <el-select
          v-model="jobForm.jobType"
          placeholder="请选择工作性质"
          style="width: 100%"
        >
          <el-option label="全职" :value="0" />
          <el-option label="兼职" :value="1" />
          <el-option label="实习" :value="2" />
          <el-option label="远程" :value="3" />
        </el-select>
      </el-form-item>

      <el-form-item label="经验要求" prop="experience">
        <el-select
          v-model="jobForm.experience"
          placeholder="请选择经验要求"
          style="width: 100%"
        >
          <el-option label="经验不限" value="经验不限" />
          <el-option label="1年以下" value="1年以下" />
          <el-option label="1-3年" value="1-3年" />
          <el-option label="3-5年" value="3-5年" />
          <el-option label="5-10年" value="5-10年" />
          <el-option label="10年以上" value="10年以上" />
        </el-select>
      </el-form-item>

      <el-form-item label="学历要求" prop="education">
        <el-select
          v-model="jobForm.education"
          placeholder="请选择学历要求"
          style="width: 100%"
        >
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
        <el-input-number v-model="jobForm.headcount" :min="1" :max="100" />
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

      <el-form-item label="职位描述" prop="description">
        <el-input
          v-model="jobForm.description"
          type="textarea"
          :rows="6"
          placeholder="请输入职位描述"
        />
      </el-form-item>

      <el-form-item label="职位要求" prop="requirement">
        <el-input
          v-model="jobForm.requirement"
          type="textarea"
          :rows="6"
          placeholder="请输入职位要求"
        />
      </el-form-item>

      <el-form-item label="职位状态" prop="status">
        <el-radio-group v-model="jobForm.status">
          <el-radio :label="0">草稿</el-radio>
          <el-radio :label="1">招聘中</el-radio>
          <el-radio :label="2">已暂停</el-radio>
          <el-radio :label="3">已招满</el-radio>
          <el-radio :label="4">已关闭</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submitForm" :loading="submitting"
          >保存</el-button
        >
        <el-button @click="goBack">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { regionData } from "element-china-area-data";
import { ElMessage } from "element-plus";
import {
  getAllJobTags,
  getJobPositionById,
  updateJobPosition,
  updateJobPositionStatus,
} from "@/api/job";

const router = useRouter();
const route = useRoute();
const jobFormRef = ref(null);
const submitting = ref(false);
const tagOptions = ref([]);
const cityOptions = ref(regionData);
// 城市选项配置
const cityProps = {
  expandTrigger: "hover",
  value: "label",
  label: "label",
  children: "children",
};

// 表单数据
const jobForm = ref({
  id: null,
  companyName: "",
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
  hiringManagerId: null,
  hiringManagerName: "",
  status: 0,
});

const oldStatus = ref(0);

// 表单验证规则
const formRules = {
  title: [{ required: true, message: "请输入职位标题", trigger: "blur" }],
  workCity: [{ required: true, message: "请选择工作城市", trigger: "change" }],
  jobType: [{ required: true, message: "请选择工作性质", trigger: "change" }],
  experience: [
    { required: true, message: "请选择经验要求", trigger: "change" },
  ],
  education: [{ required: true, message: "请选择学历要求", trigger: "change" }],
  headcount: [{ required: true, message: "请输入招聘人数", trigger: "blur" }],
  description: [{ required: true, message: "请输入职位描述", trigger: "blur" }],
  requirement: [{ required: true, message: "请输入职位要求", trigger: "blur" }],
};

// 获取职位详情
const fetchJobDetail = async (id) => {
  try {
    const response = await getJobPositionById(id);
    const jobData = response.data.data;
    console.log("jobData", jobData);

    // 将状态字符串转换为数字
    let statusValue = 0;
    switch (jobData.status) {
      case "草稿":
        statusValue = 0;
        break;
      case "招聘中":
        statusValue = 1;
        break;
      case "已暂停":
        statusValue = 2;
        break;
      case "已招满":
        statusValue = 3;
        break;
      case "已关闭":
        statusValue = 4;
        break;
    }
    // 将工作性质字符串转换为数字
    let jobTypeValue = 0;
    switch (jobData.jobType) {
      case "全职":
        jobTypeValue = 0;
        break;
      case "兼职":
        jobTypeValue = 1;
        break;
      case "实习":
        jobTypeValue = 2;
        break;
      case "远程":
        jobTypeValue = 3;
        break;
    }
    let workCityValue = [];
    if (jobData.workCity) {
      // 如果workCity是字符串，尝试分割为数组
      if (typeof jobData.workCity === "string") {
        workCityValue = jobData.workCity.split("/");
      } else if (Array.isArray(jobData.workCity)) {
        workCityValue = jobData.workCity;
      }
    }

    jobForm.value = {
      ...jobData,
      status: statusValue,
      jobType: jobTypeValue,
      workCity: workCityValue,
    };
    oldStatus = jobForm.value.status;
  } catch (error) {
    console.error("获取职位详情失败:", error);
    ElMessage.error("获取职位详情失败");
  }
};

// 提交表单
const submitForm = async () => {
  if (!jobFormRef.value) return;

  await jobFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      jobForm.value.jobType = parseInt(jobForm.value.jobType);

      const workCityString = Array.isArray(jobForm.value.workCity)
        ? jobForm.value.workCity.join("/")
        : jobForm.value.workCity;
      jobForm.value.workCity = workCityString;
      console.log("表单数据:", jobForm.value);
      try {
        // 先更新职位基本信息
        const { status, id, ...jobData } = jobForm.value;

        await updateJobPosition(id, jobData);
        console.log("updateJobPosition");
        // 再更新职位状态
        if (status !== oldStatus)
          await updateJobPositionStatus(id, status);

        ElMessage.success("职位信息更新成功");
        router.push(`/recruiter/job/${jobForm.value.id}`);
      } catch (error) {
        console.error("更新职位信息失败:", error);
        ElMessage.error("更新职位信息失败");
      } finally {
        submitting.value = false;
      }
    }
  });
};

// 返回上一页
const goBack = () => {
  router.back();
};

// 组件挂载时获取职位信息
onMounted(() => {
  const jobId = route.params.id;
  if (jobId) {
    fetchJobDetail(jobId);
  }
  getAllJobTags().then((res) => {
    tagOptions.value = res.data.data;
  });
});
</script>

<style lang="scss" scoped>
.job-edit {
  padding: 20px;
  background: #fff;
  border-radius: 4px;
}

.page-header {
  margin-bottom: 20px;
}

.job-form {
  max-width: 800px;
}

.text-center {
  text-align: center;
}
</style>
