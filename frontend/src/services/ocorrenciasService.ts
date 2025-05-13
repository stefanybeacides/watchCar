// src/services/ocorrenciasService.ts
import api from './api'

const getAuthToken = () => localStorage.getItem('authToken')

// 🔹 Criar nova denúncia
export const enviarDenuncia = async (denuncia: any) => {
  try {
    const response = await api.post('/ocorrencias/criar', denuncia, {})
    return response.data
  } catch (error) {
    console.error('Erro ao enviar denúncia:', error)
    throw error
  }
}

export const obterTodasOcorrencias = async () => {
  const token = getAuthToken()
  if (!token) throw new Error('Token de autenticação não encontrado')

  try {
    // Aqui vamos forçar um size grande, ou criar uma rota sem paginação se for o caso
    const url = `/ocorrencias/listar?page=0&size=10000`
    const response = await api.get(url, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
    return response.data.content // só os dados
  } catch (error) {
    console.error('Erro ao buscar todas as ocorrências:', error)
    throw error
  }
}

// 🔹 Obter ocorrências com filtros e paginação
export const obterOcorrencias = async (
  filters: {
    status: string
    artigo: string
    hora: string
    dataInicio: string
    dataFim: string
    usuarioNome: string
    usuarioEmail: string
    veiculoMarca: string
    veiculoModelo: string
    veiculoPlaca: string
  },
  page: number = 0,
  size: number = 10,
) => {
  const token = getAuthToken()
  if (!token) throw new Error('Token de autenticação não encontrado')

  try {
    const params = new URLSearchParams({
      page: String(page),
      size: String(size),
      status: filters.status || '',
      artigo: filters.artigo || '',
      hora: filters.hora || '',
      dataInicio: filters.dataInicio || '',
      dataFim: filters.dataFim || '',
      usuarioNome: filters.usuarioNome || '',
      usuarioEmail: filters.usuarioEmail || '',
      veiculoMarca: filters.veiculoMarca || '',
      veiculoModelo: filters.veiculoModelo || '',
      veiculoPlaca: filters.veiculoPlaca || '',
    })

    const url = `/ocorrencias/listar?${params.toString()}`
    const response = await api.get(url, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
    return response.data
  } catch (error) {
    console.error('Erro ao buscar ocorrências:', error)
    throw error
  }
}

// 🔹 Contar ocorrências
export const contarOcorrencias = async (filters: {
  status: string
  artigo: string
  hora: string
  dataInicio: string
  dataFim: string
}) => {
  const token = getAuthToken()
  if (!token) throw new Error('Token de autenticação não encontrado')

  try {
    const url = `/ocorrencias/listar/ocorrencias/count?status=${filters.status}&artigo=${filters.artigo}&hora=${filters.hora}&dataInicio=${filters.dataInicio}&dataFim=${filters.dataFim}`
    const response = await api.get(url, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
    return response.data
  } catch (error) {
    console.error('Erro ao contar ocorrências:', error)
    throw error
  }
}

export const contarOcorrenciasPorStatus = async () => {
  const token = getAuthToken()
  const response = await api.get('/ocorrencias/contagem-por-status', {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })
  return response.data
}

// 🔹 Obter ocorrência por ID
export const obterOcorrenciaPorId = async (id: number) => {
  const token = getAuthToken()
  if (!token) throw new Error('Token de autenticação não encontrado')

  try {
    const response = await api.get(`/ocorrencias/detalhar/${id}`, {
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

// 🔹 Verificar responsável
export const verificarResponsavel = async (ocorrenciaId: number, usuarioId: string) => {
  const token = getAuthToken()
  if (!token) throw new Error('Token de autenticação não encontrado')

  try {
    const response = await api.get(`/ocorrencias/${ocorrenciaId}/responsavel`, {
      params: { usuarioId },
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
    return response.data.responsavel
  } catch (error) {
    console.error('Erro ao verificar responsável:', error)
    throw error
  }
}

// 🔹 Assumir responsabilidade
export const assumirResponsavel = async (ocorrenciaId: number, usuarioId: string) => {
  const token = getAuthToken()
  if (!token) throw new Error('Token de autenticação não encontrado')

  try {
    const response = await api.put(
      `/ocorrencias/${ocorrenciaId}/assumir`,
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

// 🔹 Desassumir responsabilidade
export const desassumirResponsavel = async (ocorrenciaId: number, usuarioId: string) => {
  const token = getAuthToken()
  if (!token) throw new Error('Token de autenticação não encontrado')

  try {
    const response = await api.put(
      `/ocorrencias/${ocorrenciaId}/desassumir`,
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

// 🔹 Criar ação de investigação
export const enviarAcaoInvestigacao = async (acao: {
  idDenuncia: number
  tipoAcao: string
  descricaoAcao: string
  dataAcao: string
  idResponsavel: number
}) => {
  const token = getAuthToken()

  if (!token) {
    throw new Error('Token de autenticação não encontrado')
  }

  try {
    const response = await api.post('/ocorrencias/acao-investigacao', acao, {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    })

    return response.data
  } catch (error: any) {
    console.error('Erro ao enviar ação de investigação:', error?.response || error)
    throw error?.response?.data || new Error('Erro inesperado ao enviar ação.')
  }
}

// 🔹 Listar ações de investigação
export const listarAcoesInvestigacao = async (responsavelId: number, denunciaId: number) => {
  const token = getAuthToken()
  if (!token) throw new Error('Token de autenticação não encontrado')

  try {
    const response = await api.get('/ocorrencias/acoes-investigacao', {
      params: {
        responsavel: responsavelId,
        denuncia: denunciaId,
      },
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
    return response.data
  } catch (error) {
    console.error('Erro ao listar ações de investigação:', error)
    throw error
  }
}
