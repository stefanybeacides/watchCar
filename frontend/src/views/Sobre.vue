<template lang="pug">
  .sobre
    .card-sobre
      h1 Sobre o Sistema de Denúncia e Ocorrências
      p
        | O Sistema de Denúncia e Ocorrências foi desenvolvido para permitir que cidadãos comuniquem
        | ocorrências policiais e situações de risco à segurança pública. A plataforma integra com dados
        | oficiais do governo e contribui para um controle mais eficiente e transparente.

      p
        | Este sistema permite o registro de ocorrências e denúncias de maneira simples e rápida, com a
        | possibilidade de acompanhamento do status das denúncias realizadas. O objetivo é colaborar com
        | as autoridades na prevenção de crimes e resolução de situações de risco.

      p
        | O sistema também oferece funcionalidades de análise e visualização de dados, garantindo maior
        | eficiência na gestão das ocorrências.
</template>

<script setup lang="ts">
// Nenhuma lógica necessária por enquanto
</script>

<style scoped>
.sobre {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 3rem 1.5rem;
  background-color: #f4f4f4;
  min-height: 100vh;
}

.card-sobre {
  background-color: #ffffff;
  padding: 2.5rem 2rem;
  border-radius: 16px;
  max-width: 850px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease;
  text-align: justify;
  position: relative;
  overflow: hidden;
}

.card-sobre::before {
  content: '';
  position: absolute;
  top: -60px;
  right: -60px;
  width: 120px;
  height: 120px;
  background: #42b98333;
  border-radius: 50%;
  z-index: 0;
}

.card-sobre:hover {
  transform: translateY(-5px);
}

h1 {
  color: #42b983;
  text-align: center;
  margin-bottom: 1.5rem;
  font-size: 2rem;
  position: relative;
  z-index: 1;
}

p {
  color: #444;
  font-size: 1.05rem;
  line-height: 1.7;
  margin-bottom: 1rem;
  position: relative;
  z-index: 1;
}
</style>
