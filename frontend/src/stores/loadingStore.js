// src/store/loadingStore.js
import { createStore } from 'vuex'

const store = createStore({
  state() {
    return {
      loading: false,
    }
  },
  mutations: {
    setLoading(state, payload) {
      state.loading = payload
    },
  },
  actions: {
    startLoading({ commit }) {
      commit('setLoading', true)
    },
    stopLoading({ commit }) {
      commit('setLoading', false)
    },
  },
})

export default store
