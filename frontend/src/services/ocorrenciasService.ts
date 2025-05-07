// src/services/ocorrenciasService.ts
import api from './api'

// Função para obter o token de autenticação
const getAuthToken = () => {
  return localStorage.getItem('authToken')
}

export const enviarDenuncia = async (denuncia: any) => {
  const token = getAuthToken()

  if (!token) {
    throw new Error('Token de autenticação não encontrado')
  }

  try {
    const response = await api.post('/criar', denuncia, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })

    return response.data
  } catch (error) {
    console.error('Erro ao enviar denúncia:', error)
    throw error
  }
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
    const url = `/listar/ocorrencias/count?status=${filters.status}&artigo=${filters.artigo}&hora=${filters.hora}&dataInicio=${filters.dataInicio}&dataFim=${filters.dataFim}`

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

// Função para buscar uma ocorrência detalhada por ID
export const obterOcorrenciaPorId = async (id: number) => {
  const token = getAuthToken()

  if (!token) {
    throw new Error('Token de autenticação não encontrado')
  }

  try {
    const response = await api.get(`/ocorrencias/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })

    return response.data
  } catch (error) {
    console.error('Erro ao buscar ocorrência:', error)
    throw error
  }
}
// Função para verificar se o usuário é o responsável da ocorrência
export const verificarResponsavel = async (ocorrenciaId: number, usuarioId: string) => {
  const token = getAuthToken()

  if (!token) {
    throw new Error('Token de autenticação não encontrado')
  }

  try {
    // Ajusta a URL conforme a sua solicitação
    const response = await api.get(`http://localhost:8080/api/${ocorrenciaId}/responsavel`, {
      params: {
        usuarioId: usuarioId,
      },
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })

    return response.data.responsavel // Retorna os dados da resposta, ajustando conforme necessário
  } catch (error) {
    console.error('Erro ao verificar responsável:', error)
    throw error
  }
}

// Função para assumir a responsabilidade pela ocorrência
export const assumirResponsavel = async (ocorrenciaId: number, usuarioId: string) => {
  const token = getAuthToken()

  if (!token) {
    throw new Error('Token de autenticação não encontrado')
  }

  try {
    const response = await api.put(
      `/${ocorrenciaId}/assumir`,
      { usuarioId },
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      },
    )

    return response.data
  } catch (error) {
    console.error('Erro ao assumir responsabilidade:', error)
    throw error
  }
}

// Função para desassumir a responsabilidade pela ocorrência
export const desassumirResponsavel = async (ocorrenciaId: number, usuarioId: string) => {
  const token = getAuthToken()

  if (!token) {
    throw new Error('Token de autenticação não encontrado')
  }

  try {
    const response = await api.put(
      `/${ocorrenciaId}/desassumir`,
      { usuarioId },
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      },
    )

    return response.data
  } catch (error) {
    console.error('Erro ao desassumir responsabilidade:', error)
    throw error
  }
}
