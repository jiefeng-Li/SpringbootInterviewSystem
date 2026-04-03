<template>
  <div class="chat-page">
    <el-card class="chat-shell" shadow="never">
      <div class="chat-layout">
        <aside class="chat-sidebar">
          <div class="sidebar-header">
            <span>会话</span>
            <el-button text type="primary" @click="showStartDialog = true"
              >发起会话</el-button
            >
          </div>

          <el-scrollbar class="contact-scroll">
            <div
              v-for="item in contacts"
              :key="item.userId"
              class="contact-item"
              :class="{ active: activeContact?.userId === item.userId }"
              @click="selectContact(item)"
            >
              <el-avatar :src="item.avatarUrl" :size="36">{{
                item.username?.[0] || "U"
              }}</el-avatar>
              <div class="contact-main">
                <div class="name-row">
                  <span class="name">{{ item.username }}</span>
                  <span class="time">{{ formatTime(item.lastTime) }}</span>
                </div>
                <div class="meta-row">
                  <span class="preview">{{
                    item.lastContent || "暂无消息"
                  }}</span>
                  <el-badge
                    v-if="item.unreadCount > 0"
                    :value="item.unreadCount"
                  />
                </div>
              </div>
            </div>
            <el-empty
              v-if="!contacts.length"
              description="暂无会话"
              :image-size="80"
            />
          </el-scrollbar>
        </aside>

        <main class="chat-main">
          <template v-if="activeContact">
            <div class="chat-header">
              <div class="title">{{ activeContact.username }}</div>
              <div class="sub">
                实时连接：{{ wsConnected ? "在线" : "离线" }}
              </div>
            </div>

            <el-scrollbar ref="messageScrollRef" class="message-scroll">
              <div class="message-list">
                <div
                  v-for="msg in currentMessages"
                  :key="msg.id || msg.timestamp"
                  class="message-row"
                  :class="isSelfMessage(msg) ? 'self' : 'other'"
                >
                  <div class="bubble-wrap">
                    <div
                      class="bubble"
                      :class="msg.status === 2 ? 'revoke' : ''"
                    >
                      <template v-if="msg.status === 2">该消息已撤回</template>
                      <template v-else>{{ msg.content }}</template>
                    </div>
                    <div class="meta">
                      <span>{{
                        formatTime(msg.sentTime || msg.timestamp)
                      }}</span>
                      <span v-if="isSelfMessage(msg)">{{
                        readStatusText(msg)
                      }}</span>
                      <el-button
                        v-if="isSelfMessage(msg) && canRevoke(msg)"
                        type="danger"
                        text
                        size="small"
                        @click="handleRevoke(msg)"
                      >
                        撤回
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
            </el-scrollbar>

            <div class="chat-input">
              <el-input
                v-model="draft"
                type="textarea"
                :rows="3"
                resize="none"
                maxlength="1000"
                show-word-limit
                placeholder="输入消息，Enter发送，Shift+Enter换行"
                @keydown.enter.prevent="onEnterSend"
              />
              <div class="actions">
                <el-button @click="draft = ''">清空</el-button>
                <el-button
                  type="primary"
                  :disabled="!wsConnected"
                  @click="sendMessage"
                  >发送</el-button
                >
              </div>
            </div>
          </template>
          <el-empty v-else description="请选择会话" :image-size="100" />
        </main>
      </div>
    </el-card>

    <el-dialog
      v-model="showStartDialog"
      title="发起会话"
      width="420px"
      destroy-on-close
    >
      <el-form label-width="100px">
        <el-form-item label="对方用户ID">
          <el-input
            v-model.number="startForm.userId"
            placeholder="输入用户ID"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showStartDialog = false">取消</el-button>
        <el-button type="primary" @click="startConversation"
          >开始聊天</el-button
        >
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {
  computed,
  nextTick,
  onBeforeUnmount,
  onMounted,
  reactive,
  ref,
} from "vue";
import { useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/stores/user";
import {
  getChatContacts,
  getChatHistory,
  markChatMessagesAsRead,
  revokeChatMessage,
} from "@/api/chat";
import { getOneUser } from "@/api/user";

const userStore = useUserStore();
const route = useRoute();
const UNREAD_EVENT = "chat-unread-changed";
const CHAT_WS_MESSAGE_EVENT = "chat-ws-message";
const CHAT_WS_STATUS_EVENT = "chat-ws-status";
const CHAT_WS_SEND_EVENT = "chat-ws-send";

const contacts = ref([]);
const activeContact = ref(null);
const messagesMap = ref({});
const draft = ref("");
const wsConnected = ref(false);
const showStartDialog = ref(false);
const startForm = reactive({ userId: null });
const messageScrollRef = ref(null);

const toNumberId = (value) => Number(value || 0);
const toConversationKey = (userId) => String(toNumberId(userId));
const isSelfMessage = (msg) =>
  toNumberId(msg?.sendId) === toNumberId(userStore.userId);

const currentMessages = computed(() => {
  if (!activeContact.value?.userId) return [];
  return messagesMap.value[toConversationKey(activeContact.value.userId)] || [];
});

const formatTime = (value) => {
  if (!value) return "";
  const date = typeof value === "number" ? new Date(value) : new Date(value);
  if (Number.isNaN(date.getTime())) return "";
  return `${date.getMonth() + 1}-${date.getDate()} ${String(date.getHours()).padStart(2, "0")}:${String(
    date.getMinutes(),
  ).padStart(2, "0")}`;
};

const readStatusText = (msg) => {
  if (msg.status === 2) return "已撤回";
  if (msg.status === 1) return "已读";
  return "未读";
};

const canRevoke = (msg) => {
  if (!msg || msg.status === 2) return false;
  const base = msg.sentTime ? new Date(msg.sentTime).getTime() : msg.timestamp;
  if (!base || Number.isNaN(base)) return false;
  return Date.now() - base <= 2 * 60 * 1000;
};

const pushMessage = (msg) => {
  const sendId = toNumberId(msg.sendId);
  const receiveId = toNumberId(msg.receiveId);
  const currentUserId = toNumberId(userStore.userId);
  const peerId = sendId === currentUserId ? receiveId : sendId;
  const key = toConversationKey(peerId);

  if (!messagesMap.value[key]) {
    messagesMap.value[key] = [];
  }
  const list = messagesMap.value[key];
  const idx = list.findIndex((item) => item.id && msg.id && item.id === msg.id);
  if (idx >= 0) {
    list[idx] = { ...list[idx], ...msg };
  } else {
    list.push(msg);
  }

  const targetContact = contacts.value.find(
    (c) => toNumberId(c.userId) === peerId,
  );
  if (targetContact) {
    targetContact.lastContent = msg.status === 2 ? "[消息已撤回]" : msg.content;
    targetContact.lastTime = msg.sentTime || msg.timestamp;
    if (
      receiveId === currentUserId &&
      toNumberId(activeContact.value?.userId) !== peerId
    ) {
      targetContact.unreadCount = (targetContact.unreadCount || 0) + 1;
    }
  }
  sortContacts();
};

const sortContacts = () => {
  contacts.value.sort((a, b) => {
    const ta = new Date(a.lastTime || 0).getTime();
    const tb = new Date(b.lastTime || 0).getTime();
    return tb - ta;
  });
};

const scrollToBottom = async () => {
  await nextTick();
  const wrap = messageScrollRef.value?.wrapRef;
  if (wrap) {
    wrap.scrollTop = wrap.scrollHeight;
  }
};

const fetchContacts = async () => {
  const { data } = await getChatContacts();
  contacts.value = data?.data || data || [];
  sortContacts();
};

const markCurrentRead = async () => {
  const currentUserId = toNumberId(userStore.userId);
  const unreadIds = currentMessages.value
    .filter(
      (item) =>
        toNumberId(item.receiveId) === currentUserId &&
        item.status === 0 &&
        item.id,
    )
    .map((item) => item.id);
  if (!unreadIds.length) return;
  await markChatMessagesAsRead(unreadIds);
  currentMessages.value.forEach((m) => {
    if (unreadIds.includes(m.id)) {
      m.status = 1;
      m.readTime = new Date().toISOString();
    }
  });
  const contact = contacts.value.find(
    (c) => toNumberId(c.userId) === toNumberId(activeContact.value?.userId),
  );
  if (contact) {
    contact.unreadCount = 0;
  }
  window.dispatchEvent(new Event(UNREAD_EVENT));
};

const loadHistory = async (targetUserId) => {
  const { data } = await getChatHistory(targetUserId, 100);
  const list = (data?.data || data || []).slice().reverse();
  messagesMap.value[toConversationKey(targetUserId)] = list;
  await markCurrentRead();
  await scrollToBottom();
};

const selectContact = async (item) => {
  activeContact.value = item;
  const key = toConversationKey(item.userId);
  if (!messagesMap.value[key]) {
    await loadHistory(item.userId);
  } else {
    await markCurrentRead();
    await scrollToBottom();
  }
};

const startConversation = async () => {
  if (!startForm.userId || startForm.userId <= 0) {
    ElMessage.warning("请输入有效用户ID");
    return;
  }
  if (toNumberId(startForm.userId) === toNumberId(userStore.userId)) {
    ElMessage.warning("不能和自己发起会话");
    return;
  }
  try {
    const { data } = await getOneUser({ userId: startForm.userId });
    const user = data?.data || data || {};
    if (!user?.userId) {
      ElMessage.error("用户不存在");
      return;
    }
    let contact = contacts.value.find(
      (c) => toNumberId(c.userId) === toNumberId(user.userId),
    );
    if (!contact) {
      contact = {
        userId: user.userId,
        username: user.username,
        avatarUrl: user.avatarUrl,
        lastContent: "",
        lastTime: null,
        unreadCount: 0,
      };
      contacts.value.unshift(contact);
    }
    activeContact.value = contact;
    await loadHistory(contact.userId);
    showStartDialog.value = false;
    startForm.userId = null;
  } catch (e) {
    ElMessage.error(e?.message || "发起会话失败");
  }
};

const parseTargetUserId = () => {
  const raw = route.query?.targetUserId;
  if (raw == null) return 0;
  const id = Number(Array.isArray(raw) ? raw[0] : raw);
  if (!Number.isFinite(id) || id <= 0) return 0;
  return id;
};

const openConversationByRoute = async () => {
  const targetUserId = parseTargetUserId();
  if (!targetUserId || targetUserId === Number(userStore.userId)) return false;

  let contact = contacts.value.find((c) => Number(c.userId) === targetUserId);
  if (!contact) {
    try {
      const { data } = await getOneUser({ userId: targetUserId });
      const user = data?.data || data || {};
      if (!user?.userId) return false;
      const qName = Array.isArray(route.query?.targetName)
        ? route.query.targetName[0]
        : route.query?.targetName;
      const qAvatar = Array.isArray(route.query?.targetAvatar)
        ? route.query.targetAvatar[0]
        : route.query?.targetAvatar;
      contact = {
        userId: user.userId,
        username: qName || user.username,
        avatarUrl: qAvatar || user.avatarUrl,
        lastContent: "",
        lastTime: null,
        unreadCount: 0,
      };
      contacts.value.unshift(contact);
      sortContacts();
    } catch {
      return false;
    }
  }

  await selectContact(contact);
  return true;
};

const handleGlobalWsStatus = (event) => {
  wsConnected.value = !!event?.detail?.connected;
};

const handleGlobalWsMessage = async (event) => {
  const payload = event?.detail;
  if (!payload) return;

  if (Array.isArray(payload)) {
    payload.forEach((msg) => pushMessage(msg));
  } else {
    pushMessage(payload);
  }

  if (activeContact.value) {
    await markCurrentRead();
  }
  await scrollToBottom();
};

const sendMessage = () => {
  const content = draft.value.trim();
  if (!content) return;
  if (!toNumberId(userStore.userId)) {
    ElMessage.error("用户信息未加载，请重新登录后重试");
    return;
  }
  if (!activeContact.value?.userId) {
    ElMessage.warning("请先选择会话");
    return;
  }
  if (!wsConnected.value) {
    ElMessage.error("连接已断开，请稍后重试");
    return;
  }

  window.dispatchEvent(
    new CustomEvent(CHAT_WS_SEND_EVENT, {
      detail: {
        sendId: toNumberId(userStore.userId),
        receiveId: toNumberId(activeContact.value.userId),
        content,
        timestamp: Date.now(),
        msgType: 0,
      },
    }),
  );
  draft.value = "";
};

const onEnterSend = (e) => {
  if (e.shiftKey) {
    draft.value += "\n";
    return;
  }
  sendMessage();
};

const handleRevoke = async (msg) => {
  if (!msg?.id) {
    ElMessage.warning("该消息暂不支持撤回");
    return;
  }
  try {
    await revokeChatMessage(msg.id);
    msg.status = 2;
    msg.revokeTime = new Date().toISOString();
    const peerId = isSelfMessage(msg)
      ? toNumberId(msg.receiveId)
      : toNumberId(msg.sendId);
    const contact = contacts.value.find((c) => toNumberId(c.userId) === peerId);
    if (contact) {
      contact.lastContent = "[消息已撤回]";
    }
    ElMessage.success("撤回成功");
  } catch (e) {
    ElMessage.error(e?.message || "撤回失败");
  }
};

onMounted(async () => {
  window.addEventListener(CHAT_WS_STATUS_EVENT, handleGlobalWsStatus);
  window.addEventListener(CHAT_WS_MESSAGE_EVENT, handleGlobalWsMessage);
  wsConnected.value = !!window.__chatWsConnected;

  try {
    await fetchContacts();
    const opened = await openConversationByRoute();
    if (!opened && contacts.value.length) {
      await selectContact(contacts.value[0]);
    }
  } catch {
    ElMessage.error("加载会话失败");
  }
});

onBeforeUnmount(() => {
  window.removeEventListener(CHAT_WS_STATUS_EVENT, handleGlobalWsStatus);
  window.removeEventListener(CHAT_WS_MESSAGE_EVENT, handleGlobalWsMessage);
});
</script>

<style lang="scss" scoped>
.chat-page {
  height: calc(100vh - 160px);
}

.chat-shell {
  height: 100%;
}

.chat-layout {
  display: grid;
  grid-template-columns: 320px 1fr;
  height: 100%;
}

.chat-sidebar {
  border-right: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;

  .sidebar-header {
    height: 52px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 12px;
    border-bottom: 1px solid #ebeef5;
    font-weight: 600;
  }

  .contact-scroll {
    flex: 1;
  }

  .contact-item {
    display: flex;
    gap: 10px;
    padding: 10px 12px;
    border-bottom: 1px solid #f5f7fa;
    cursor: pointer;

    &:hover {
      background: #f7fbff;
    }

    &.active {
      background: #ecf5ff;
    }
  }

  .contact-main {
    flex: 1;
    min-width: 0;
  }

  .name-row,
  .meta-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .name {
    font-weight: 600;
    color: #111827;
  }

  .time {
    color: #9ca3af;
    font-size: 12px;
  }

  .preview {
    margin-top: 4px;
    color: #6b7280;
    font-size: 13px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 180px;
  }
}

.chat-main {
  display: flex;
  flex-direction: column;
  height: 100%;

  .chat-header {
    height: 52px;
    border-bottom: 1px solid #ebeef5;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 16px;

    .title {
      font-weight: 600;
      color: #111827;
    }

    .sub {
      color: #10b981;
      font-size: 12px;
    }
  }

  .message-scroll {
    flex: 1;
    padding: 14px 16px;
    background: #f8fafc;
  }

  .message-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  .message-row {
    display: flex;

    &.self {
      justify-content: flex-end;

      .bubble {
        background: #dbeafe;
      }
    }

    &.other {
      justify-content: flex-start;

      .bubble {
        background: #ffffff;
      }
    }
  }

  .bubble-wrap {
    max-width: 72%;
  }

  .bubble {
    padding: 8px 12px;
    border-radius: 12px;
    color: #111827;
    white-space: pre-wrap;
    word-break: break-word;

    &.revoke {
      color: #6b7280;
      font-style: italic;
    }
  }

  .meta {
    margin-top: 4px;
    display: flex;
    align-items: center;
    gap: 8px;
    color: #9ca3af;
    font-size: 12px;
  }

  .chat-input {
    border-top: 1px solid #ebeef5;
    padding: 12px;
    background: #fff;

    .actions {
      margin-top: 10px;
      display: flex;
      justify-content: flex-end;
      gap: 8px;
    }
  }
}

@media (max-width: 900px) {
  .chat-layout {
    grid-template-columns: 1fr;
  }

  .chat-sidebar {
    max-height: 220px;
  }
}
</style>
