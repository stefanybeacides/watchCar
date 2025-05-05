<template>
  <div class="denuncia">
    <h1>Registrar Denúncia</h1>
    <form @submit.prevent="registrarDenuncia">
      <div class="input-group">
        <label for="titulo">Título</label>
        <input type="text" id="titulo" v-model="titulo" required />
      </div>
      <div class="input-group">
        <label for="descricao">Descrição</label>
        <textarea id="descricao" v-model="descricao" required></textarea>
      </div>
      <button type="submit">Enviar Denúncia</button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const titulo = ref('')
const descricao = ref('')

const registrarDenuncia = async () => {
  if (!titulo.value || !descricao.value) {
    alert('Preencha todos os campos')
    return
  }

  // Chamada de serviço para registrar a denúncia
  // Exemplo de como você poderia enviar os dados
  console.log('Denúncia registrada:', { titulo: titulo.value, descricao: descricao.value })
  alert('Denúncia registrada com sucesso!')
}
</script>

<style scoped>
.denuncia {
  padding: 2rem;
}

h1 {
  text-align: center;
  margin-bottom: 1.5rem;
}

.input-group {
  margin-bottom: 1rem;
}

input,
textarea {
  width: 100%;
  padding: 0.8rem;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  width: 100%;
  padding: 0.8rem;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 1rem;
}
</style>
