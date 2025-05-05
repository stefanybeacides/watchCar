<template>
  <div class="ocorrencias">
    <h1>Ocorrências Registradas</h1>
    <div v-if="ocorrencias.length === 0">
      <p>Nenhuma ocorrência registrada.</p>
    </div>
    <ul>
      <li v-for="(ocorrencia, index) in ocorrencias" :key="index">
        {{ ocorrencia.texto }}
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const ocorrencias = ref<any[]>([])

onMounted(async () => {
  // Chamada de serviço para pegar as ocorrências
  ocorrencias.value = await fetchOcorrencias()
})

const fetchOcorrencias = async () => {
  // Simula uma requisição ao servidor para pegar as ocorrências
  return [
    { texto: 'Ocorrência 1: Roubo de veículo' },
    { texto: 'Ocorrência 2: Agitação em área pública' },
  ]
}
</script>

<style scoped>
.ocorrencias {
  padding: 2rem;
}

h1 {
  text-align: center;
}
</style>
