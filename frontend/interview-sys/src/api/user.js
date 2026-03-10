import request from '@/utils/request'

export const userLogin = (account, password) => {
  return request({
    method: 'get',
    url: '/user/login',
    params: { account, password }
  })
}

export const getCurrentUserInfo = () => {
  return request({
    method: 'get',
    url: '/user/current'
  })
}

export const commonUserRegister = (data) => {
  return request({
    method: 'post',
    url: '/user/register',
    data
  })
}

export const compAdminRegister = (data) => {
  return request({
    method: 'post',
    url: '/user/comp/register',
    data
  })
}

export const sysAdminRegister = (data) => {
  return request({
    method: 'post',
    url: '/user/admin/register',
    data
  })
}

export const getOneUser = (data) => {
  return request({
    method: 'get',
    url: '/user',
    params: data
  })
}

export const updateOneUserInfo = (id, data) => {
  return request({
    method: 'put',
    url: `/user/${id}`,
    data
  })
}

export const deleteOneUser = (id) => {
  return request({
    method: 'delete',
    url: `/user/${id}`
  })
}