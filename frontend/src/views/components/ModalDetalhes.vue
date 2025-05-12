<template lang="pug">
  .modal-backdrop
    .modal-content
      header.modal-header
        h2 Detalhes da Investigação
        button.close(@click="$emit('close')") ×

      section.modal-body
        .section
          h3 Seção da Ocorrência
          .info-titulo
            label Título:
            span {{ ocorrencia.descricaoOcorrencia }}
        
        .section
          h3 Veículo
          .info-item
            label Marca:
            span {{ ocorrencia.veiculoMarca }}
          .info-item
            label Modelo:
            span {{ ocorrencia.veiculoModelo }}
          .info-item
            label Placa:
            span {{ ocorrencia.veiculoPlaca }}

        .section
          h3 Local
          .info-item
            label CEP:
            span {{ ocorrencia.cep }}
          .info-item
            label Logradouro:
            span {{ ocorrencia.logradouro }}
          .info-item
            label Bairro:
            span {{ ocorrencia.bairro }}
          .info-item
            label Cidade:
            span {{ ocorrencia.cidade }}
          .info-item
            label Estado:
            span {{ ocorrencia.estado }}

        .section
          h3 Denunciante
          .info-item
            label Nome:
            span {{ ocorrencia.usuarioNome }}
          .info-item
            label E-mail:
            span {{ ocorrencia.usuarioEmail }}

        .section
          h3 Ações de Investigação
          ul.acaoinvestgiacao-list(v-if="acoesInvestigacao.length")
            li(v-for="(acao, index) in acoesInvestigacao" :key="index")
              span.nome Tipo: {{ acao.tipoAcao }}
              br
              span.descricao Descrição: {{ acao.descricaoAcao }}
              br
              span.data Data: {{ new Date(acao.dataAcao).toLocaleString('pt-BR') }}
              br
              span.responsavel Responsável: {{ acao.nomeResponsavel }}
          p(v-else) Nenhuma ação de investigação registrada.

      footer.modal-footer
        button(@click="$emit('close')").btn-sm Fechar
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { obterOcorrenciaPorId } from '@/services/ocorrenciasService'
import { fetchUserData } from '@/services/authService' // Supondo que você tenha esse serviço para obter usuário

const props = defineProps({
  ocorrencia: {
    type: Object,
    required: true, // Garantir que a prop ocorrencia será obrigatória
  },
  acoesInvestigacao: {
    type: Array,
    required: false,
    default: () => [],
  },
})

const emit = defineEmits(['close', 'salvo'])

const acoesInvestigacao = ref<any[]>([]) // Para armazenar as ações de investigação
const responsavel = ref<any>(null) // Para armazenar os dados do responsável

// Função para carregar dados da ocorrência e ações de investigação
const carregarDadosOcorrencia = async () => {
  try {
    // Verifica se 'ocorrencia' está definida
    if (!props.ocorrencia) {
      throw new Error('Ocorrência não encontrada.')
    }

    // Chama a API para obter os detalhes da ocorrência usando o ID
    const resposta = await obterOcorrenciaPorId(props.ocorrencia.id)
    console.log('Resposta da API:', resposta)

    // Preenche as ações de investigação com a resposta da API
    acoesInvestigacao.value = resposta.acoesInvestigacao || []

    // Verifica se existe um responsável e faz a consulta para obter os dados do usuário
    if (resposta.acoesInvestigacao && resposta.acoesInvestigacao.length > 0) {
      const idResponsavel = resposta.acoesInvestigacao[0].idResponsavel
      await carregarResponsavel(idResponsavel)
    }
  } catch (error) {
    console.error('Erro ao carregar dados da ocorrência:', error)
  }
}

// Função para carregar os dados do responsável
const carregarResponsavel = async (idResponsavel: number) => {
  try {
    const usuario = await fetchUserData()
    responsavel.value = usuario // Armazenando o usuário na variável `responsavel`
  } catch (error) {
    console.error('Erro ao carregar dados do responsável:', error)
  }
}

// Chama a função quando o modal for montado
onMounted(() => {
  // Certifique-se de que a ocorrência esteja definida antes de carregar os dados
  if (props.ocorrencia) {
    carregarDadosOcorrencia()
  }
})
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 600px;
  height: 90vh; /* Altura máxima visível */
  display: flex;
  flex-direction: column;
  position: relative;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  overflow: hidden; /* Impede estouro do conteúdo */
}

.modal-header {
  flex-shrink: 0;
  padding: 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #ddd;
}

.modal-body {
  flex: 1;
  overflow-y: auto; /* Aqui está o scroll */
  padding: 1.5rem;
}

.modal-footer {
  flex-shrink: 0;
  padding: 1rem 1.5rem;
  border-top: 1px solid #ddd;
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  background-color: #fff;
}

.modal-header h2 {
  font-size: 1.25rem;
  color: #218838;
}

.modal-header .close {
  background: transparent;
  border: 2px solid #218838;
  border-radius: 50%;
  font-size: 1.2rem;
  width: 32px;
  height: 32px;
  color: #218838;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
}

.modal-header .close:hover {
  background-color: #218838;
  color: white;
}

/* Rodapé com botões */
.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1rem;
}

.btn-sm {
  padding: 0.4rem 0.9rem;
  font-size: 0.875rem;
  border-radius: 6px;
  background-color: #fff;
  color: #218838;
  border: 2px solid #218838;
  cursor: pointer;
  transition: all 0.3s ease-in-out;
}

.btn-sm:hover {
  background-color: #218838;
  color: #fff;
  transform: scale(1.03);
}

.btn-sm:active {
  transform: scale(0.97);
}

/* Campos de formulário */
textarea,
select {
  width: 100%;
  padding: 0.6rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 0.875rem;
  transition: border-color 0.2s;
  margin-bottom: 1rem;
}

textarea:focus,
select:focus {
  outline: none;
  border-color: #218838;
  box-shadow: 0 0 0 2px rgba(33, 136, 56, 0.2);
}

/* Sessões */
.section {
  margin-bottom: 1.5rem;
  border-bottom: 1px solid #ddd;
  padding-bottom: 1rem;
}

.section h3 {
  margin-bottom: 0.75rem;
  font-size: 1rem;
  color: #333;
  border-left: 4px solid #218838;
  padding-left: 0.5rem;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.4rem;
  font-size: 0.875rem;
}

.info-titulo {
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.4rem;
  font-size: 0.875rem;
}

.info-titulo label {
  font-weight: 600;
  color: #555;
  margin-right: 1rem;
  white-space: nowrap;
}

.info-titulo span {
  flex: 1;
  text-align: right;
  color: #333;
  word-break: break-word;
}

.info-titulo span {
  font-weight: 600;
  color: #555;
  margin-right: 1rem;
  white-space: nowrap;
  font-weight: bold;
}

.info-item span {
  flex: 1;
  text-align: right;
  color: #333;
  word-break: break-word;
}

.acaoinvestgiacao-list {
  list-style: none;
  padding-left: 0;
  margin: 0;
}

.acaoinvestgiacao-list li {
  flex-direction: column;
  border-bottom: 1px solid #ddd; /* Separador sutil entre ações */
  padding-bottom: 10px;
  margin-bottom: 10px;
}

.acaoinvestgiacao-list li:last-child {
  border-bottom: none; /* Remove a borda do último item */
}

.acaoinvestgiacao-list span {
  display: inline-block;
  margin-bottom: 3px; /* Ajustar o espaço entre os itens */
  color: #333;
}

.acaoinvestgiacao-list .nome {
  font-weight: 600;
  color: #218838; /* Um verde mais suave para o tipo da ação */
}

.acaoinvestgiacao-list .descricao,
.acaoinvestgiacao-list .data {
  margin-bottom: 3px; /* Ajustar o espaçamento entre esses itens */
  font-size: 0.875rem; /* Menor tamanho para descrição e data */
  color: #555;
}

.acaoinvestgiacao-list .responsavel {
  font-weight: bold;
  color: #333;
  margin-top: 3px; /* Menor espaço entre responsável e os outros itens */
  font-size: 0.9rem;
}
</style>
