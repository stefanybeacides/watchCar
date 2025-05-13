<script setup lang="ts">
import { ref, computed, watchEffect, onMounted, onUnmounted } from 'vue'
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { toast } from 'vue3-toastify'

const router = useRouter()

// Ref reativa que controla o estado de autenticação
const authToken = ref(localStorage.getItem('authToken'))
const userName = ref(localStorage.getItem('userName') || 'Usuário')
const perfilUsuario = ref('PUBLICO')

onMounted(() => {
  window.addEventListener('storage', updateUserData)
})

onUnmounted(() => {
  window.removeEventListener('storage', updateUserData)
})

function updateUserData() {
  authToken.value = localStorage.getItem('authToken')
  userName.value = localStorage.getItem('userName') || 'Usuário'
  perfilUsuario.value = localStorage.getItem('userPerfil') || 'PUBLICO'
}

const roleMap: Record<string, string> = {
  PUBLICO: 'Público',
  POLICIAL: 'Policial',
  AGENTE_DE_SEGURANCA: 'Agente de Segurança',
  INVESTIGADOR: 'Investigador',
  GESTOR_DE_SEGURANCA_PUBLICA: 'Gestor de Segurança Pública',
}

const perfilUsuarioFormatado = computed(() => {
  return roleMap[perfilUsuario.value] || perfilUsuario.value
})

// Computed reativo para login
const isLoggedIn = computed(() => authToken.value !== null)

// Sempre que o token mudar, salvar no localStorage
watchEffect(() => {
  if (authToken.value) {
    localStorage.setItem('authToken', authToken.value)
  } else {
    localStorage.removeItem('authToken')
  }
})

// Mesmo para o nome
watchEffect(() => {
  if (userName.value && userName.value !== 'Usuário') {
    localStorage.setItem('userName', userName.value)
  } else {
    localStorage.removeItem('userName')
  }
})

// Função de logout
const handleLogout = () => {
  authToken.value = null
  userName.value = 'Usuário'
  router.push({ name: 'login' })
}

window.addEventListener('storage', () => {
  authToken.value = localStorage.getItem('authToken')
  userName.value = localStorage.getItem('userName') || 'Usuário'
})

const menuAberto = ref(false)

const toggleMenu = () => {
  menuAberto.value = !menuAberto.value
}

const fecharMenu = () => {
  menuAberto.value = false
}
</script>

<template lang="pug">
div.layout
  div.alerta-sistema
    p Atenção: Este sistema é um projeto de testes e 
      strong não representa uma plataforma oficial de denúncias
      | . As informações aqui inseridas são fictícias e utilizadas exclusivamente para fins acadêmicos e demonstrativos.

  header.navbar
    .container
      // Logo
      img.logo(src="@/assets/logo.svg", alt="Vue logo", width="60", height="60")

      // Botão de abrir/fechar menu (visível no mobile)
      button.menu-toggle(@click="toggleMenu")
        i.fas(:class="menuAberto ? 'fa-times' : 'fa-bars'")

      // Menu completo (condicional no mobile)
      nav.nav-content(:class="{ aberto: menuAberto }")
        nav.nav-menu
          RouterLink.nav-link(to="/" @click="fecharMenu") Início
          RouterLink.nav-link(to="/sobre" @click="fecharMenu") Sobre
          RouterLink.nav-link(to="/ocorrencias" @click="fecharMenu") Ocorrências
          RouterLink.nav-link(to="/denuncia" @click="fecharMenu") Denúncia

        nav.nav-buttons
          div(v-if="!isLoggedIn")
            RouterLink.nav-button(to="/login" @click="fecharMenu") Login
            RouterLink.nav-button.primary.ml(to="/register" @click="fecharMenu") Cadastre-se
        div(v-if="isLoggedIn" class="nav-user-wrapper")
          div.nav-user-info
            span.nav-user-name {{ userName }}
            div.nav-user-role {{ perfilUsuarioFormatado }}
            RouterLink.nav-button.small-button.ml(to="/meus-dados" @click="fecharMenu") Meus Dados
          button.nav-button.primary(@click="() => { handleLogout(); fecharMenu() }") Sair



  main.main-content
    RouterView
    Loader  <!-- Componente de Loader aqui -->
    ToastContainer  <!-- Adicionado ToastContainer -->

  footer.footer
    p © 2025 - Todos os direitos reservados
</template>

<style scoped>
/* Garante que o layout ocupe 100% da altura da tela */
html,
body {
  height: 100%;
  margin: 0;
  padding: 0;
}

.nav-menu {
  display: flex;
  gap: 2rem;
  justify-content: center;
  align-items: center;
  flex: 1;
}

.nav-link {
  text-decoration: none;
  color: #333;
  font-weight: 500;
  padding: 0.5rem;
  transition: color 0.2s;
}

.nav-link:hover {
  color: #42b983;
}

.nav-buttons {
  display: flex;
  gap: 4rem;
}

.nav-user-name {
  color: #333;
  font-weight: 500;
  margin-right: 1rem;
}

.nav-button {
  text-decoration: none;
  padding: 0.5rem 1rem;
  border: 1px solid #666;
  border-radius: 4px;
  color: #333;
  transition: background-color 0.2s;
}

.nav-button:hover {
  background-color: #f0f0f0;
}

.nav-button.primary {
  background-color: #42b983;
  color: white;
  border-color: #42b983;
}

.nav-button.primary:hover {
  background-color: #369f6f;
}

/* Layout de toda a aplicação */
.layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh; /* Garantir que o layout ocupe toda a altura da tela */
  background-color: #ffffff;
  font-family: 'Segoe UI', sans-serif;
}

/* Menu completo */
.navbar {
  background-color: #ffffff;
  border-bottom: 1px solid #ddd;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%; /* Garantir que ocupe toda a largura */
}

/* Logo */
.logo {
  height: 60px;
}

/* Links de navegação */
.nav-links {
  display: flex;
  gap: 1rem;
}

.nav-button {
  text-decoration: none;
  padding: 0.5rem 1rem;
  border: 1px solid #666;
  border-radius: 4px;
  color: #333;
  transition: background-color 0.2s;
}

.nav-button:hover {
  background-color: #f0f0f0;
}

.nav-button.primary {
  background-color: #42b983;
  color: white;
  border-color: #42b983;
}

.nav-button.primary:hover {
  background-color: #369f6f;
}

/* Conteúdo principal */
.main-content {
  flex: 1; /* Garante que o conteúdo ocupe todo o espaço disponível */
  padding: 2rem;
  background-color: #ffffff;
  width: 100%; /* Garantir que ocupe toda a largura */
  box-sizing: border-box;
}

/* Rodapé ocupa 100% */
.footer {
  background-color: #ffffff;
  border-top: 1px solid #ddd;
  padding: 1.5rem 2rem;
  text-align: center;
  font-size: 0.9rem;
  color: #555;
  width: 100%; /* Garantir que ocupe toda a largura */
}

/* Container para centralizar o conteúdo dentro do header */
.container {
  display: flex;
  align-items: center;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
}

/* Alinhar o conteúdo do menu principal entre logo e botões */
.nav-content {
  display: flex;
  align-items: center;
  flex: 1;
  justify-content: center;
  gap: 2rem;
}

/* Faz os botões ficarem à direita */
.nav-buttons {
  margin-left: auto;
  display: flex;
  gap: 1rem;
}

.nav-user-role {
  font-size: 0.75rem; /* menor que o nome */
  color: #888; /* cinza */
  margin-top: -0.25rem;
}
.nav-user-wrapper {
  display: flex;
  align-items: center;
  gap: 1rem; /* espaço entre info e botão */
}

.nav-user-info {
  display: flex;
  align-items: center; /* Centraliza tudo */
  flex-direction: column;
}

.nav-user-name {
  font-weight: bold;
}

.nav-user-role {
  font-size: 0.75rem;
  color: #888;
  margin-top: 0.2rem;
  text-align: center;
}

@media (max-width: 768px) {
  .navbar {
    flex-direction: column;
    align-items: flex-start;
    padding: 1rem;
  }

  .container {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .nav-menu {
    flex-direction: column;
    width: 100%;
    gap: 1rem;
  }

  .nav-buttons {
    flex-direction: column;
    width: 100%;
    gap: 0.5rem;
  }

  .nav-link,
  .nav-button {
    width: 100%;
    text-align: left;
  }

  .nav-user-wrapper {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.25rem;
  }

  .main-content {
    padding: 1rem;
  }

  .footer {
    font-size: 0.8rem;
    padding: 1rem;
    text-align: center;
  }
}

.menu-toggle {
  display: none;
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #333;
}

.nav-content {
  display: flex;
  gap: 2rem;
  align-items: center;
}

@media (max-width: 768px) {
  .menu-toggle {
    display: block;
  }

  .nav-content {
    display: none;
    flex-direction: column;
    width: 100%;
    gap: 1rem;
    margin-top: 1rem;
  }

  .nav-content.aberto {
    display: flex;
  }

  .nav-menu,
  .nav-buttons {
    width: 100%;
    flex-direction: column;
    align-items: flex-start;
  }

  .nav-link,
  .nav-button {
    width: 100%;
    text-align: left;
  }
}

.alerta-sistema {
  background-color: #ffe5e5;
  color: #a94442;
  padding: 0.5rem 1rem;
  text-align: center;
  font-size: 0.85rem;
  border-bottom: 1px solid #e0b4b4;
}

.nav-button.ml {
  margin-left: 0.5rem;
}

.small-button {
  padding: 0.25rem 0.5rem;
  font-size: 0.75rem;
  line-height: 1.2;
  border: 1px solid #42b983;
  color: #42b983;
  background-color: white;
  border-radius: 4px;
  text-align: center;
  margin-top: 4%;
  transition:
    background-color 0.2s,
    color 0.2s;
  display: inline-block;
  font-weight: 500;
  cursor: pointer;
}

.small-button:hover {
  background-color: #42b983;
  color: white;
}
</style>
