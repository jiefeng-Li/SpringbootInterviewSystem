import request from '@/utils/request'

export const addJobApplication = (data) => {
    return request({
        url: '/jobApplication',
        method: 'post',
        data
    })
}

export const getOwnJobApplicationList = (data) => {
    return request({
        url: '/jobApplication/list/own',
        method: 'get',
        params: data
    })
}

export const getJobApplicationList = (data) => {
    return request({
        url: '/jobApplication/list',
        method: 'get',
        params: data
    })
}

export const reviewJobApplication = (data) => {
    return request({
        url: '/jobApplication/review',
        method: 'post',
        data
    })
}

export const reviewJobApplicationBatch = (data) => {
    return request({
        url: '/jobApplication/review/batch',
        method: 'post',
        data
    })
}