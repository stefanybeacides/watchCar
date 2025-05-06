import './assets/main.css'
import 'vue3-toastify/dist/index.css'
import '@fortawesome/fontawesome-free/css/all.css'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Vue3Toastify from 'vue3-toastify'
import App from './App.vue'
import router from './router'
import 'vue3-toastify/dist/index.css' // Importe o CSS do Toastify
// Importando Font Awesome
import { library } from '@fortawesome/fontawesome-svg-core'
import { faCar } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

// Adicionando o ícone `faCar` à biblioteca
library.add(faCar)
const app = createApp(App)

// Usando o Vue3Toastify globalmente
app.use(Vue3Toastify, {
  autoClose: 4000,
  position: 'top-right',
  theme: 'light',
  pauseOnFocusLoss: false,
  pauseOnHover: false,
})

app.use(createPinia())
app.use(router)
app.component('font-awesome-icon', FontAwesomeIcon)

app.mount('#app')
