<template lang="pug">
  .ocorrencias-container
    h1.titulo Ocorrências Registradas

    //- Filtros e botão
    .filters
      .inputs
      .filters .inputs
        select(v-model="filters.status")
          option(value="") Selecione o Status
          option(value="Em andamento") Em andamento
          option(value="Solucionado") Solucionado
          option(value="Arquivado") Arquivado
        input(type="text" v-model="filters.artigo" placeholder="Artigo")
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
            th Status
            th Hora
            th Data
            th Descrição
        tbody
          tr(v-if="ocorrencias.length === 0")
            td(colspan="9") Sem registros
          tr(v-for="(ocorrencia, index) in ocorrencias" :key="index")
            td {{ ocorrencia.usuarioNome }}
            td {{ ocorrencia.usuarioEmail }}
            td {{ ocorrencia.veiculoPlaca }}
            td {{ ocorrencia.veiculoModelo }}
            td {{ ocorrencia.veiculoMarca }}
            td {{ ocorrencia.statusDenuncia }}
            td {{ ocorrencia.horaOcorrencia }}
            td {{ ocorrencia.dataHora }}
            td {{ ocorrencia.descricaoOcorrencia }}

      //- Paginação à direita
      .pagination
        button(@click="changePage(currentPage - 1)" :disabled="currentPage === 0").pagination-btn Página Anterior
        span Página {{ currentPage + 1 }} de {{ totalPages }}
        button(@click="changePage(currentPage + 1)" :disabled="currentPage + 1 >= totalPages").pagination-btn Próxima Página
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { obterOcorrencias } from '@/services/ocorrenciasService'

const ocorrencias = ref<any[]>([])
const filters = ref({
  status: '',
  artigo: '',
  hora: '',
  dataInicio: '',
  dataFim: '',
})
const currentPage = ref(0)
const totalPages = ref(1)
const pageSize = ref(10)
const token = localStorage.getItem('authToken')

if (!token) {
  localStorage.setItem('loginMessage', 'Faça login.')
  window.location.href = '/login'
}

const fetchOcorrencias = async () => {
  try {
    const data = await obterOcorrencias(filters.value, currentPage.value, pageSize.value)
    ocorrencias.value = data.content
    totalPages.value = data.totalPages
  } catch (error) {
    console.error('Erro ao carregar as ocorrências:', error)
  }
}

const changePage = (page: number) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
    fetchOcorrencias()
  }
}

onMounted(fetchOcorrencias)
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
  width: 150px;
}

.filters .btn {
  background-color: #28a745;
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

.table-wrapper {
  overflow-x: auto;
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
  width: 150px;
  font-size: 0.875rem;
}
</style>
