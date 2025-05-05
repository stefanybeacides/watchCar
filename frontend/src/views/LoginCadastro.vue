<template lang="pug">
  section.container
    div.login-container
      h2 Login
      form(@submit.prevent="handleLogin")
        div.input-group
          label(for="loginCpf") CPF
          input(type="text" id="loginCpf" v-model="loginCpf" required)

        div.input-group
          label(for="loginPassword") Senha
          input(type="password" id="loginPassword" v-model="loginPassword" required)

        button(type="submit") Entrar

      RouterLink(to="/forgot-password") Esqueci minha senha

    div.register-container
      h2 Cadastro
      form(@submit.prevent="handleRegister")
    
        div.input-group
          label Tipo de Usuário
          div.radio-buttons
            label(for="policial")
              input(type="radio" id="policial" :value="2" v-model="userType")
              | Policial
            label(for="publico")
              input(type="radio" id="publico" :value="1" v-model="userType")
              | Público

        div.input-group
          label(for="name") Nome Completo
          input(type="text" id="name" v-model="name" required)

        div.input-group
          label(for="cpf") CPF
          input(type="text" id="cpf" v-model="cpf" required)

        div.input-group
          label(for="registerEmail") E-mail
          input(type="email" id="registerEmail" v-model="email" required)

        div.input-group
          label(for="registerPassword") Senha
          input(type="password" id="registerPassword" v-model="password" required)

        div.input-group
          label(for="confirmPassword") Confirmar Senha
          input(type="password" id="confirmPassword" v-model="confirmPassword" required)

        div.input-group(v-if="isPolicial")
          label(for="delegate") Delegacia
          input(type="text" id="delegate" v-model="delegate" required)

        div.input-group(v-if="isPolicial")
          label(for="badge") Distintivo
          input(type="text" id="badge" v-model="badge" required)

        div.input-group(v-if="isPolicial")
          label(for="ra") RA (Registro de Atividade)
          input(type="text" id="ra" v-model="ra" required)

        button(type="submit") Criar Conta
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import {
  login as loginApi,
  register as registerApi,
  fetchUserData as fetchUserData,
} from '@/services/authService'

const name = ref('')
const cpf = ref('')
const loginCpf = ref('')
const loginPassword = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const userType = ref(1) // 1 = Público, 2 = Policial
const delegate = ref('')
const badge = ref('')
const ra = ref('')
const alerta = ref<boolean | null>(null) // Novo campo alerta
const router = useRouter()

const isPolicial = computed(() => userType.value === 2) // 2 representa "Policial"

// Adicionando um watcher para depuração
watch(userType, (newValue) => {
  console.log('Novo tipo de usuário:', newValue) // Para depuração
})

const handleLogin = async () => {
  if (!loginCpf.value || !loginPassword.value) {
    alert('Preencha todos os campos obrigatórios para login.')
    return
  }

  try {
    const response = await loginApi(loginCpf.value, loginPassword.value)
    if (response && response.token) {
      localStorage.setItem('authToken', response.token)

      const userData = await fetchUserData() // Recupera os dados do usuário

      localStorage.setItem('userName', userData.username) // Assume que 'name' é a chave para o nome do usuário

      alert('Login realizado com sucesso!')
      window.dispatchEvent(new Event('storage'))
      router.push({ name: 'inicio' }) // Redireciona para a tela inicial após login
    } else {
      alert('Erro ao fazer login. Verifique seu CPF e senha.')
    }
  } catch (err) {
    console.error('Erro ao fazer requisição:', err)
    alert('Erro ao realizar login.')
  }
}

const handleRegister = async () => {
  if (!name.value || !cpf.value || !email.value || !password.value || !confirmPassword.value) {
    alert('Preencha todos os campos obrigatórios.')
    return
  }

  if (password.value !== confirmPassword.value) {
    alert('As senhas não coincidem.')
    return
  }

  if (isPolicial.value && (!delegate.value || !badge.value || !ra.value)) {
    alert('Preencha os campos adicionais para policial.')
    return
  }

  try {
    const response = await registerApi(
      name.value,
      password.value,
      email.value,
      cpf.value,
      userType.value,
    )

    if (response && response.success) {
      alert('Cadastro realizado com sucesso!')
      router.push({ name: 'login' })
    }
  } catch (err) {
    console.error('Erro ao fazer requisição:', err)
    alert('Erro ao cadastrar usuário.')
  }
}
</script>

<style scoped>
.container {
  display: flex;
  justify-content: center;
  gap: 2rem;
  padding: 2rem;
  min-height: 100vh;
  background-color: #f9f9f9;
}

.login-container,
.register-container {
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 2rem;
  width: 100%;
  max-width: 500px;
}

h2 {
  text-align: center;
  color: #333;
  margin-bottom: 1.5rem;
}

.input-group {
  margin-bottom: 1rem;
}

input,
select {
  width: 100%;
  padding: 0.8rem;
  margin-top: 0.5rem;
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

button:hover {
  background-color: #369f6f;
}

/* Estilo para o link "Esqueci a senha" */
RouterLink {
  display: block;
  text-align: center;
  color: #42b983;
  margin-top: 1rem;
}

.radio-buttons {
  margin-top: 3%;
  display: flex;
  justify-content: center; /* Centraliza os radio buttons */
  align-items: center; /* Alinha os radio buttons verticalmente */
  gap: 2rem; /* Espaço entre os radio buttons */
}

.radio-buttons label {
  display: flex;
  align-items: center;
  white-space: nowrap; /* Impede quebra de texto */
  font-size: 0.9rem;
  cursor: pointer;
}

.radio-buttons input {
  margin: 0 0.3rem 0 0; /* Espaço à direita do radio */
  transform: scale(1);
}

/* Responsividade */
@media (max-width: 768px) {
  .container {
    flex-direction: column;
    align-items: center;
  }

  .login-container,
  .register-container {
    width: 80%;
  }
}
</style>
