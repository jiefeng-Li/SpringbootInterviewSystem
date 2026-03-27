<template>
  <div>
    <h1>API Test</h1>
    <el-input-number v-model="resumeId" :min="1" label="resumeId" />
    <el-input-number
      v-model="templateId"
      :min="1"
      :max="4"
      label="templateId"
      style="margin-left: 12px"
    />
    <el-button @click="testApi">Test API</el-button>
  </div>
</template>

<script setup>
import { getResumeDownload } from "@/api/resume";
import { ElMessage } from "element-plus";
import { ref } from "vue";

const resumeId = ref(9);
const templateId = ref(2);

async function testApi() {
  try {
    const exportRes = await getResumeDownload(resumeId.value, templateId.value);
    const contentType = exportRes?.headers?.["content-type"] || "";
    if (!contentType.includes("application/pdf")) {
      const errorText = await exportRes?.data?.text?.();
      ElMessage.error(errorText || "导出失败，返回的不是PDF文件");
      return;
    }

    if (exportRes?.data) {
      // 创建下载链接
      const blob = exportRes.data;
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute(
        "download",
        `resume_${resumeId.value}_tpl_${templateId.value}.pdf`,
      );
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);

      ElMessage.success("简历下载成功");
    }
  } catch (error) {
    console.error("API Error:", error);
    ElMessage.error("简历下载失败");
  }
}
</script>

<style lang="scss" scoped></style>
