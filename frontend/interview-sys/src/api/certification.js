import request from '@/utils/request'

export const getCompanyCertificationList = (params) => {
    return request({
        method: 'get',
        url: '/certification/list',
        params
    })
}

export const addCompanyCertification = (data) => {
    return request({
        method: 'post',
        url: '/certification',
        data
    })
}

export const getCompanyCertificationStatus = () => {
    return request({
        method: 'get',
        url: '/certification/status'
    })
}

export const reviewCompanyCertification = (id, data) => {
    return request({
        method: 'put',
        url: `/certification/${id}`,
        data
    })
}