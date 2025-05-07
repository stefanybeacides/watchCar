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



        // Exibe se o usuário é responsável ou não
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
import {
  obterOcorrenciaPorId,
  assumirResponsavel,
  desassumirResponsavel,
} from '@/services/ocorrenciasService'

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

const historico = ref<{ nome: string; data: string | null }[]>([])

const carregarHistorico = async () => {
  try {
    const resposta = await obterOcorrenciaPorId(props.ocorrencia.id)
    historico.value = resposta.historicoResponsaveis || []
  } catch (error) {
    console.error('Erro ao carregar histórico de responsáveis:', error)
  }
}

onMounted(carregarHistorico)

watch(() => props.ocorrencia.id, carregarHistorico)

const confirmarAcao = async () => {
  try {
    if (props.isResponsavel) {
      await desassumirResponsavel(props.ocorrencia.id, props.userId)
    } else {
      await assumirResponsavel(props.ocorrencia.id, props.userId)
    }
    emit('salvo')
    emit('close')
  } catch (error) {
    console.error('Erro ao atualizar responsável:', error)
  }
}

const close = () => {
  emit('close')
}
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

.historico-list {
  list-style: none;
  padding-left: 0;
}

.historico-list li {
  padding: 0.5rem 0;
  border-bottom: 1px solid #ddd;
  font-size: 0.875rem;
}

.historico-list span {
  display: block;
  margin-bottom: 2px;
  color: #333;
}

.historico-list .nome {
  font-weight: 600;
  color: #555;
}

.historico-list .data {
  color: #333;
  font-style: italic;
}
</style>
