<template>
  <div class="system-message-page">
    <el-card shadow="never" class="message-card">
      <template #header>
        <div class="card-header">
          <span>系统消息推送</span>
          <el-tag type="info">管理员专用</el-tag>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="发送范围" prop="sendToAll">
          <el-radio-group v-model="form.sendToAll">
            <el-radio :value="true">全量用户</el-radio>
            <el-radio :value="false">指定用户</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item
          label="目标用户ID"
          prop="targetUserIdsText"
          v-if="!form.sendToAll"
        >
          <el-input
            v-model="form.targetUserIdsText"
            type="textarea"
            :rows="3"
            placeholder="请输入用户ID，使用英文逗号分隔，例如：1001,1002,1003"
          />
          <div class="hint">仅在“指定用户”模式下生效。</div>
        </el-form-item>

        <el-form-item label="包含自己">
          <el-switch v-model="form.includeSelf" />
        </el-form-item>

        <el-form-item label="消息内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="6"
            maxlength="1000"
            show-word-limit
            placeholder="请输入要推送的系统消息内容"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            发送并推送
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>

      <el-alert
        title="说明：在线用户会实时收到通知；离线用户会在下次连接后收到未读系统消息。"
        type="success"
        :closable="false"
        show-icon
      />
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { pushSystemMessage } from "@/api/chat";

const formRef = ref(null);
const submitting = ref(false);

const form = reactive({
  sendToAll: true,
  targetUserIdsText: "",
  includeSelf: false,
  content: "",
});

const rules = {
  content: [
    { required: true, message: "请输入消息内容", trigger: "blur" },
    { min: 1, max: 1000, message: "消息长度需在1-1000之间", trigger: "blur" },
  ],
  targetUserIdsText: [
    {
      validator: (_rule, value, callback) => {
        if (form.sendToAll) {
          callback();
          return;
        }
        if (!String(value || "").trim()) {
          callback(new Error("请输入至少一个目标用户ID"));
          return;
        }
        const ids = parseTargetUserIds(value);
        if (!ids.length) {
          callback(new Error("目标用户ID格式不正确"));
          return;
        }
        callback();
      },
      trigger: "blur",
    },
  ],
};

const parseTargetUserIds = (text) => {
  const parts = String(text || "")
    .split(",")
    .map((item) => item.trim())
    .filter(Boolean);

  const set = new Set();
  parts.forEach((item) => {
    const id = Number(item);
    if (Number.isInteger(id) && id > 0) {
      set.add(id);
    }
  });
  return Array.from(set);
};

const handleSubmit = async () => {
  await formRef.value?.validate();

  const payload = {
    sendToAll: form.sendToAll,
    includeSelf: form.includeSelf,
    content: form.content.trim(),
  };
  if (!form.sendToAll) {
    payload.targetUserIds = parseTargetUserIds(form.targetUserIdsText);
  }

  submitting.value = true;
  try {
    const res = await pushSystemMessage(payload);
    const count = Number(res?.data?.data || 0);
    ElMessage.success(`发送成功，已推送 ${count} 位用户`);
  } catch {
    ElMessage.error("发送失败，请稍后重试");
  } finally {
    submitting.value = false;
  }
};

const resetForm = () => {
  form.sendToAll = true;
  form.targetUserIdsText = "";
  form.includeSelf = false;
  form.content = "";
  formRef.value?.clearValidate();
};
</script>

<style scoped>
.system-message-page {
  padding: 20px;
}

.message-card {
  max-width: 900px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
}

.hint {
  margin-top: 6px;
  color: #909399;
  font-size: 12px;
}
</style>
