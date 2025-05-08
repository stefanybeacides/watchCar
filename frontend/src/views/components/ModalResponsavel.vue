<template lang="pug">
  .modal-backdrop
    .modal-content
      header.modal-header
        h2 Responsável pela Ocorrência
        button.close(@click="close") ×

      section.modal-body
        .section
          h3 Ocorrência
          .info-item
            label Usuário:
            span {{ ocorrencia.usuarioNome }}
          .info-item
            label Email:
            span {{ ocorrencia.usuarioEmail }}
          .info-item
            label Status:
            span {{ ocorrencia.statusDenuncia }}
          .info-item
            label Hora:
            span {{ ocorrencia.horaOcorrencia }}
          .info-item
            label Data:
            span {{ ocorrencia.dataHora }}
          .info-titulo
            label Descrição:
            span {{ ocorrencia.descricaoOcorrencia }}
          .info-item
            label Artigo:
            span {{ ocorrencia.artigoCodigo }} - {{ ocorrencia.artigoDescricao }}

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
          h3 Ações de Investigação
          ul.acaoinvestigacao-list(v-if="acoes.length")
            li(v-for="(acao, index) in acoes" :key="index")
              span.tipo Tipo: {{ acao.tipoAcao }}
              br
              span.descricao Descrição: {{ acao.descricaoAcao }}
              br
              span.data Data: {{ new Date(acao.dataAcao).toLocaleString('pt-BR') }}
              br
              span.responsavel Responsável: {{ acao.nomeResponsavel }}
              br
              span.distintivo Distintivo: {{ acao.distintivoResponsavel }}
              br
              span.delegacia Delegacia: {{ acao.delegaciaResponsavel }}
          p(v-else) Nenhuma ação de investigação registrada.

        .section
          h3 Histórico de Responsáveis
          ul.historico-list
            li(v-for="(item, index) in historico" :key="index")
              span.nome {{ item.nome }}
              br
              span.data {{ item.data ? new Date(item.data).toLocaleString('pt-BR') : 'Data não disponível' }}
              br
              span.distintivo Distintivo: {{ item.distintivo || 'Não informado' }}
              br
              span.delegacia Delegacia: {{ item.delegacia || 'Não informada' }}
        p.responsavel-status(v-if="isResponsavel")
          strong Você já é responsável por esta ocorrência.
        p.responsavel-status(v-else)
          strong Você ainda não é responsável por esta ocorrência.

      footer.modal-footer
        button.btn-sm(@click="confirmarAcao")
          | {{ isResponsavel ? 'Desassumir' : 'Assumir' }}
        button.btn-sm(@click="close") Cancelar
</template>

<script setup lang="ts">
import { defineProps, defineEmits, onMounted, ref, watch } from 'vue'
import { toast } from 'vue3-toastify'
import {
  obterOcorrenciaPorId,
  assumirResponsavel,
  desassumirResponsavel,
} from '@/services/ocorrenciasService'
import { fetchUserData } from '@/services/authService'
import { useLoadingStore } from '@/stores/loadingStore'
const store = useLoadingStore()
const props = defineProps({
  ocorrencia: {
    type: Object,
    required: true,
  },
  userId: {
    type: String,
    required: true,
  },
  isResponsavel: {
    type: Boolean,
    required: true,
  },
})

const emit = defineEmits(['close', 'salvo'])
const historico = ref<
  { nome: string; data: string | null; distintivo: string; delegacia: string }[]
>([])
const acoes = ref<any[]>([])

const carregarHistorico = async () => {
  try {
    if (!props.ocorrencia) {
      throw new Error('Ocorrência não encontrada.')
    }

    const resposta = await obterOcorrenciaPorId(props.ocorrencia.id)

    acoes.value = resposta.acoesInvestigacao || []
    historico.value = resposta.historicoResponsaveis || []
  } catch (error) {
    console.error('Erro ao carregar dados da ocorrência:', error)
  }
}

const confirmarAcao = async () => {
  try {
    if (props.isResponsavel) {
      store.startLoading() // Inicia o loading
      await desassumirResponsavel(props.ocorrencia.id, props.userId)
    } else {
      store.startLoading() // Inicia o loading
      await assumirResponsavel(props.ocorrencia.id, props.userId)
      store.stopLoading() // Para o loading quando a ação terminar
    }
    emit('salvo')
    emit('close')
  } catch (error) {
    store.stopLoading() // Para o loading quando a ação terminar
    toast.error('Erro ao atualizar responsável.')
  }
}

const close = () => {
  emit('close')
}

onMounted(carregarHistorico)

watch(() => props.ocorrencia.id, carregarHistorico)
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
  height: 90vh; /* Limita a altura do modal */
  display: flex;
  flex-direction: column;
  position: relative;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  overflow: hidden; /* Garante que o conteúdo não vaze para fora */
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
  overflow-y: auto; /* Garante rolagem no conteúdo */
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

.section {
  margin-bottom: 1.5rem;
  border-bottom: 1px solid #ddd;
  padding-bottom: 1rem;
}

/* Remove linha superior se necessário */
.section.no-border-top {
  border-top: none;
  margin-top: -1rem; /* aproxima visualmente */
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.4rem;
  font-size: 0.875rem;
}

.info-titulo label,
.info-item label {
  font-weight: 600;
  color: #555;
  margin-right: 1rem;
  white-space: nowrap;
}

.info-titulo span,
.info-item span {
  flex: 1;
  text-align: right;
  color: #333;
  word-break: break-word;
}

/* Sem estilo visual adicional na responsabilidade */
.responsavel-status {
  margin-top: 20px;
  font-weight: bold;
  color: #218838;
  text-align: center;
}

.acaoinvestigacao-list {
  list-style: none;
  padding-left: 0;
  margin: 0;
}

.acaoinvestigacao-list li {
  flex-direction: column;
  border-bottom: 1px solid #ddd; /* Separador sutil entre ações */
  padding-bottom: 10px;
  margin-bottom: 10px;
}

.acaoinvestigacao-list li:last-child {
  border-bottom: none; /* Remove a borda do último item */
}

.acaoinvestigacao-list span {
  display: inline-block;
  margin-bottom: 3px; /* Ajustar o espaço entre os itens */
  color: #333;
}

.acaoinvestigacao-list .tipo {
  font-weight: 600; /* Negrito */
  color: #218838; /* Verde */
}

.acaoinvestigacao-list .nome {
  font-weight: 600;
  color: #218838; /* Um verde mais suave para o tipo da ação */
}

.acaoinvestigacao-list .descricao,
.acaoinvestigacao-list .data {
  margin-bottom: 3px; /* Ajustar o espaçamento entre esses itens */
  font-size: 0.875rem; /* Menor tamanho para descrição e data */
  color: #555;
}

.acaoinvestigacao-list .responsavel {
  font-weight: bold;
  color: #333;
  margin-top: 3px; /* Menor espaço entre responsável e os outros itens */
  font-size: 0.9rem;
}

.acaoinvestigacao-list .distintivo,
.acaoinvestigacao-list .delegacia {
  font-size: 0.875rem;
  color: #555;
  margin-bottom: 3px;
}

.acaoinvestigacao-list .distintivo {
  font-weight: bold;
}

.acaoinvestigacao-list .delegacia {
  font-style: italic;
}

.historico-list {
  list-style: none;
  padding-left: 0;
  margin: 0;
}

.historico-list li {
  flex-direction: column;
  padding: 0.5rem 0;
  border-bottom: 1px solid #ddd;
  margin-bottom: 10px;
}

.historico-list li:last-child {
  border-bottom: none; /* Remove a borda do último item */
}

.historico-list span {
  display: inline-block;
  margin-bottom: 3px; /* Ajuste do espaço entre os itens */
  color: #333;
}

.historico-list .nome {
  font-weight: 600;
  color: #218838; /* Um verde mais suave para o nome */
}

.historico-list .data {
  font-size: 0.875rem; /* Tamanho reduzido para a data */
  color: #555;
  margin-bottom: 3px; /* Ajustar o espaçamento */
}

.historico-list .distintivo,
.historico-list .delegacia {
  font-size: 0.875rem;
  color: #555;
  margin-bottom: 3px;
}

.historico-list .distintivo {
  font-weight: bold;
}

.historico-list .delegacia {
  font-style: italic;
}
</style>
