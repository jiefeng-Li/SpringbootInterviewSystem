import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    userId: '',
    username: '',
    role: '',
    email: '',
    phone: '',
    avatarUrl: '',
    profile: '',
    accountStatus: '',
    lastLoginTime: '',
    isDeleted: 0,
    createTime: '',
    updateTime: '',
    editTime: '',
    companyId: '',
    companyName: '',
    isloginned: false,
  }),
  
  getters: {
    isAuthenticated: (state) => !!state.token,
  },
  actions: {
    setLoginStatus(status) {
      this.isloginned = status
    },
    setToken(token) {
      this.token = token
    },
    setUser(data) {
      this.userId = data.userId || '';
      this.username = data.username || '';
      this.role = data.role || '';
      this.email = data.email || '';
      this.phone = data.phone || '';
      this.avatarUrl = data.avatarUrl || '';
      this.profile = data.profile || '';
      this.accountStatus = data.accountStatus || '';
      this.lastLoginTime = data.lastLoginTime || '';
      this.isDeleted = data.isDeleted || 0;
      this.createTime = data.createTime || '';
      this.updateTime = data.updateTime || '';
      this.editTime = data.editTime || '';
      this.companyId = data.companyId || '';
      this.companyName = data.companyName || '';
    },
    logout() {
      this.token = ''
      this.userId = ''
      this.username = ''
      this.role = ''
      this.email = ''
      this.phone = ''
      this.avatarUrl = ''
      this.profile = ''
      this.accountStatus = ''
      this.lastLoginTime = ''
      this.isDeleted = 0
      this.createTime = ''
      this.updateTime = ''
      this.editTime = ''
      this.companyId = ''
      this.companyName = ''
      this.isloginned = false
    },

  }
})
