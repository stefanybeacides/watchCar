<template lang="pug">
  .denuncia
    h1 Registrar Denúncia de Roubo ou Furto de Veículo

    // Linha do tempo
    .timeline
      .step(:class="{ active: etapa === 1, completed: etapa > 1 }")
        i.fas.fa-user
        | Dados Pessoais
      .step(:class="{ active: etapa === 2, completed: etapa > 2 }")
        i.fas.fa-map-marker-alt
        | Local
      .step(:class="{ active: etapa === 3, completed: etapa > 3 }")
        i.fas.fa-car
        | Veículo
      .step(:class="{ active: etapa === 4, completed: etapa > 4 }")
        i.fas.fa-comment
        | Descrição
      .step(:class="{ active: etapa === 5, completed: etapa > 5 }")
        i.fas.fa-check-circle
        | Finalizar


    .line
    form(@submit.prevent="enviarDenuncia")
      template(v-if="etapa === 1")
        .step-content(:class="{'active-step': etapa === 1}")
          .input-anonimo
            label(for="anonimo") Denunciar de forma anônima
            .anonimo-checkbox
              input(type="checkbox" id="anonimo" v-model="anonimo" @change="toggleAnonimo")

          // Exibe dados pessoais apenas se não for anônimo
          .input-group(v-if="!anonimo")
            label(for="username") Nome
            input(type="text" id="username" v-model="usuario.username" :disabled="anonimo || (usuarioLogado && !anonimo)" :readonly="usuarioLogado")

          .input-group(v-if="!anonimo")
            label(for="cpf") CPF
            input(type="text" id="cpf" v-model="usuario.cpf" :disabled="anonimo || (usuarioLogado && !anonimo)" :readonly="usuarioLogado")

          .input-group(v-if="!anonimo")
            label(for="email") E-mail
            input(type="email" id="email" v-model="usuario.email" :disabled="anonimo || (usuarioLogado && !anonimo)" :readonly="usuarioLogado")
      template(v-if="etapa === 2")
        .step-content(:class="{'active-step': etapa === 2}")
          .input-group
            label(for="cep") CEP
            input(type="text" id="cep" v-model="cep" required @blur="buscarEndereco")
          
          .input-group
            label(for="logradouro") Logradouro
            input(type="text" id="logradouro" v-model="logradouro" required :disabled="cep.length < 8")
          
          .input-group
            label(for="bairro") Bairro
            input(type="text" id="bairro" v-model="bairro" required :disabled="cep.length < 8")
          
          .input-group
            label(for="cidade") Cidade
            input(type="text" id="cidade" v-model="cidade" required :disabled="cep.length < 8")
          
          .input-group
            label(for="estado") Estado
            input(type="text" id="estado" v-model="estado" required :disabled="cep.length < 8")

      template(v-if="etapa === 3")
        .step-content(:class="{'active-step': etapa === 2}")
              .input-group
                label(for="artigoId") Tipo de Ocorrência
                select(id="artigoId" v-model="artigoSelecionadoId" required)
                  option(value="" disabled selected) Selecione o Tipo de Ocorrência
                  option(v-for="artigo in artigos" :key="artigo.id" :value="artigo.id") {{ artigo.descricao }}
              .input-group
                label(for="placa") Placa do Veículo
                input(type="text" id="placa" v-model="placa" required)
              .input-group
                label(for="ano") Ano do Veículo
                input(type="number" id="ano" v-model="ano" required)
              .input-group
                label(for="marca") Marca
                input(type="text" id="marca" v-model="marca" required)
              .input-group
                label(for="modelo") Modelo
                input(type="text" id="modelo" v-model="modelo" required)
              .input-group
                label(for="cor") Cor
                input(type="text" id="cor" v-model="cor" required)
      template(v-if="etapa === 4")
        .step-content(:class="{'active-step': etapa === 3}")
              .input-group
                label(for="horaOcorrencia") Hora da Ocorrência
                input(type="time" id="horaOcorrencia" v-model="horaOcorrencia" required)
              .input-group
                label(for="descricao") Descrição
                textarea(id="descricao" v-model="descricao" required)
      template(v-if="etapa === 5")
        .step-content(:class="{'active-step': etapa === 4}")
          .termo-container
            h2 Termo de Envio de Denúncia
            p Ao prosseguir, você confirma que as informações fornecidas são verdadeiras e que entende as implicações legais da denúncia falsa.
            
          // Exibe o checkbox para alertas se não for anônimo
          .input-alertas(v-if="!anonimo")
            label(for="receberAlertas") Deseja receber alertas por e-mail sobre sua denúncia?
            .alertas-checkbox
              input(type="checkbox" id="receberAlertas" v-model="receberAlertas")
              span Receber alertas por e-mail





      .botoes
        button.btn-voltar(type="button" @click="voltar" :disabled="etapa === 1") Voltar
        button.btn-avancar(type="button" @click="proximaEtapa" :disabled="!podeAvancar") {{ etapa === 5 ? 'Enviar Denúncia' : 'Próxima Etapa' }}
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { fetchUserData } from '@/services/authService'
import { toast } from 'vue3-toastify'
import { enviarDenuncia as enviarDenunciaService } from '@/services/ocorrenciasService'
import { buscarArtigos } from '@/services/artigoService'
import { useLoadingStore } from '@/stores/loadingStore'
const store = useLoadingStore()
const router = useRouter()

// Definindo os dados do formulário
const placa = ref('')
const ano = ref(0)
const marca = ref('')
const modelo = ref('')
const cor = ref('')
const horaOcorrencia = ref('')
const descricao = ref('')
const anonimo = ref(false)
const tipoOcorrencia = ref('')
const artigos = ref([])
const artigoSelecionadoId = ref(null)
const receberAlertas = ref(true) // valor padrão: sim

// Controle da etapa atual
const etapa = ref(1) // Etapa inicial 1, agora etapa 2 será para localização

// Definindo os dados do usuário
const usuario = ref({
  id: '',
  username: '',
  cpf: '',
  email: '',
})
const cep = ref('')
const logradouro = ref('')
const bairro = ref('')
const cidade = ref('')
const estado = ref('')

// Função para buscar endereço usando o CEP
const buscarEndereco = async () => {
  if (cep.value.length === 8) {
    try {
      const response = await axios.get(`https://viacep.com.br/ws/${cep.value}/json/`)
      logradouro.value = response.data.logradouro || ''
      bairro.value = response.data.bairro || ''
      cidade.value = response.data.localidade || ''
      estado.value = response.data.uf || ''
    } catch (error) {
      toast.error('Erro ao buscar endereço. Verifique o CEP.')
    }
  }
}

// Validação da etapa de localização
const etapa2Valida = computed(() => {
  return (
    cep.value.length === 8 &&
    logradouro.value.trim() !== '' &&
    bairro.value.trim() !== '' &&
    cidade.value.trim() !== '' &&
    estado.value.trim() !== ''
  )
})

const usuarioLogado = ref(false) // Controla se o usuário está logado
const carregarArtigos = async () => {
  try {
    artigos.value = await buscarArtigos()
  } catch (error) {
    toast.error('Erro ao carregar os artigos do Código Penal.')
  }
}
const buscarUsuario = async () => {
  const token = localStorage.getItem('authToken')

  if (!token) {
    // Não faz requisição se não houver token
    console.warn('Token não encontrado. Usuário não está autenticado.')
    return
  }

  try {
    const usuarioData = await fetchUserData()
    usuario.value = usuarioData
    usuarioLogado.value = true
  } catch (error) {
    toast.error('Erro ao buscar dados do usuário já logado.')
    console.error('Erro ao buscar dados do usuário:', error)
  }
}

// Controle da etapa atual

// Função que altera a etapa atual
const proximaEtapa = () => {
  if (etapa.value < 5) {
    // Agora são 5 etapas, de 1 a 5
    etapa.value++
  } else {
    enviarDenuncia()
  }
}

// Função que volta à etapa anterior
const voltar = () => {
  if (etapa.value > 1) {
    etapa.value--
  }
}

const etapa1Valida = computed(() => {
  return (
    anonimo.value ||
    (usuario.value.username.trim() !== '' &&
      usuario.value.cpf.trim() !== '' &&
      usuario.value.email.trim() !== '')
  )
})

// Validação da etapa 2
const etapa3Valida = computed(() => {
  return (
    placa.value.trim() !== '' &&
    ano.value > 0 &&
    marca.value.trim() !== '' &&
    modelo.value.trim() !== '' &&
    cor.value.trim() !== '' &&
    artigoSelecionadoId.value !== null
  )
})

// Validação da etapa 3
const etapa4Valida = computed(() => {
  return horaOcorrencia.value.trim() !== '' && descricao.value.trim() !== ''
})

const podeAvancar = computed(() => {
  if (etapa.value === 1) return etapa1Valida.value
  if (etapa.value === 2) return etapa2Valida.value
  if (etapa.value === 3) return etapa3Valida.value
  if (etapa.value === 4) return etapa4Valida.value

  return true // etapa 4
})

// Função para enviar a denúncia
const enviarDenuncia = async () => {
  try {
    let idUsuario = null

    // Se o usuário não for anônimo e estiver logado, usa o id do usuário
    if (!anonimo.value && usuarioLogado.value) {
      idUsuario = usuario.value.id
    } else if (anonimo.value) {
      // Se for anônimo, o id será 1
      idUsuario = 1
    }

    const denuncia = {
      idUsuario: usuario.value.id || null, // Se o usuário estiver logado, pega o id, caso contrário, usa null
      username: usuario.value.username, // Username preenchido no formulário
      cpf: usuario.value.cpf,
      email: usuario.value.email,
      descricao: descricao.value,
      statusDenuncia: 'Em andamento',
      horaOcorrencia: horaOcorrencia.value,
      dataHora: new Date().toISOString(),
      placa: placa.value,
      ano: ano.value,
      tipo: 'Carro',
      modelo: modelo.value,
      marca: marca.value,
      cor: cor.value,
      artigoLei: artigoSelecionadoId.value,
      receberAlertas: receberAlertas.value,
      cep: cep.value,
      logradouro: logradouro.value,
      bairro: bairro.value,
      cidade: cidade.value,
      estado: estado.value,
    }
    store.startLoading() // Inicia o loading
    await enviarDenunciaService(denuncia)
    store.stopLoading() // Para o loading quando a ação terminar
    toast.success('Denúncia registrada com sucesso!')
    router.push({ name: 'inicio' })
  } catch (error) {
    store.stopLoading() // Para o loading quando a ação terminar
    toast.error('Erro ao registrar denúncia. Verifique os dados e tente novamente.')
  }
}

// Alterna a flag de anonimato
const toggleAnonimo = () => {
  if (anonimo.value) {
    receberAlertas.value = false
    etapa.value = 2
  } else {
    etapa.value = 1
  }
}

// Buscar dados do usuário assim que o componente for montado
onMounted(() => {
  buscarUsuario()
  carregarArtigos()
})
</script>

<style scoped>
.denuncia {
  padding: 2rem;
  max-width: 1000px;
  margin: auto;
  font-family: 'Arial', sans-serif;
  background-color: #fefefe;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

h1 {
  text-align: center;
  margin-bottom: 2rem;
  color: #2c3e50;
}

/* Campos de formulário */
.input-group {
  margin-bottom: 1.2rem;
}
.input-anonimo {
  margin-bottom: 1.2rem;
  text-align: center;
}

label {
  display: block;
  margin-bottom: 0.4rem;
  font-weight: 500;
  color: #333;
}

input,
textarea {
  width: 100%;
  padding: 0.7rem;
  font-size: 0.9rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  transition: border-color 0.3s;
}

input:focus,
textarea:focus {
  outline: none;
  border-color: #28a745;
}

textarea {
  resize: vertical;
  min-height: 80px;
}

/* Botões */
.botoes {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-top: 1.5rem;
}

button {
  padding: 0.6rem 1.2rem;
  font-size: 0.9rem;
  border: 2px solid #28a745;
  border-radius: 6px;
  cursor: pointer;
  background-color: white;
  color: #2c3e50;
  transition: all 0.3s ease;
}

button:hover,
button:focus {
  background-color: #28a745;
  color: white;
}

button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Estilo do campo anônimo */
.anonimo-checkbox {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 1rem;
}

.anonimo-checkbox input {
  margin-right: 8px;
}

.active-step {
  display: block;
}

.input-group {
  margin-bottom: 1.2rem;
}

select {
  width: 100%;
  padding: 0.7rem;
  font-size: 0.9rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  transition: border-color 0.3s;
}

select:focus {
  outline: none;
  border-color: #28a745;
}

.termo-container {
  max-width: 600px;
  margin: 0 auto;
  text-align: center;
  background-color: #f8f8f8;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.termo-container h2 {
  font-size: 1.5rem;
  margin-bottom: 1rem;
}

.termo-container p {
  font-size: 1rem;
  line-height: 1.5;
  color: #333;
}

.input-alertas {
  margin-top: 1.2rem;
  text-align: center;
}

.alertas-checkbox {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 0.5rem;
}

.alertas-checkbox input[type='checkbox'] {
  width: 18px;
  height: 18px;
  margin-right: 8px;
  accent-color: #28a745; /* verde personalizado */
  cursor: pointer;
}

.alertas-checkbox span {
  font-size: 0.95rem;
  color: #333;
}
/* Linha do tempo */
.timeline {
  display: flex;
  justify-content: space-between;
  margin-bottom: 2rem;
  position: relative;
  padding: 0 1rem;
}

.step {
  flex: 1;
  text-align: center;
  padding: 0.8rem 0.5rem;
  background-color: #e0e0e0;
  border-radius: 20px;
  font-size: 0.85rem;
  color: #555;
  transition: all 0.3s;
  position: relative;
  margin: 0 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.line {
  position: absolute;
  top: 50%;
  left: 0;
  width: calc(100% - 40px); /* Deixe um espaço nas laterais para não encostar nos cards */
  height: 2px;
  background-color: #28a745; /* Cor da linha */
  z-index: -1;
}
.step:not(:last-child) .line {
  display: block; /* Exibe a linha entre os passos */
}
.step:last-child .line {
  display: none; /* Não exibe a linha após o último passo */
}

.step i {
  font-size: 1.5rem;
  margin-bottom: 4px;
}

.step.active {
  background-color: #28a745;
  color: white;
  font-weight: bold;
}

.step.completed {
  background-color: #28a745;
  color: white;
  font-weight: bold;
}

.step.completed::after {
  content: '✔';
  font-size: 1rem;
  color: white;
  position: absolute;
  top: 8px;
  right: 10px;
}

/* Conteúdo de cada etapa */
.step-content {
  margin-bottom: 2rem;
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
