import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    userId: '',
    companyId: '',
    role: ''
  }),
  isloginned: false,
  getters: {
    isAuthenticated: (state) => !!state.token
  },
  actions: {
    setToken(token) {
      this.token = token
    },
    setUser({ userId, companyId, role }) {
      if (userId !== undefined) this.userId = userId
      if (companyId !== undefined) this.companyId = companyId
      if (role !== undefined) this.role = role
    },
    logout() {
      this.token = ''
      this.userId = ''
      this.companyId = ''
      this.role = ''
    }
  }
})
