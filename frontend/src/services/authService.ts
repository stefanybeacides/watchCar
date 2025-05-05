// src/services/authService.ts
import api from './api'
const getAuthToken = () => {
  return localStorage.getItem('authToken')
}
export const login = async (cpf: string, password: string) => {
  const response = await api.post('/login', { cpf, password })
  return response.data
}

export const register = async (
  username: string,
  password: string,
  email: string,
  cpf: string, // Adicionando cpf
  tipo: number, // Adicionando tipo
) => {
  const response = await api.post('/register', {
    username,
    password,
    email,
    cpf, // Passando cpf
    tipo, // Passando tipo
  })
  return response.data
}
export const fetchUserData = async () => {
  const token = getAuthToken()

  if (token) {
    try {
      const response = await api.get('/user', {
        headers: {
          Authorization: 'Bearer ' + token, // Adicionando o token ao cabeçalho
        },
      })
      return response.data
    } catch (error) {
      console.error('Erro ao buscar dados do usuário:', error)
      throw error
    }
  } else {
    throw new Error('Token de autenticação não encontrado.')
  }
}
