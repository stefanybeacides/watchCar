<template lang="pug">
  section.container
    div.register-container
      h2 Cadastro
      form(@submit.prevent="handleRegister")
    
        div.input-group
          label Tipo de Usuário
          div.radio-buttons
            label(for="publico")
              input(type="radio" id="publico" :value="1" v-model="userType")
              | Cidadao
            label(for="policial")
              input(type="radio" id="policial" :value="2" v-model="userType")
              | Policial
            label(for="AgenteDeSeguranca")
              input(type="radio" id="publico" :value="3" v-model="userType")
              | Agente de Seguranca
            label(for="investigador")
              input(type="radio" id="publico" :value="4" v-model="userType")
              | Investigador
            label(for="gestorDeSegurancaPublica")
              input(type="radio" id="publico" :value="5" v-model="userType")
              | Gestor de Seguranca Publica

        div.input-group
          label(for="name") Nome Completo
          input(type="text" id="name" v-model="name" required)
          span.error-message(v-if="nameError") {{ nameError }}

        div.input-group
          label(for="cpf") CPF
          input(type="text" id="cpf" v-model="cpf" maxlength="14" required)
          span.error-message(v-if="cpfError") {{ cpfError }}

        div.input-group
          label(for="registerEmail") E-mail
          input(type="email" id="registerEmail" v-model="email" required)
          span.error-message(v-if="emailError") {{ emailError }}

        div.input-group.password-group
          label(for="registerPassword") Senha
          div.password-wrapper
            input(
              :type="showRegisterPassword ? 'text' : 'password'"
              id="registerPassword"
              v-model="password"
              required
            )
            span.toggle-icon(@click="showRegisterPassword = !showRegisterPassword")
              i(:class="showRegisterPassword ? 'fas fa-eye-slash' : 'fas fa-eye'")

        div.input-group.password-group
          label(for="confirmPassword") Confirmar Senha
          div.password-wrapper
            input(
              :type="showConfirmPassword ? 'text' : 'password'"
              id="confirmPassword"
              v-model="confirmPassword"
              required
            )
            span.toggle-icon(@click="showConfirmPassword = !showConfirmPassword")
              i(:class="showConfirmPassword ? 'fas fa-eye-slash' : 'fas fa-eye'")

        // Exibe a mensagem de erro se as senhas não coincidirem
        span.error-message(v-if="confirmPasswordError") {{ confirmPasswordError }}

        div.input-group(v-if="isPolicialOuSimilar")
          label(for="delegate") Delegacia
          input(type="text" id="delegate" v-model="delegate" required)
          span.error-message(v-if="delegateError") {{ delegateError }}

        div.input-group(v-if="isPolicialOuSimilar")
          label(for="badge") Distintivo
          input(type="text" id="badge" v-model="badge" required)
          span.error-message(v-if="badgeError") {{ badgeError }}

        div.input-group(v-if="isPolicialOuSimilar")
          label(for="ra") RA (Registro de Atividade)
          input(type="text" id="ra" v-model="ra" required)
          span.error-message(v-if="raError") {{ raError }}

        div.input-group(v-if="isGestor")
          label(for="departamento") Departamento
          input(type="text" id="departamento" v-model="departamento" required)
          span.error-message(v-if="departamentoError") {{ departamentoError }}

        div.input-group(v-if="isGestor")
          label(for="cargo") Cargo
          input(type="text" id="cargo" v-model="cargo" required)
          span.error-message(v-if="cargoError") {{ cargoError }}
  

        button(
          type="submit"
          :disabled="!isFormValid") Criar Conta

      RouterLink.center-link(to="/login") Já tem conta? Faça Login
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { register as registerApi } from '@/services/authService'
import { toast } from 'vue3-toastify'
import { useLoadingStore } from '@/stores/loadingStore'
const store = useLoadingStore()
const router = useRouter()

// Dados do formulário
const name = ref('')
const cpf = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const userType = ref(1) // 1 = Público, 2 = Policial
const delegate = ref('')
const badge = ref('')
const ra = ref('')
const showRegisterPassword = ref(false)
const showConfirmPassword = ref(false)
const isPolicialOuSimilar = computed(() => [2, 3, 4].includes(userType.value))
const isGestor = computed(() => userType.value === 5)

// Erros
const nameError = ref('')
const cpfError = ref('')
const emailError = ref('')
const confirmPasswordError = ref('')
const delegateError = ref('')
const badgeError = ref('')
const raError = ref('')
const departamento = ref('')
const cargo = ref('')
const departamentoError = ref('')
const cargoError = ref('')

// Computed property para verificar se é Policial
const isPolicial = computed(() => userType.value === 2)

// Computed property para verificar se o formulário está válido
const isFormValid = computed(() => {
  const camposBasicosValidos =
    !nameError.value &&
    !cpfError.value &&
    !emailError.value &&
    !confirmPasswordError.value &&
    name.value &&
    cpf.value &&
    email.value &&
    password.value &&
    confirmPassword.value &&
    password.value === confirmPassword.value

  if (isPolicialOuSimilar.value) {
    return (
      camposBasicosValidos &&
      !delegateError.value &&
      !badgeError.value &&
      !raError.value &&
      delegate.value &&
      badge.value &&
      ra.value
    )
  }

  if (isGestor.value) {
    return (
      camposBasicosValidos &&
      !departamentoError.value &&
      !cargoError.value &&
      departamento.value &&
      cargo.value
    )
  }

  return camposBasicosValidos
})

// Watchers para validar CPF, Email, e Senhas
watch(cpf, (val) => {
  cpf.value = formatCPF(val)
  cpfError.value = !isValidCPF(val) ? 'CPF inválido' : ''
})

watch(email, (val) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  emailError.value = !emailRegex.test(val) ? 'E-mail inválido' : ''
})

watch([password, confirmPassword], ([pass, confirm]) => {
  confirmPasswordError.value = confirm && confirm !== pass ? 'Senhas não coincidem' : ''
})

// Função para formatar CPF
function formatCPF(value: string): string {
  return value
    .replace(/\D/g, '')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d{1,2})$/, '$1-$2')
}

// Função para validar CPF
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

// Função de registro
const handleRegister = async () => {
  if (!name.value || !cpf.value || !email.value || !password.value || !confirmPassword.value) {
    toast.error('Por favor, preencha todos os campos obrigatórios.')
    return
  }

  if (password.value !== confirmPassword.value) {
    toast.error('As senhas não coincidem.')
    return
  }

  if (isPolicial.value && (!delegate.value || !badge.value || !ra.value)) {
    toast.error('Preencha os campos adicionais para policial.')
    return
  }

  if (isGestor.value && (!departamento.value || !cargo.value)) {
    toast.error('Preencha os campos adicionais para Gestor de Segurança Pública.')
    return
  }

  try {
    store.startLoading() // Inicia o loading
    const response = await registerApi(
      name.value,
      password.value,
      email.value,
      cpf.value.replace(/\D/g, ''), // Enviar CPF sem formatação
      userType.value,
      // Campos adicionais dependendo do tipo de usuário
      isPolicialOuSimilar.value ? delegate.value : undefined,
      isPolicialOuSimilar.value ? badge.value : undefined,
      isPolicialOuSimilar.value ? ra.value : undefined,
      isGestor.value ? departamento.value : undefined,
      isGestor.value ? cargo.value : undefined,
    )
    if (response && response.success) {
      toast.success('Cadastro realizado com sucesso!')
      store.stopLoading() // Para o loading quando a ação terminar
      await router.push('/login')
    }
  } catch (err) {
    store.stopLoading() // Para o loading quando a ação terminar
    toast.error('Erro ao cadastrar usuário.')
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

.register-container {
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 2rem;
  width: 100%;
  max-width: 100%;
}

h2 {
  text-align: center;
  color: #333;
  margin-bottom: 1.5rem;
}

.input-group {
  margin-bottom: 1rem;
}

.radio-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
  margin-top: 10px;
}

.radio-buttons label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  font-weight: 500;
}

.radio-buttons input[type='radio'] {
  width: 16px; /* Reduzir para um tamanho mais padrão */
  height: 16px; /* Reduzir para um tamanho mais padrão */
  margin: 0; /* Remover qualquer margem extra */
  border-radius: 50%; /* Manter a forma redonda */
  outline: none;
  cursor: pointer;
  transition: all 0.2s ease;
}

.radio-buttons input[type='radio']:checked {
  background-color: #42b983; /* Cor de fundo quando selecionado */
  border-color: #42b983; /* Cor de borda quando selecionado */
}

.radio-buttons input[type='radio']:checked::after {
  content: ''; /* Cria o círculo interno */
  display: block;
  width: 8px; /* Tamanho do círculo interno */
  height: 8px; /* Tamanho do círculo interno */
  background-color: #42b983; /* Cor do círculo interno */
  border-radius: 50%; /* Forma arredondada */
  margin: 4px; /* Centraliza o círculo interno dentro do botão de rádio */
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
  color: #42b983;
  margin-top: 5px;
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
  font-size: 0.875rem;
  margin-top: 0.25rem;
}

.input-group {
  margin-bottom: 1rem;
}

button:disabled {
  background-color: #dcdcdc;
  cursor: not-allowed;
}

.center-link {
  display: block;
  text-align: center;
  color: #42b983;
  margin-top: 1rem;
}
</style>
