import { createRouter, createWebHistory } from 'vue-router'
import LoginCadastro from '../views/LoginCadastro.vue'
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
      name: 'loginCadastro', // A página de login/cadastro
      component: LoginCadastro,
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

export default router
