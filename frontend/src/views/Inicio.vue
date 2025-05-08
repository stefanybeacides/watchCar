<template lang="pug">
  .inicio
    h1 Bem-vindo ao Sistema de Denúncia de Veículos

    // Seção de Notícias e Informações
    .informacoes
      .noticias
        .noticia(v-for="(noticia, index) in noticias" :key="index")
          .noticia-card
            img(:src="noticia.img" alt="Notícia" class="noticia-img")
            p.texto {{ noticia.texto }}

    // Carrossel de Artigos
    .carrossel
      h2 Artigos e Dicas de Segurança
      .swiper-container
        .swiper-wrapper
          .swiper-slide
            .artigo
              img(src="https://via.placeholder.com/300x200" alt="Artigo 1")
              h3 Como evitar roubos de veículos
              p Breve descrição do artigo.
          .swiper-slide
            .artigo
              img(src="https://via.placeholder.com/300x200" alt="Artigo 2")
              h3 Dicas de segurança para estacionar
              p Breve descrição do artigo.

    // Vídeo explicativo
    .video
      h2 Como registrar uma denúncia
      iframe(width="560" height="315" src="https://www.youtube.com/embed/3x5-XHEy8JE?si=ecm_B1LC-nD4xJ9l" title="Como registrar uma denúncia" frameborder="0" allowfullscreen)

    // Botão de Registrar Denúncia
    button(@click="registrarDenuncia" class="btn-denuncia") Registrar Denúncia
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const noticias = ref([
  {
    img: 'https://via.placeholder.com/300x200',
    texto: 'Sistema de segurança em área pública está sendo aprimorado.',
  },
  {
    img: 'https://via.placeholder.com/300x200',
    texto: 'Aumento de ocorrências em regiões específicas, redobrar atenção.',
  },
  {
    img: 'https://via.placeholder.com/300x200',
    texto: 'Polícia alerta sobre novas formas de fraude digital.',
  },
])

const registrarDenuncia = () => {
  router.push({ name: 'denuncia' })
}
</script>

<style scoped>
/* Estilo geral */
.inicio {
  text-align: center;
  padding: 3rem 2rem;
  background: linear-gradient(135deg, #1e1e2f, #3a3a6a);
  min-height: 100vh;
  color: #fff;
  font-family: 'Roboto', sans-serif;
  box-sizing: border-box;
}

h1 {
  font-size: 2.8rem;
  margin-bottom: 2rem;
  text-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
}

h2 {
  font-size: 2rem;
  margin: 1.5rem 0;
  color: #42b983;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

/* Seção de Notícias */
.informacoes {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  margin-bottom: 3rem;
}

.noticias {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.noticia-card {
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5);
  overflow: hidden;
  width: 250px;
  backdrop-filter: blur(10px);
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
}

.noticia-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.5);
}

.noticia-img {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-bottom: 2px solid #fff;
}

.texto {
  padding: 1rem;
  font-size: 1rem;
  line-height: 1.5;
  color: #eaeaea;
}

/* Carrossel de Artigos */
.carrossel {
  margin-bottom: 3rem;
}

.swiper-container {
  width: 100%;
  padding-bottom: 2rem;
  display: flex;
  justify-content: center;
  align-items: center;
}

.swiper-wrapper {
  display: flex;
  gap: 1.5rem;
}

.swiper-slide {
  display: flex;
  justify-content: center;
  align-items: center;
}

.artigo {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5);
  overflow: hidden;
  width: 300px;
  backdrop-filter: blur(10px);
  text-align: center;
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
}

.artigo:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.5);
}

.artigo img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.artigo h3 {
  font-size: 1.2rem;
  margin: 1rem 0;
  color: #fff;
}

.artigo p {
  padding: 0 1rem 1rem;
  font-size: 0.9rem;
  color: #ddd;
}

/* Vídeo explicativo */
.video {
  background: rgba(255, 255, 255, 0.1);
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(10px);
  margin-bottom: 3rem;
}

.video iframe {
  border-radius: 8px;
}

/* Botão de Registrar Denúncia */
.btn-denuncia {
  margin-top: 2rem;
  background: linear-gradient(135deg, #42b983, #34a74f);
  color: white;
  padding: 1rem 2.5rem;
  border: none;
  font-size: 1.4rem;
  font-weight: bold;
  border-radius: 50px;
  cursor: pointer;
  box-shadow: 0 8px 16px rgba(66, 185, 131, 0.4);
  transition: all 0.3s ease;
}

.btn-denuncia:hover {
  background: linear-gradient(135deg, #34a74f, #2e9d65);
  box-shadow: 0 12px 24px rgba(66, 185, 131, 0.6);
  transform: translateY(-3px);
}

/* Scrollbar estilizado */
::-webkit-scrollbar {
  width: 10px;
}

::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
}

::-webkit-scrollbar-thumb {
  background: #42b983;
  border-radius: 10px;
}

::-webkit-scrollbar-thumb:hover {
  background: #34a74f;
}

/* Responsividade */
@media (max-width: 768px) {
  .noticias,
  .swiper-container {
    flex-direction: column;
    align-items: center;
  }

  .noticia-card,
  .artigo {
    width: 90%;
  }

  iframe {
    width: 100%;
    height: auto;
  }

  h1 {
    font-size: 2.2rem;
  }

  h2 {
    font-size: 1.8rem;
  }

  .btn-denuncia {
    width: 90%;
    font-size: 1.2rem;
  }
}
</style>
