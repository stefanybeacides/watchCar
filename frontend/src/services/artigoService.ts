// src/services/artigoService.ts
import api from './api'

// Função para obter o token de autenticação
const getAuthToken = () => {
  return localStorage.getItem('authToken')
}

// Buscar todos os artigos
export const buscarArtigos = async () => {
  const token = getAuthToken()

  if (!token) {
    throw new Error('Token de autenticação não encontrado')
  }

  try {
    const response = await api.get('/artigos', {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
    return response.data
  } catch (error) {
    console.error('Erro ao buscar artigos:', error)
    throw error
  }
}
