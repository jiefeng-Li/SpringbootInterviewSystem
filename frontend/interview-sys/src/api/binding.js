import request from '@/utils/request'

export const bindCompany = (data) => {
  return request({
    method: 'post',
    url: '/user/binding',
    data,
  })
}


export const getOwnBindingInfo = (data) => {
    return request({
        method: 'get',
        url: '/user/binding/own/list',
        params: data
    })
}

export const cancelBindingCompany = (id) => {
    return request({
        method: 'delete',
        url: `/user/binding/cancel/${id}`
    })
}

export const unbindCompany = (id) => {
    return request({
        method: 'put',
        url: `/user/unbind/${id}`
    })
}

export const getBindingList = (data) => {
    return request({
        method: 'get',
        url: '/user/binding/list',
        params: data
    })
}

export const approveBinding = (id, reviewNotes) => {
    return request({
        method: 'put',
        url: `/user/binding/${id}`,
        data: {
            status: 1,
            reviewNotes,
            id,
        }
    })
}

export const rejectBinding = (id, reviewNotes) => {
    return request({
        method: 'put',
        url: `/user/binding/${id}`,
        data: {
            status: 2,
            reviewNotes,
            id,
        }
    })
}