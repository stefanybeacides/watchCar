// src/services/ocorrenciasService.ts
import api from './api'

// Função para obter o token de autenticação
const getAuthToken = () => {
  return localStorage.getItem('authToken')
}

// Função para obter as ocorrências com filtros e paginação
export const obterOcorrencias = async (
  filters: { status: string; artigo: string; hora: string; dataInicio: string; dataFim: string },
  page: number = 0,
  size: number = 10,
) => {
  const token = getAuthToken()

  if (!token) {
    throw new Error('Token de autenticação não encontrado')
  }

  try {
    const url = `/listar/ocorrencias?page=${page}&size=${size}&status=${filters.status}&artigo=${filters.artigo}&hora=${filters.hora}&dataInicio=${filters.dataInicio}&dataFim=${filters.dataFim}`

    const response = await api.get(url, {
      headers: {
        Authorization: `Bearer ${token}`, // Adicionando o token ao cabeçalho
      },
    })

    return response.data // Retorna as ocorrências e dados de paginação
  } catch (error) {
    console.error('Erro ao buscar ocorrências:', error)
    throw error
  }
}

// Função para buscar a quantidade total de ocorrências (caso precise de contagem)
export const contarOcorrencias = async (filters: {
  status: string
  artigo: string
  hora: string
  dataInicio: string
  dataFim: string
}) => {
  const token = getAuthToken()

  if (!token) {
    throw new Error('Token de autenticação não encontrado')
  }

  try {
    const url = `/api/listar/ocorrencias/count?status=${filters.status}&artigo=${filters.artigo}&hora=${filters.hora}&dataInicio=${filters.dataInicio}&dataFim=${filters.dataFim}`

    const response = await api.get(url, {
      headers: {
        Authorization: `Bearer ${token}`, // Adicionando o token ao cabeçalho
      },
    })

    return response.data // Retorna o total de ocorrências
  } catch (error) {
    console.error('Erro ao contar ocorrências:', error)
    throw error
  }
}
