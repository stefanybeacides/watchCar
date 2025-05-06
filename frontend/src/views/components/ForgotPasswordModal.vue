<template>
  <div v-if="isVisible" class="modal-overlay" @click.self="closeModal">
    <div class="modal-content">
      <h3>Recuperação de Senha</h3>
      <form @submit.prevent="handleSubmit">
        <div class="input-group">
          <label for="recoverInput">CPF ou E-mail</label>
          <input
            type="text"
            id="recoverInput"
            v-model="recoverInput"
            placeholder="Informe seu CPF ou E-mail"
            required
          />
        </div>

        <button type="submit" class="submit-btn">Enviar</button>
      </form>

      <button @click="closeModal" class="close-btn">Fechar</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

// Estado de visibilidade
const isVisible = ref(false)
const recoverInput = ref('')

// Método para abrir o modal
const openModal = () => {
  isVisible.value = true
}

const closeModal = () => {
  isVisible.value = false
  recoverInput.value = ''
}

defineExpose({
  openModal,
  closeModal,
})

// Método para lidar com o envio
const handleSubmit = () => {
  if (recoverInput.value) {
    alert('Solicitação de recuperação enviada!')
    closeModal()
  } else {
    alert('Por favor, informe o CPF ou E-mail.')
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 2rem;
  border-radius: 8px;
  width: 400px;
  text-align: center;
}

h3 {
  margin-bottom: 1.5rem;
}

.input-group {
  margin-bottom: 1rem;
}

input {
  width: 100%;
  padding: 0.8rem;
  margin-top: 0.5rem;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.submit-btn {
  width: 100%;
  padding: 0.8rem;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.submit-btn:hover {
  background-color: #369f6f;
}

.close-btn {
  margin-top: 1rem;
  background-color: #ccc;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
}

.close-btn:hover {
  background-color: #999;
}
</style>
