// src/services/arquivoService.ts
import api from './api'

// Função para obter o token de autenticação
const getAuthToken = () => {
  return localStorage.getItem('authToken')
}

// Função para importar um arquivo CSV
export const importarCsv = async (file: File) => {
  const formData = new FormData()
  formData.append('file', file)

  try {
    const response = await api.post('/arquivos/import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        Authorization: `Bearer ${getAuthToken()}`,
      },
    })
    return response.data
  } catch (error) {
    console.error('Erro ao importar CSV:', error)
    throw error
  }
}
