// src/services/artigoService.ts
import api from './api'

// Função para obter o token de autenticação
const getAuthToken = () => {
  return localStorage.getItem('authToken')
}

// Buscar todos os artigos
export const buscarArtigos = async () => {
  try {
    const response = await api.get('/artigos', {})
    return response.data
  } catch (error) {
    console.error('Erro ao buscar artigos:', error)
    throw error
  }
}
