// src/axios.js
import axios from 'axios'
import store from './store/loadingStore'

const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080', // Substitua pela URL da sua API
  timeout: 10000, // Tempo de espera para as requisições
})

// Interceptor para exibir o loader antes da requisição
axiosInstance.interceptors.request.use(
  (config) => {
    store.dispatch('startLoading') // Inicia o loader
    return config
  },
  (error) => {
    store.dispatch('stopLoading') // Para o loader em caso de erro
    return Promise.reject(error)
  },
)

// Interceptor para ocultar o loader após a resposta
axiosInstance.interceptors.response.use(
  (response) => {
    store.dispatch('stopLoading') // Para o loader após a resposta
    return response
  },
  (error) => {
    store.dispatch('stopLoading') // Para o loader em caso de erro
    return Promise.reject(error)
  },
)

export default axiosInstance
