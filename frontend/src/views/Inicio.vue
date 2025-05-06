<template>
  <div class="inicio">
    <h1>Bem-vindo ao Sistema de Denúncia</h1>

    <!-- Seção de Notícias e Informações -->
    <div class="informacoes">
      <div class="noticias">
        <div class="noticia" v-for="(noticia, index) in noticias" :key="index">
          <div class="noticia-card">
            <img :src="noticia.img" alt="Notícia" class="noticia-img" />
            <p class="texto">{{ noticia.texto }}</p>
          </div>
        </div>
      </div>

      <!-- Artigos e Links úteis -->
      <div class="links-uteis">
        <h2>Artigos e Links Úteis</h2>
        <ul>
          <li>
            <a href="https://www.policiacivil.sp.gov.br/" target="_blank">Polícia Civil - SP</a>
          </li>
          <li>
            <a href="https://www.gov.br/pt-br/temas/cidadania" target="_blank"
              >Como fazer uma denúncia</a
            >
          </li>
          <li>
            <a href="https://www.cartorios.org/denuncias-anonimas" target="_blank"
              >Denúncias Anônimas</a
            >
          </li>
        </ul>
      </div>

      <!-- Vídeo explicativo -->
      <div class="video">
        <h2>Como registrar uma denúncia</h2>
        <iframe
          width="560"
          height="315"
          src="https://www.youtube.com/embed/3x5-XHEy8JE?si=ecm_B1LC-nD4xJ9l"
          title="YouTube video player"
          frameborder="0"
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
          referrerpolicy="strict-origin-when-cross-origin"
          allowfullscreen
        ></iframe>
      </div>
    </div>

    <!-- Botão de Registrar Denúncia -->
    <button @click="registrarDenuncia" class="btn-denuncia">Registrar Denúncia</button>
  </div>
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
    img: 'https://s2-g1.glbimg.com/LhcpQMDoCmw-Iv8UawMDbC4zGlg=/1327x0/https://i.s3.glbimg.com/v1/AUTH_59edd422c0c84a879bd37670ae4f538a/internal_photos/bs/2024/P/U/m1PAS7StCOktK7gRkkLw/chuvas.png',
    texto: 'Aumento de ocorrências em regiões específicas, redobrar atenção.',
  },
  {
    img: 'https://cdn.folhadomate.com/wp-content/uploads/2020/06/golpe-696x762.jpg',
    texto: 'Polícia alerta sobre novas formas de fraude digital.',
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
  background-color: #f4f4f4;
  min-height: 100vh;
}

h1 {
  font-size: 2rem;
  color: #333;
  margin-bottom: 2rem;
  font-weight: bold;
}

.informacoes {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  margin-bottom: 2rem;
}

.noticias {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-around;
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.noticia-card {
  display: flex;
  flex-direction: column;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  width: 250px;
  transition: transform 0.3s ease;
}

.noticia-card:hover {
  transform: translateY(-5px);
}

.noticia-img {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.texto {
  padding: 0.5rem;
  font-size: 1rem;
  color: #333;
}

.links-uteis {
  background-color: #ffffff;
  padding: 1rem;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.links-uteis h2 {
  font-size: 1.5rem;
  margin-bottom: 1rem;
}

.links-uteis ul {
  list-style: none;
  padding: 0;
}

.links-uteis li {
  margin-bottom: 1rem;
}

.links-uteis a {
  text-decoration: none;
  color: #42b983;
  font-size: 1.1rem;
}

.links-uteis a:hover {
  text-decoration: underline;
}

.video {
  background-color: #fff;
  padding: 1rem;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.btn-denuncia {
  margin-top: 2rem;
  background-color: #42b983;
  color: white;
  padding: 0.8rem 1.5rem;
  border: none;
  cursor: pointer;
  font-size: 1.2rem;
  border-radius: 6px;
}

.btn-denuncia:hover {
  background-color: #34a74f;
}
</style>
