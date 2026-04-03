import request from "@/utils/request";

export const getSysOverview = () => {
  return request({
    method: "get",
    url: "/statistics/sys/overview",
  });
};

export const getCompanyOverview = () => {
  return request({
    method: "get",
    url: "/statistics/company/overview",
  });
};
