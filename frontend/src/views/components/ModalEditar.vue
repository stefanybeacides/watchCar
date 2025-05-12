<template lang="pug">
  .modal-backdrop
    .modal-content
      header.modal-header
        h2 Novo Detalhe da Investigação
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
          h3 Novo Detalhe
          label Tipo de Ação:
          select(v-model="novoDetalhe.tipoAcao")
            option(value="") Selecione
            option(value="INVESTIGACAO") Investigação DP
            option(value="ACOMPANHAMENTO") Acompanhamento
            option(value="Solucionado") Solucionar
            option(value="Arquivado") Arquivado
            option(value="OUTRO") Outro

          label Descrição da Ação:
          textarea(v-model="novoDetalhe.descricaoAcao" rows="4")

      footer.modal-footer
        button(@click="salvar").btn-sm Salvar
        button(@click="$emit('close')").btn-sm Cancelar
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { enviarAcaoInvestigacao } from '@/services/ocorrenciasService'
import { toast } from 'vue3-toastify'
import { useLoadingStore } from '@/stores/loadingStore'
const store = useLoadingStore()
const props = defineProps({
  ocorrencia: Object,
})

const emit = defineEmits(['close', 'salvo'])

const novoDetalhe = ref({
  tipoAcao: '',
  descricaoAcao: '',
})

// Quando abrir o modal, limpa campos
watch(
  () => props.ocorrencia,
  () => {
    novoDetalhe.value = { tipoAcao: '', descricaoAcao: '' }
  },
)

const salvar = async () => {
  const { tipoAcao, descricaoAcao } = novoDetalhe.value || {}

  if (!tipoAcao?.trim() || !descricaoAcao?.trim()) {
    toast.warning('Preencha todos os campos obrigatórios.')
    return
  }

  const userId = parseInt(localStorage.getItem('userId') || '0', 10)

  if (!userId || isNaN(userId)) {
    toast.error('Usuário não identificado.')
    return
  }
  if (!props.ocorrencia) {
    toast.error('Dados da ocorrência não disponíveis.')
    return
  }

  const payload = {
    idDenuncia: props.ocorrencia.id,
    tipoAcao: tipoAcao.trim(),
    descricaoAcao: descricaoAcao.trim(),
    dataAcao: new Date().toISOString(),
    idResponsavel: userId,
  }

  try {
    store.startLoading() // Inicia o loading

    await enviarAcaoInvestigacao(payload)
    emit('salvo')
    emit('close')
    toast.success('Ação registrada com sucesso.')
    store.stopLoading() // Para o loading quando a ação terminar
  } catch (err) {
    store.stopLoading() // Para o loading quando a ação terminar
    console.error('Erro ao salvar ação:', err)
    toast.error('Erro ao salvar ação de investigação.')
  }
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
</style>
