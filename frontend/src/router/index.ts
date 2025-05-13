import { createRouter, createWebHistory } from 'vue-router'
import Cadastro from '../views/Cadastro.vue'
import Login from '../views/Login.vue'
import Inicio from '../views/Inicio.vue'
import Sobre from '../views/Sobre.vue'
import Ocorrencias from '../views/Ocorrencias.vue'
import Denuncia from '../views/Denuncia.vue'
import PasswordResetModal from '@/views/components/PasswordResetModal.vue'
import { fetchUserData } from '@/services/authService'
import MeusDados from '../views/MeusDados.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'inicio',
      component: Inicio,
    },
    {
      path: '/login',
      name: 'login',
      component: Login,
    },
    {
      path: '/register',
      name: 'cadastro',
      component: Cadastro,
    },
    {
      path: '/sobre',
      name: 'sobre',
      component: Sobre,
    },
    {
      path: '/ocorrencias',
      name: 'ocorrencias',
      component: Ocorrencias,
    },
    {
      path: '/denuncia',
      name: 'denuncia',
      component: Denuncia,
    },
    {
      path: '/redefinir-senha',
      name: 'PasswordReset',
      component: PasswordResetModal,
    },
    {
      path: '/meus-dados',
      name: 'meus-dados',
      component: MeusDados,
      meta: { requiresAuth: true },
    },
  ],
})
router.beforeEach((to, from, next) => {
  const requiresAuth = to.meta.requiresAuth
  const token = localStorage.getItem('authToken')

  if (requiresAuth && !token) {
    return next({ name: 'login' })
  }

  next()
})

router.beforeEach(async (to, from, next) => {
  const token = localStorage.getItem('authToken')

  if (token) {
    try {
      const userData = await fetchUserData() // Atualiza userName, userPerfil, userId
      localStorage.setItem('userName', userData.username)
      localStorage.setItem('userPerfil', userData.role.name)
      localStorage.setItem('userId', userData.id)
      window.dispatchEvent(new Event('storage'))
    } catch (err) {
      console.error('Erro ao buscar dados do usuário', err)
      // Token inválido? Limpa e redireciona:
      localStorage.clear()
      return next('/login')
    }
  }

  next()
})

export default router
