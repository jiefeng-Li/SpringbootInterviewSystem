import request from '@/utils/request'

export const sendOffer = (data) => {
	return request({
		url: '/offerRecord/send',
		method: 'post',
		data,
	})
}

export const updateOfferStatus = (data) => {
	return request({
		url: '/offerRecord/status',
		method: 'post',
		data,
	})
}

export const getOfferListByUser = (params) => {
	return request({
		url: '/offerRecord/list/by-user',
		method: 'get',
		params,
	})
}

export const getSentOfferList = (params) => {
	return request({
		url: '/offerRecord/list/sent',
		method: 'get',
		params,
	})
}

export const getOfferById = (id) => {
	return request({
		url: '/offerRecord',
		method: 'get',
		params: { id },
	})
}

