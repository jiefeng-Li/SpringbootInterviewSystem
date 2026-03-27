import request from '@/utils/request'

export const getInterviewById = (id) => {
    return request({
      url: '/interviewNotice',
      method: 'get',
      params: {
          noticeId: id
      }
    })
}

export const getInterviewNoticeList = (data) => {
    return request({
      url: '/interviewNotice/list',
      method: 'get',
      params: {
        ...data
      }
    })
}

export const addInterviewNotice = (data) => {
    return request({
      url: '/interviewNotice/add',
      method: 'post',
      data
    })
}