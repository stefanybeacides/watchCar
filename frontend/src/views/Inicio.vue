<template>
  <div class="inicio">
    <h1>Bem-vindo ao Sistema de Denúncia</h1>
    <div class="noticias">
      <div class="noticia" v-for="(noticia, index) in noticias" :key="index">
        <img :src="noticia.img" alt="Notícia" class="noticia-img" />
        <p>{{ noticia.texto }}</p>
      </div>
    </div>
    <button @click="registrarDenuncia" class="btn-denuncia">Registrar Denúncia</button>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const noticias = ref([
  {
    img: 'https://via.placeholder.com/150',
    texto: 'Notícia 1: Sistema de segurança em área pública.',
  },
  {
    img: 'https://via.placeholder.com/150',
    texto: 'Notícia 2: Aumento de ocorrências em regiões específicas.',
  },
])

const registrarDenuncia = () => {
  router.push({ name: 'denuncia' })
}
</script>

<style scoped>
.inicio {
  text-align: center;
  padding: 2rem;
}

.noticias {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.noticia-img {
  width: 100%;
  height: auto;
}

.btn-denuncia {
  margin-top: 2rem;
  background-color: #42b983;
  color: white;
  padding: 0.8rem 1.5rem;
  border: none;
  cursor: pointer;
}
</style>
