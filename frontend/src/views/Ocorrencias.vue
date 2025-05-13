<template lang="pug">
  .ocorrencias-container
    h1.titulo Ocorrências Registradas
    .dashboard-cards(v-if="perfilUsuario !== 'PUBLICO'")
      .card(
        status="Todos"
        :class="{ ativo: statusSelecionado === 'Todos', inativo: statusSelecionado !== 'Todos' }"
        @click="filtrarPorStatus('Todos')"
      )
        h2 Todos
        p.count {{ contagemStatus['Todos'] || 0 }}

      .card(
        status="Em andamento"
        :class="{ ativo: statusSelecionado === 'Em andamento', inativo: statusSelecionado !== 'Em andamento' }"
        @click="filtrarPorStatus('Em andamento')"
      )
        h2 Em Andamento
        p.count {{ contagemStatus['Em andamento'] || 0 }}

      .card(
        status="Solucionado"
        :class="{ ativo: statusSelecionado === 'Solucionado', inativo: statusSelecionado !== 'Solucionado' }"
        @click="filtrarPorStatus('Solucionado')"
      )
        h2 Solucionado
        p.count {{ contagemStatus['Solucionado'] || 0 }}

      .card(
        status="Arquivado"
        :class="{ ativo: statusSelecionado === 'Arquivado', inativo: statusSelecionado !== 'Arquivado' }"
        @click="filtrarPorStatus('Arquivado')"
      )
        h2 Arquivado
        p.count {{ contagemStatus['Arquivado'] || 0 }}




    //- Filtros e botão
    .filters
      .inputs
      .filters.inputs
        button.btn-import(
        v-if="perfilUsuario === 'GESTOR_DE_SEGURANCA_PUBLICA'"
        @click="$refs.fileInput.click()"
        ) Importar CSV
        input(
          type="file"
          ref="fileInput"
          @change="handleFileUpload"
          accept=".xlsx"
          style="display: none;"
        )
        select(v-model="filters.usuarioNome")
          option(value="") Nome do Usuário
          option(v-for="nome in usuariosDisponiveis" :key="nome" :value="nome") {{ nome }}

        select(v-model="filters.usuarioEmail")
          option(value="") Email do Usuário
          option(v-for="email in emailsDisponiveis" :key="email" :value="email") {{ email }}

        select(v-model="filters.veiculoPlaca")
          option(value="") Placa
          option(v-for="placa in placasDisponiveis" :key="placa" :value="placa") {{ placa }}

        select(v-model="filters.veiculoMarca")
          option(value="") Marca
          option(v-for="marca in marcasDisponiveis" :key="marca" :value="marca") {{ marca }}

        select(v-model="filters.veiculoModelo")
          option(value="") Modelo
          option(v-for="modelo in modelosDisponiveis" :key="modelo" :value="modelo") {{ modelo }}
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
        button(@click="limparFiltros").btn Limpar Filtros


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
            td(colspan="11" style="text-align: center;") Sem registros
          tr(v-for="(ocorrencia, index) in ocorrencias" :key="index")
            td(data-label="Usuário") {{ ocorrencia.usuarioNome }}
            td(data-label="Email")  {{ ocorrencia.usuarioEmail }}
            td(data-label="Placa")  {{ ocorrencia.veiculoPlaca }}
            td(data-label="Modelo")  {{ ocorrencia.veiculoModelo }}
            td(data-label="Marca")  {{ ocorrencia.veiculoMarca }}
            td(data-label="Local") 
              button.btn-local(@click="abrirModalLocal(ocorrencia)")
                span 📍 Ver Local
            td(data-label="Status")  {{ ocorrencia.statusDenuncia }}
            td(data-label="Hora Denuncia")  {{ ocorrencia.horaOcorrencia }}
            td(data-label="Data e hora da ocorrencia")  {{ formatDataHora(ocorrencia.dataHora) }}
            td(data-label="Descricao Ocorrencia")  {{ ocorrencia.descricaoOcorrencia }}
            td(data-label="Artigo")  {{ ocorrencia.artigoCodigo }} - {{ ocorrencia.artigoDescricao }}
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
import {
  obterOcorrencias,
  obterTodasOcorrencias,
  contarOcorrenciasPorStatus,
} from '@/services/ocorrenciasService'
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
import { importarCsv } from '@/services/arquivoService' // Adicione essa linha no topo
import { toast } from 'vue3-toastify'
const statusSelecionado = ref('Todos')

const filtrarPorStatus = (status: string) => {
  if (status === 'Todos') {
    // Se o status for 'Todos', limpa o filtro de status
    statusSelecionado.value = 'Todos' // Marca 'Todos' como ativo
    filters.value.status = '' // Limpa o filtro de status
  } else {
    // Para os outros status, apenas define o status selecionado
    statusSelecionado.value = status
    filters.value.status = status
  }

  currentPage.value = 0 // Reseta a página para a primeira página
  fetchOcorrencias() // Recarrega as ocorrências com o filtro atualizado
}

const store = useLoadingStore()
const ocorrencias = ref<any[]>([])
const artigos = ref<any[]>([]) // Para armazenar os artigos
const filters = ref({
  status: '',
  artigo: '',
  hora: '',
  dataInicio: '',
  dataFim: '',
  usuarioNome: '',
  usuarioEmail: '',
  veiculoPlaca: '',
  veiculoMarca: '',
  veiculoModelo: '',
})
const placasDisponiveis = ref<string[]>([])
const marcasDisponiveis = ref<string[]>([])
const modelosDisponiveis = ref<string[]>([])
const usuariosDisponiveis = ref<string[]>([])
const emailsDisponiveis = ref<string[]>([])
const currentPage = ref(0)
const totalPages = ref(1)
const totalelements = ref(1)
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

const limparFiltros = () => {
  filters.value = {
    status: '',
    artigo: '',
    hora: '',
    dataInicio: '',
    dataFim: '',
    usuarioNome: '',
    usuarioEmail: '',
    veiculoPlaca: '',
    veiculoMarca: '',
    veiculoModelo: '',
  }
  statusSelecionado.value = 'Todos'
  fetchOcorrencias()
}

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
    carregarFiltrosDinamicos()
    totalelements.value = data.totalElements
    totalPages.value = data.totalPages
    fetchContagemStatus() // Atualiza o dashboard
    store.stopLoading() // Para o loading quando a ação terminar
  } catch (error) {
    store.stopLoading() // Para o loading quando a ação terminar
    console.error('Erro ao carregar as ocorrências:', error)
  }
}

const carregarFiltrosDinamicos = async () => {
  try {
    const todas = await obterTodasOcorrencias()

    // Garantir que os Sets são tipados como Set<string>
    const placas = new Set<string>()
    const marcas = new Set<string>()
    const modelos = new Set<string>()
    const usuarios = new Set<string>()
    const emails = new Set<string>()

    todas.forEach((ocorrencia: any) => {
      // Aqui estamos verificando se as propriedades existem antes de adicionar aos Sets
      if (ocorrencia.veiculoPlaca) placas.add(ocorrencia.veiculoPlaca)
      if (ocorrencia.veiculoMarca) marcas.add(ocorrencia.veiculoMarca)
      if (ocorrencia.veiculoModelo) modelos.add(ocorrencia.veiculoModelo)
      if (ocorrencia.usuarioNome) usuarios.add(ocorrencia.usuarioNome)
      if (ocorrencia.usuarioEmail) emails.add(ocorrencia.usuarioEmail)
    })

    // Atribuindo os valores aos refs
    placasDisponiveis.value = Array.from(placas)
    marcasDisponiveis.value = Array.from(marcas)
    modelosDisponiveis.value = Array.from(modelos)
    usuariosDisponiveis.value = Array.from(usuarios)
    emailsDisponiveis.value = Array.from(emails)
  } catch (e) {
    console.error('Erro ao carregar filtros dinâmicos:', e)
  }
}

const handleFileUpload = async (event: Event) => {
  const input = event.target as HTMLInputElement
  const file = input?.files?.[0]
  if (!file) return

  try {
    store.startLoading() // Inicia o loading
    const resultado = await importarCsv(file)
    store.stopLoading() // Para o loading quando a ação terminar

    fetchOcorrencias() // Atualiza a lista após importar
    toast.success('Importado com sucesso:')
  } catch (error: any) {
    store.stopLoading() // Para o loading quando a ação terminar
    window.location.reload()
    toast.error('Erro na importação: ', error.message)
    console.error('Erro na importação:', error.message)
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
  carregarFiltrosDinamicos() // ✅ popula filtros apenas uma vez
})

const contagemStatus = ref({})

const fetchContagemStatus = async () => {
  contagemStatus.value = await contarOcorrenciasPorStatus()
}
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

.btn-import {
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

@media (max-width: 768px) {
  .ocorrencias-table,
  .ocorrencias-table thead,
  .ocorrencias-table tbody,
  .ocorrencias-table th,
  .ocorrencias-table td,
  .ocorrencias-table tr {
    display: block;
  }

  .ocorrencias-table thead {
    display: none; /* Oculta o cabeçalho em telas pequenas */
  }

  .ocorrencias-table td {
    position: relative;
    padding-left: 50%;
    border: none;
    border-bottom: 1px solid #eee;
  }

  .ocorrencias-table td::before {
    content: attr(data-label);
    display: block;
    font-weight: bold;
    margin-bottom: 0.25rem;
    color: #555;
  }

  .ocorrencias-table tr {
    margin-bottom: 1rem;
    border: 1px solid #ccc;
    border-radius: 6px;
    padding: 1rem;
    background-color: #fff;
  }
}

/* Tabela responsiva */
.ocorrencias-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.875rem;
  margin-top: 1rem;
}

/* Títulos das colunas */
.ocorrencias-table th,
.ocorrencias-table td {
  padding: 0.5rem;
  border: 1px solid #ddd;
  text-align: left;
}

/* Cabeçalhos da tabela */
.ocorrencias-table th {
  background-color: #f7f7f7;
}

/* Responsividade: Ajustar para telas pequenas */
@media (max-width: 768px) {
  /* Tabela se torna flexível e cada linha se comporta como um bloco */
  .ocorrencias-table,
  .ocorrencias-table td {
    display: block;
    width: 100%;
  }

  /* Rótulos da célula (data-label) são exibidos em cima dos valores */
  .ocorrencias-table td::before {
    content: attr(data-label);
    display: block;
    font-weight: bold;
    margin-bottom: 0.25rem;
    color: #555;
    text-align: left;
  }

  /* Remover bordas entre as células para um layout mais claro */
  .ocorrencias-table td {
    border: none;
    padding-left: 0;
    padding-right: 0;
  }

  /* Para dar espaçamento entre os elementos */
  .ocorrencias-table td {
    padding: 10px;
  }
}
.dashboard-cards {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin: 2rem 0;
  flex-wrap: wrap;
}

.card {
  display: flex;
  flex-direction: column; /* Se o conteúdo estiver em coluna */
  align-items: center; /* Centraliza o conteúdo horizontalmente */
  justify-content: center; /* Centraliza o conteúdo verticalmente */
  flex: 1 1 200px;
  max-width: 250px;
  min-height: 120px;
  border-radius: 8px;
  padding: 1.5rem;
  color: white;
  text-align: center;
  font-size: 1.2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease,
    border 0.3s ease;
}

/* Card colors */
.card[status='Em andamento'] {
  background-color: #ffc107;
}

.card[status='Todos'] {
  background-color: #0772ff;
}

.card[status='Solucionado'] {
  background-color: #28a745;
}

.card[status='Arquivado'] {
  background-color: #6c757d;
}

.card .count {
  font-size: 2.5rem;
  font-weight: bold;
  margin-top: 0.5rem;
}

/* Estilo para quando o card está ativo/selecionado */
.card.ativo {
  transform: scale(1.05); /* Aumenta ligeiramente o card */
  box-shadow: 0 0 12px rgba(0, 0, 0, 0.2); /* Sombras mais fortes */
  border: 2px solid #fff; /* Borda branca para destacar */
  cursor: pointer;
  z-index: 1; /* Garante que o card ativo fique acima dos outros */
}

/* Estilo para os cards inativos (não selecionados), diminuindo seu tamanho */
.card.inativo {
  transform: scale(0.8); /* Diminui o tamanho dos cards não selecionados */
  opacity: 0.7; /* Torna os cards inativos mais transparentes */
}

/* Efeito de hover para dar um feedback visual mais interessante */
.card:hover {
  transform: scale(1.02); /* Aumenta um pouco o card no hover */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15); /* Sombras mais suaves ao passar o mouse */
}
</style>
