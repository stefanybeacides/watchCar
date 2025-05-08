<template lang="pug">
  .modal-backdrop
    .modal
      h2 Detalhes do Local

      section.local-info
        h3.section-title 📍 Localização
        .info-item
          strong Logradouro:
          span {{ ocorrencia.logradouro }}
        .info-item
          strong Bairro:
          span {{ ocorrencia.bairro }}
        .info-item
          strong Cidade:
          span {{ ocorrencia.cidade }}
        .info-item
          strong Estado:
          span {{ ocorrencia.estado }}
        .info-item
          strong CEP:
          span {{ ocorrencia.cep }}

      section.mapa-container(v-if="ocorrencia.cep")
        iframe(
          :src="`https://www.google.com/maps?q=${ocorrencia.cep}&output=embed`"
          width="100%"
          height="300"
          frameborder="0"
          style="border:0"
          allowfullscreen
        )

      .botao-fechar
        button.fechar(@click="$emit('close')") Fechar
</template>
<script setup lang="ts">
const props = defineProps({
  ocorrencia: {
    type: Object,
    required: true,
  },
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
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.modal {
  background: #fff;
  padding: 2rem;
  border-radius: 10px;
  width: 520px;
  max-width: 95%;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
  animation: fadeIn 0.3s ease-in-out;
}

h2 {
  margin-bottom: 1rem;
  text-align: center;
  color: #218838;
  font-weight: 600;
}

.section-title {
  font-size: 1.1rem;
  margin-bottom: 1rem;
  color: #218838;
  border-bottom: 1px solid #c4e3cc;
  padding-bottom: 0.4rem;
}

.local-info {
  background-color: #f9fdf9;
  border: 1px solid #d0e9d2;
  padding: 1rem;
  border-radius: 8px;
  margin-bottom: 1.5rem;
}

.info-item {
  display: flex;
  gap: 0.5rem;
  margin: 0.4rem 0;
  line-height: 1.5;
}

.info-item strong {
  color: #218838;
  min-width: 90px;
}

.info-item span {
  color: #333;
}

.mapa-container {
  margin-bottom: 1.5rem;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
}

.botao-fechar {
  display: flex;
  justify-content: flex-end;
}

.fechar {
  background-color: #218838;
  color: white;
  border: none;
  padding: 0.6rem 1.2rem;
  border-radius: 6px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s ease-in-out;
}

.fechar:hover {
  background-color: #1a6e2d;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
