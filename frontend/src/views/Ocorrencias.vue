<template lang="pug">
  .ocorrencias-container
    h1.titulo Ocorrências Registradas

    //- Filtros e botão
    .filters
      .inputs
      .filters.inputs
        select(v-model="filters.status")
          option(value="") Selecione o Status
          option(value="Em andamento") Em Andamento
          option(value="Solucionado") Solucionado
          option(value="Arquivado") Arquivado
        select(v-model="filters.artigo" id="artigo" name="artigo")
          option(value="") Selecione o Artigo
          option(v-for="artigo in artigos" :key="artigo.id" :value="artigo.id")
            | {{ artigo.codArtigo }} - {{ artigo.descricao }}
        input(type="text" v-model="filters.hora" placeholder="Hora")
        input(type="datetime-local" v-model="filters.dataInicio" placeholder="Data Início")
        input(type="datetime-local" v-model="filters.dataFim" placeholder="Data Fim")
        button(@click="fetchOcorrencias").btn Buscar

    //- Tabela de Ocorrências
    .table-wrapper
      table.ocorrencias-table
        thead
          tr
            th Usuário
            th E-mail
            th Placa
            th Modelo
            th Marca
            th Local
            th Status
            th Hora da Ocorrência
            th Data Registro da Ocorrência
            th Descrição
            th Artigo
            th(v-if="perfilUsuario !== 'PUBLICO'") Ações
        tbody
          tr(v-if="ocorrencias.length === 0")
            td(colspan="10" style="text-align: center;") Sem registros
          tr(v-for="(ocorrencia, index) in ocorrencias" :key="index")
            td {{ ocorrencia.usuarioNome }}
            td {{ ocorrencia.usuarioEmail }}
            td {{ ocorrencia.veiculoPlaca }}
            td {{ ocorrencia.veiculoModelo }}
            td {{ ocorrencia.veiculoMarca }}
            td
              button.btn-local(@click="abrirModalLocal(ocorrencia)")
                span 📍 Ver Local
            td {{ ocorrencia.statusDenuncia }}
            td {{ ocorrencia.horaOcorrencia }}
            td {{ formatDataHora(ocorrencia.dataHora) }}
            td {{ ocorrencia.descricaoOcorrencia }}
            td {{ ocorrencia.artigoCodigo }} - {{ ocorrencia.artigoDescricao }}
            td(v-if="perfilUsuario !== 'PUBLICO'" class="acoes-dropdown")
              .dropdown
                button(
                  @click.stop="toggleMenu(index)"
                  :class="{ 'active': menuAbertoIndex === index }"
                  class="menu-button"
                )
                  span 
                    i.fa-solid.fa-plus
                .dropdown-menu(v-if="menuAbertoIndex === index")
                  button.btn-sm(@click="abrirModalDetalhes(ocorrencia)")
                    EyeOutlined
                    | Ver Detalhes

                  button.btn-sm(@click="abrirModalEditar(ocorrencia)")
                    PlusOutlined
                    | Adicionar Detalhes

                  button.btn-sm(
                    v-if="(perfilUsuario === 'POLICIAL' || perfilUsuario === 'AGENTE_DE_SEGURANCA' || perfilUsuario === 'INVESTIGADOR') && !getResponsabilidade(ocorrencia.id)"
                    @click="abrirModalResponsavel(ocorrencia)"
                  )
                    EditOutlined
                    | Responsavel






      //- Paginação à direita
      .pagination
        button(@click="changePage(currentPage - 1)" :disabled="currentPage === 0").pagination-btn Página Anterior
        span Página {{ currentPage + 1 }} de {{ totalPages }}
        button(@click="changePage(currentPage + 1)" :disabled="currentPage + 1 >= totalPages").pagination-btn Próxima Página


      ModalDetalhes(
        v-if="modalDetalhesAberto"
        :ocorrencia="ocorrenciaSelecionada"
        :acoesInvestigacao="ocorrenciaSelecionada.acoesInvestigacao"
        @close="modalDetalhesAberto = false"
        @salvo="fetchOcorrencias"
      )

      ModalEditar(
        v-if="modalEditarAberto"
        :ocorrencia="ocorrenciaSelecionada"
        @close="modalEditarAberto = false"
        @salvo="fetchOcorrencias"
      )
      ModalResponsavel(
        v-if="modalResponsavelAberto"
        :ocorrencia="ocorrenciaSelecionada"
        :userId="userId"
        :isResponsavel="responsabilidadeParaAlterar"
        @close="modalResponsavelAberto = false"
        @salvo="fetchOcorrencias"
      )

      ModalLocal(
        v-if="modalLocalAberto"
        :ocorrencia="ocorrenciaSelecionada"
        @close="modalLocalAberto = false"
      )


</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { obterOcorrencias } from '@/services/ocorrenciasService'
import { buscarArtigos } from '@/services/artigoService'
import ModalDetalhes from '@/views/components/ModalDetalhes.vue'
import ModalEditar from '@/views/components/ModalEditar.vue'
import ModalResponsavel from '@/views/components/ModalResponsavel.vue'
import ModalLocal from '@/views/components/ModalLocal.vue'

import {
  verificarResponsavel,
  assumirResponsavel,
  desassumirResponsavel,
} from '@/services/ocorrenciasService'

import { EyeOutlined, PlusOutlined, EditOutlined } from '@ant-design/icons-vue'
import { useLoadingStore } from '@/stores/loadingStore'
const store = useLoadingStore()
const ocorrencias = ref<any[]>([])
const artigos = ref<any[]>([]) // Para armazenar os artigos
const filters = ref({
  status: '',
  artigo: '', // Aqui vai o ID do artigo
  hora: '',
  dataInicio: '',
  dataFim: '',
})
const currentPage = ref(0)
const totalPages = ref(1)
const pageSize = ref(10)
const token = localStorage.getItem('authToken')
const perfilUsuario = ref(localStorage.getItem('userPerfil') || 'PUBLICO')
const modalDetalhesAberto = ref(false)
const modalEditarAberto = ref(false)
const ocorrenciaSelecionada = ref(null)
const modalResponsavelAberto = ref(false)
const userId = localStorage.getItem('userId') || ''
const responsabilidades = ref<any[]>([]) // Agora é um array para armazenar cada estado
const responsabilidadeParaAlterar = ref(false) // Inicialmente, o usuário não é responsável.
const modalLocalAberto = ref(false)
const ocorrenciaLocalSelecionada = ref(null)

const abrirModalResponsavel = async (ocorrencia: any) => {
  ocorrenciaSelecionada.value = ocorrencia
  modalResponsavelAberto.value = true

  // Verifica se o usuário é responsável
  const isResponsavel = await verificarResponsavel(ocorrencia.id, userId)

  // Se o usuário não for responsável, o botão mostrará "Assumir"
  responsabilidadeParaAlterar.value = isResponsavel
}

const abrirModalDetalhes = (ocorrencia: any) => {
  ocorrenciaSelecionada.value = ocorrencia
  modalDetalhesAberto.value = true
}

const abrirModalEditar = (ocorrencia: any) => {
  ocorrenciaSelecionada.value = ocorrencia
  modalEditarAberto.value = true
}
const menuAbertoIndex = ref<number | null>(null)

const toggleMenu = (index: number) => {
  menuAbertoIndex.value = menuAbertoIndex.value === index ? null : index
}

const abrirModalLocal = (ocorrencia: any) => {
  ocorrenciaSelecionada.value = ocorrencia
  console.log('ocoo ', ocorrenciaSelecionada)
  modalLocalAberto.value = true
}
// Fecha menu ao clicar fora
document.addEventListener('click', (event) => {
  const target = event.target as HTMLElement
  if (!target.closest('.dropdown')) {
    menuAbertoIndex.value = null
  }
})
if (!token) {
  localStorage.setItem('loginMessage', 'Faça login.')
  window.location.href = '/login'
}

const formatDataHora = (dataHora: any) => {
  const date = new Date(dataHora)
  return date.toLocaleString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  })
}

const fetchOcorrencias = async () => {
  try {
    store.startLoading() // Inicia o loading
    const data = await obterOcorrencias(filters.value, currentPage.value, pageSize.value)
    ocorrencias.value = data.content
    totalPages.value = data.totalPages
    store.stopLoading() // Para o loading quando a ação terminar
  } catch (error) {
    store.stopLoading() // Para o loading quando a ação terminar
    console.error('Erro ao carregar as ocorrências:', error)
  }
}
const getResponsabilidade = (ocorrenciaId: string) => {
  const responsabilidade = responsabilidades.value.find((res) => res.id === ocorrenciaId)
  return responsabilidade ? responsabilidade.responsavel : false
}

const verDetalhes = (ocorrencia: any) => {
  console.log('Visualizar:', ocorrencia)
  // Navegar para tela de detalhes ou abrir modal
}

const editarOcorrencia = (ocorrencia: any) => {
  console.log('Editar:', ocorrencia)
  // Redirecionar para a tela de edição com os dados
}
const fetchArtigos = async () => {
  try {
    const data = await buscarArtigos() // Supondo que você tenha esse serviço para pegar os artigos
    artigos.value = data // Armazena os artigos retornados da API
  } catch (error) {
    console.error('Erro ao carregar os artigos:', error)
  }
}

const changePage = (page: number) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
    fetchOcorrencias()
  }
}

// Chama as funções quando o componente for montado
onMounted(() => {
  fetchOcorrencias()
  fetchArtigos() // Chama para buscar os artigos
})
</script>

<style scoped>
.ocorrencias-container {
  display: block;
  padding: 2rem;
}

.titulo {
  text-align: center;
  margin-bottom: 1rem;
}

.filters {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.filters .inputs {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 1rem;
}

.filters input {
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  width: 180px;
}

.filters .btn {
  background-color: #218838;
  color: white;
  border: none;
  padding: 0.4rem 0.8rem;
  border-radius: 6px;
  cursor: pointer;
  margin-left: 1rem;
  font-size: 0.875rem;
  transition: background-color 0.3s;
  line-height: 1; /* Ajuste no line-height para controlar a altura */
}

.filters .btn:hover {
  background-color: #218838;
}

.btn:disabled {
  background-color: #b5dab5;
  cursor: not-allowed;
}

.ocorrencias-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.875rem;
  margin-top: 1rem;
}

.ocorrencias-table th,
.ocorrencias-table td {
  padding: 0.5rem;
  border: 1px solid #ddd;
  text-align: left;
}

.ocorrencias-table th {
  background-color: #f7f7f7;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1rem;
  align-items: center;
}

.pagination-btn {
  background-color: #218838;
  color: white;
  border: none;
  padding: 0.4rem 0.8rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.875rem;
  transition: background-color 0.3s;
}

.pagination-btn:hover {
  background-color: #218838;
}

.pagination-btn:disabled {
  background-color: #b0e0b8;
  cursor: not-allowed;
}

.filters select {
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  width: 200px;
  font-size: 0.875rem;
}

.btn-sm {
  padding: 0.3rem 0.6rem;
  font-size: 0.75rem;
  margin-right: 0.5rem;
  background-color: #218838;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-sm:hover {
  background-color: #218838;
}

.acoes-dropdown {
  position: relative;
}

.dropdown-menu {
  position: absolute;
  right: 0;
  top: 100%;
  background-color: #fff;
  border: 1px solid #ccc;
  border-radius: 6px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  padding: 0.5rem 0;
  display: flex;
  flex-direction: column;
  z-index: 1000;
  min-width: 140px;
}

.dropdown-menu button {
  background: none;
  border: none;
  padding: 0.5rem 1rem;
  text-align: left;
  cursor: pointer;
  font-size: 0.875rem;
  color: #333;
  transition: background-color 0.2s ease-in-out;
  min-width: 180px;
}

.dropdown-menu button:hover {
  background-color: #218838;
  color: white;
}
.menu-button {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: transparent;
  border: none;
  padding: 0.2rem;
  width: 26px;
  height: 26px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
  color: #218838;
}

.menu-button span {
  line-height: 0.6;
  font-size: 1rem;
  color: #218838;
}

.menu-button:hover,
.menu-button.active {
  background-color: #218838;
}

.menu-button:hover span,
.menu-button.active span {
  color: white;
}

.dropdown-menu {
  position: absolute;
  right: 0;
  top: 100%;
  background-color: #fff;
  border: 1px solid #ccc;
  border-radius: 6px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  padding: 0.25rem 0;
  display: flex;
  flex-direction: column;
  z-index: 1000;
  min-width: 160px;
}
.btn-sm {
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-local {
  background-color: white;
  color: #218838;
  border: 1px solid #218838;
  border-radius: 4px;
  font-size: 0.8rem; /* tamanho do texto */
  min-width: 100px; /* largura mínima */
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  text-align: center;
  display: flex; /* Define a exibição como flex */
  align-items: center; /* Alinha o ícone e o texto no centro vertical */
  gap: 0rem; /* Adiciona um pequeno espaço entre o ícone e o texto */
  padding: 0.3rem -0rem; /* Aumenta a altura ajustando o padding vertical (topo e fundo) */
}

.btn-local:hover,
.btn-local:focus {
  background-color: #218838;
  color: white;
  border-color: #218838; /* Garante que a borda também fica verde no hover */
}
</style>
