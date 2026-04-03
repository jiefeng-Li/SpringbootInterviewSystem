import request from "@/utils/request";

export const getChatContacts = () => {
  return request({
    method: "get",
    url: "/msg/contacts",
  });
};

export const getChatHistory = (targetUserId, limit = 100) => {
  return request({
    method: "get",
    url: `/msg/history/${targetUserId}`,
    params: { limit },
  });
};

export const markChatMessagesAsRead = (messageIds) => {
  return request({
    method: "post",
    url: "/msg",
    data: messageIds,
  });
};

export const revokeChatMessage = (id) => {
  return request({
    method: "post",
    url: `/msg/revoke/${id}`,
  });
};

export const pushSystemMessage = (data) => {
  return request({
    method: "post",
    url: "/msg/system/push",
    data,
  });
};
