import { createRouter, createWebHistory } from 'vue-router'
import Cadastro from '../views/Cadastro.vue'
import Login from '../views/Login.vue'
import Inicio from '../views/Inicio.vue'
import Sobre from '../views/Sobre.vue'
import Ocorrencias from '../views/Ocorrencias.vue'
import Denuncia from '../views/Denuncia.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'inicio', // Redefine a rota inicial como 'inicio'
      component: Inicio, // A primeira tela que o usuário verá será a 'Inicio.vue'
    },
    {
      path: '/login',
      name: 'login', // A página de login/cadastro
      component: Login,
    },
    {
      path: '/register',
      name: 'cadastro', // A página de login/cadastro
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

export default router
