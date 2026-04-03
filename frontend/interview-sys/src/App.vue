<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from "vue";
import { RouterView } from "vue-router";
import { ElNotification } from "element-plus";
import { useUserStore } from "@/stores/user";
import { baseURL } from "@/utils/request";

const CHAT_WS_MESSAGE_EVENT = "chat-ws-message";
const CHAT_WS_STATUS_EVENT = "chat-ws-status";
const CHAT_WS_SEND_EVENT = "chat-ws-send";

const userStore = useUserStore();
const wsRef = ref(null);
const reconnectTimer = ref(null);

const wsUrl = computed(() => {
  const wsBase = baseURL.replace(/^http/, "ws");
  return `${wsBase}/chat?token=${encodeURIComponent(userStore.token || "")}`;
});

const showIncomingMessageNotification = (payload) => {
  if (!payload || Array.isArray(payload)) return;

  const currentUserId = Number(userStore.userId || 0);
  const receiveId = Number(payload.receiveId || 0);
  const sendId = Number(payload.sendId || 0);
  const status = Number(payload.status || 0);
  const msgType = Number(payload.msgType || 0);

  if (!currentUserId || receiveId !== currentUserId) return;
  if (sendId === currentUserId) return;
  if (status !== 0) return;

  const rawContent = String(payload.content || "").trim();
  const content =
    rawContent.length > 32 ? `${rawContent.slice(0, 32)}...` : rawContent;

  ElNotification({
    title: msgType === 1 ? "系统通知" : "收到新消息",
    message: content || "你有一条新消息",
    type: "info",
    duration: 3000,
    position: "top-right",
  });
};

const emitStatus = (connected) => {
  const status = !!connected;
  window.__chatWsConnected = status;
  window.dispatchEvent(
    new CustomEvent(CHAT_WS_STATUS_EVENT, { detail: { connected: status } }),
  );
};

const clearReconnectTimer = () => {
  if (reconnectTimer.value) {
    clearTimeout(reconnectTimer.value);
    reconnectTimer.value = null;
  }
};

const closeWebSocket = () => {
  clearReconnectTimer();
  if (wsRef.value) {
    wsRef.value.close();
    wsRef.value = null;
  }
  emitStatus(false);
};

const connectWebSocket = () => {
  if (!userStore.token) return;
  if (
    wsRef.value &&
    (wsRef.value.readyState === WebSocket.OPEN ||
      wsRef.value.readyState === WebSocket.CONNECTING)
  ) {
    return;
  }

  const ws = new WebSocket(wsUrl.value);
  wsRef.value = ws;

  ws.onopen = () => {
    emitStatus(true);
  };

  ws.onmessage = (event) => {
    try {
      const payload = JSON.parse(event.data);
      showIncomingMessageNotification(payload);
      window.dispatchEvent(
        new CustomEvent(CHAT_WS_MESSAGE_EVENT, { detail: payload }),
      );
    } catch {
      // 忽略无效消息
    }
  };

  ws.onclose = () => {
    emitStatus(false);
    wsRef.value = null;
    clearReconnectTimer();
    if (userStore.token) {
      reconnectTimer.value = setTimeout(() => connectWebSocket(), 2500);
    }
  };

  ws.onerror = () => {
    emitStatus(false);
  };
};

const handleSendMessage = (event) => {
  const payload = event?.detail;
  if (!payload || !wsRef.value || wsRef.value.readyState !== WebSocket.OPEN) {
    return;
  }
  wsRef.value.send(JSON.stringify(payload));
};

watch(
  () => userStore.token,
  (token) => {
    if (token) {
      connectWebSocket();
      return;
    }
    closeWebSocket();
  },
  { immediate: true },
);

onMounted(() => {
  window.addEventListener(CHAT_WS_SEND_EVENT, handleSendMessage);
  if (userStore.token) {
    connectWebSocket();
  }
});

onBeforeUnmount(() => {
  window.removeEventListener(CHAT_WS_SEND_EVENT, handleSendMessage);
  closeWebSocket();
});
</script>

<template>
  <router-view />
</template>

<style scoped></style>
