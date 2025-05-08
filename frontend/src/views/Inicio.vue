<template lang="pug">
  .inicio
    h1 Bem-vindo ao Sistema de Denúncia de Veículos
    p.subtitulo Registre, acompanhe e contribua com a segurança da sua cidade.

    button(@click="registrarDenuncia" class="btn-denuncia") Registrar Denúncia

    .linha

    h2 Últimas Notícias e Informações
    .informacoes
      .noticias
        .noticia(v-for="(noticia, index) in noticias" :key="index")
          .noticia-card
            img(:src="noticia.img" alt="Notícia" class="noticia-img")
            p.texto {{ noticia.texto }}

    .linha

    .carrossel
      h2 Artigos e Dicas de Segurança
      .swiper-container
        .swiper-wrapper
          .swiper-slide
            .artigo
              img(src="https://www.smartia.com.br/wp-content/uploads/2019/05/seguro-auto-1.jpg" alt="Artigo 1")
              h3 Como evitar roubos de veículos
              p Mantenha seu carro em locais iluminados, instale alarmes e rastreadores, e evite deixar objetos de valor à vista para reduzir o risco de furtos.

          .swiper-slide
            .artigo
              img(src="https://www.urbanparkgru.com.br/dicas-de-viagem/wp-content/uploads/2018/05/cinco-orientacoes-para-estacionar-com-seguranca.jpg" alt="Artigo 2")
              h3 Dicas de segurança para estacionar
              p Sempre estacione em locais movimentados, prefira vagas próximas a câmeras de vigilância e evite deixar o carro por longos períodos em áreas desertas.


    .linha

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
    img: 'https://backend.intelbras.com/sites/default/files/inline-images/Captura%20de%20tela%202023-01-11%20141306.png',
    texto: 'Sistema de segurança em área pública está sendo aprimorado.',
  },
  {
    img: 'https://fotos-jornaldocarro-estadao.nyc3.cdn.digitaloceanspaces.com/uploads/2019/03/12130947/car-1590508_1920-1160x773.jpg',
    texto: 'Aumento de ocorrências em regiões específicas, redobrar atenção.',
  },
  {
    img: 'https://terrabrasilnoticias.com/wp-content/uploads/2024/09/cyber-fraud_1725782281751.jpg',
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
  background-color: #f9f9f9;
  min-height: 100vh;
  color: #333;
  font-family: 'Roboto', sans-serif;
  box-sizing: border-box;
}

h1 {
  font-size: 3rem;
  margin-bottom: 0.5rem;
  color: #222;
}

h2 {
  font-size: 2rem;
  margin: 1.5rem 0 0.5rem;
  color: #2c3e50;
}

.subtitulo {
  font-size: 1.2rem;
  margin-bottom: 2rem;
  color: #555;
}

/* Botão de Registrar Denúncia */
.btn-denuncia {
  margin: 2rem auto;
  background: #e74c3c;
  color: white;
  padding: 1rem 2.5rem;
  border: none;
  font-size: 1.4rem;
  font-weight: bold;
  border-radius: 8px;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(231, 76, 60, 0.4);
  transition: all 0.3s ease;
  display: inline-block;
}

.btn-denuncia:hover {
  background: #c0392b;
  box-shadow: 0 6px 18px rgba(231, 76, 60, 0.6);
  transform: translateY(-3px);
}

/* Linha divisória */
.linha {
  height: 1px;
  background-color: #ddd;
  margin: 3rem 0 2rem;
}

/* Seção de Notícias */
.noticias {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 1.5rem;
  margin-bottom: 3rem;
}

.noticia-card {
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  width: 250px;
  transition: transform 0.3s ease;
}

.noticia-card:hover {
  transform: translateY(-6px);
}

.noticia-img {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.texto {
  padding: 1rem;
  font-size: 1rem;
  color: #333;
}

/* Carrossel de Artigos */
.carrossel {
  margin-bottom: 3rem;
}

.swiper-container {
  width: 100%;
  padding: 1rem 0;
  display: flex;
  justify-content: center;
}

.swiper-wrapper {
  display: flex;
  gap: 1.5rem;
  overflow-x: auto;
}

.swiper-slide {
  flex: 0 0 auto;
}

.artigo {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 300px;
  text-align: center;
  transition: transform 0.3s ease;
}

.artigo:hover {
  transform: translateY(-6px);
}

.artigo img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.artigo h3 {
  font-size: 1.2rem;
  margin: 1rem 0 0.5rem;
  color: #222;
}

.artigo p {
  padding: 0 1rem 1rem;
  font-size: 0.95rem;
  color: #555;
}

/* Vídeo explicativo */
.video {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 3rem;
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
}

.video iframe {
  width: 100%;
  height: 400px;
  border-radius: 8px;
}

/* Responsividade */
@media (max-width: 768px) {
  .noticia-card,
  .artigo {
    width: 90%;
  }

  .video iframe {
    height: 250px;
  }

  h1 {
    font-size: 2.2rem;
  }

  h2 {
    font-size: 1.6rem;
  }

  .btn-denuncia {
    width: 90%;
    font-size: 1.2rem;
  }
}
</style>
