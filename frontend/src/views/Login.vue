<template lang="pug">
  section.container
    div.login-container
      h2 Login
      RouterLink.center-link(to="/register") Se não tem conta, cadastre-se
      form(@submit.prevent="handleLogin")
        div.input-group
          label(for="loginCpf") CPF
          input(
            type="text"
            id="loginCpf"
            v-model="loginCpf"
            maxlength="14"
            required
            autocomplete="off"
          )
          span.error-message(v-if="cpfError") {{ cpfError }}

        div.input-group.password-group
          label(for="loginPassword") Senha
          div.password-wrapper
            input(
              :type="showPassword ? 'text' : 'password'"
              id="loginPassword"
              v-model="loginPassword"
              required
            )
            span.toggle-icon(@click="showPassword = !showPassword")
              i(:class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'")

        button(type="submit") Entrar
      span.forgot-password(@click="openForgotPasswordModal") Esqueci a senha

    ForgotPasswordModal(ref="forgotPasswordModal")
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { login as loginApi, fetchUserData as fetchUserData } from '@/services/authService'
import { toast } from 'vue3-toastify'
import ForgotPasswordModal from '@/views/components/ForgotPasswordModal.vue'
import { useLoadingStore } from '@/stores/loadingStore'
const store = useLoadingStore()

const loginCpf = ref('')
const loginPassword = ref('')
const showPassword = ref(false)
const cpfError = ref('')
const router = useRouter()
const forgotPasswordModal = ref<InstanceType<typeof ForgotPasswordModal> | null>(null)

onMounted(() => {
  const msg = localStorage.getItem('loginMessage')
  if (msg) {
    toast.error(msg)
    localStorage.removeItem('loginMessage')
  }
})

const openForgotPasswordModal = () => {
  if (forgotPasswordModal.value) {
    forgotPasswordModal.value.openModal() // Acessando o método openModal do componente filho
  }
}
// Função para formatar CPF
function formatCPF(value: string): string {
  return value
    .replace(/\D/g, '')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d{1,2})$/, '$1-$2')
}

// Watcher para validar CPF
watch(loginCpf, (val) => {
  loginCpf.value = formatCPF(val)
  cpfError.value = !isValidCPF(val) ? 'CPF inválido' : '' // Exibir erro caso CPF seja inválido
})

function isValidCPF(cpf: string): boolean {
  cpf = cpf.replace(/[^\d]+/g, '')
  if (cpf.length !== 11 || /^(\d)\1+$/.test(cpf)) return false

  let sum = 0
  for (let i = 0; i < 9; i++) sum += parseInt(cpf.charAt(i)) * (10 - i)
  let rev = 11 - (sum % 11)
  if (rev === 10 || rev === 11) rev = 0
  if (rev !== parseInt(cpf.charAt(9))) return false

  sum = 0
  for (let i = 0; i < 10; i++) sum += parseInt(cpf.charAt(i)) * (11 - i)
  rev = 11 - (sum % 11)
  if (rev === 10 || rev === 11) rev = 0
  return rev === parseInt(cpf.charAt(10))
}

// Função de login
const handleLogin = async () => {
  if (!loginCpf.value || !loginPassword.value) {
    toast.warning('Preencha todos os campos obrigatórios para login.')
    return
  }

  if (cpfError.value) {
    toast.error('Por favor, verifique o CPF inserido.')
    return
  }

  try {
    loginCpf.value = loginCpf.value.replace(/\D/g, '')
    store.startLoading() // Inicia o loading

    const response = await loginApi(loginCpf.value, loginPassword.value)
    if (response && response.token) {
      localStorage.setItem('authToken', response.token)

      const userData = await fetchUserData()
      toast.success('Login realizado com sucesso!')
      localStorage.setItem('userName', userData.username)
      localStorage.setItem('userPerfil', userData.role.name)
      localStorage.setItem('userId', userData.id)
      store.stopLoading() // Para o loading quando a ação terminar

      window.dispatchEvent(new Event('storage'))
      await router.push({ name: 'ocorrencias' })
      window.location.reload()
    } else {
      store.stopLoading() // Para o loading quando a ação terminar
      toast.error('Erro ao fazer login. Verifique seu CPF e senha.')
    }
  } catch (err) {
    store.stopLoading() // Para o loading quando a ação terminar
    toast.error('Erro ao realizar login.')
  }
}
</script>

<style scoped>
.container {
  display: flex;
  justify-content: center;
  gap: 2rem;
  padding: 2rem;
  background-color: #f9f9f9;
}

.login-container {
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
button {
  width: 100%;
  padding: 0.8rem;
  margin-top: 0.5rem;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  background-color: #42b983;
  color: white;
  cursor: pointer;
}

button:hover {
  background-color: #369f6f;
}

RouterLink {
  display: block;
  text-align: center;
  align-items: center;
  color: #42b983;
  margin-top: 1rem;
}

.center-link {
  display: block;
  text-align: center;
  color: #42b983;
  margin-top: 1rem;
}

.password-group {
  position: relative;
}

.password-wrapper {
  position: relative;
}

.password-wrapper input {
  width: 100%;
  padding-right: 2.5rem; /* espaço pro ícone */
}

.toggle-icon {
  position: absolute;
  right: 0.8rem;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  color: #888;
}

button:disabled {
  background-color: #dcdcdc;
  cursor: not-allowed;
}

.error-message {
  color: red;
  font-size: 0.875rem; /* Tamanho da fonte para a mensagem de erro */
  margin-top: 0.25rem;
}

.forgot-password {
  display: block;
  text-align: right;
  margin-top: 2rem;
  margin-bottom: 1rem;
  color: #42b983;
  font-size: 0.9rem;
  cursor: pointer;
  text-decoration: underline;
}

.forgot-password:hover {
  color: #369f6f;
}
</style>
