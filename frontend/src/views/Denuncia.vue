<template lang="pug">
.template
  .denuncia
    h1 Registrar Denúncia de Roubo ou Furto de Veículo

    // Linha do tempo
    .timeline
      .progress-line(:class="{ completed: progresso === 100 }")
        .fill(:style="{ width: progresso + '%' }")
      .step(:class="{ active: etapa === 1, completed: etapa > 1 }")
        i.fas.fa-user
        span Dados Pessoais
      .step(:class="{ active: etapa === 2, completed: etapa > 2 }")
        i.fas.fa-map-marker-alt
        span Local
      .step(:class="{ active: etapa === 3, completed: etapa > 3 }")
        i.fas.fa-car
        span Veículo
      .step(:class="{ active: etapa === 4, completed: etapa > 4 }")
        i.fas.fa-comment
        span Descrição
      .step(:class="{ active: etapa === 5 }")
        i.fas.fa-check-circle
        span Finalizar



    .line
    form(@submit.prevent="enviarDenuncia")
      template(v-if="etapa === 1")
        .step-content(:class="{'active-step': etapa === 1}")
          .input-anonimo
            label(for="anonimo") Denunciar de forma anônima
            .anonimo-checkbox
              input(type="checkbox" id="anonimo" v-model="anonimo" @change="toggleAnonimo")

          .input-group(v-if="!anonimo")
            label(for="username")
            | Nome 
            span.text-danger(v-if="mostrarAsteriscos") *
            input(type="text" id="username" v-model="usuario.username" :disabled="anonimo || (usuarioLogado && !anonimo)" :readonly="usuarioLogado")

          .input-group(v-if="!anonimo")
            label(for="cpf") 
            | CPF
            span.text-danger(v-if="mostrarAsteriscos") *
            input(type="text" id="cpf" v-model="usuario.cpf" :disabled="anonimo || (usuarioLogado && !anonimo)" :readonly="usuarioLogado")

          .input-group(v-if="!anonimo")
            label(for="email") 
            | E-mail
            span.text-danger(v-if="mostrarAsteriscos") *
            input(type="email" id="email" v-model="usuario.email" :disabled="anonimo || (usuarioLogado && !anonimo)" :readonly="usuarioLogado")

      template(v-if="etapa === 2")
        .step-content(:class="{'active-step': etapa === 2}")
          .input-group
            label(for="cep") 
            | CEP
            span.text-danger() *
            input(
              type="text"
              id="cep"
              v-model="formattedCep"
              maxlength="9"
              placeholder="Digite o CEP"
              required
              @input="formatCepInput"
              @blur="buscarEndereco"
            )
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
        .step-content(:class="{'active-step': etapa === 3}")
          .input-group
            label(for="artigoId") 
            | Tipo de Ocorrência
            span.text-danger() *
            select(id="artigoId" v-model="artigoSelecionadoId" required)
              option(value="" disabled selected) Selecione o Tipo de Ocorrência
              option(v-for="artigo in artigos" :key="artigo.id" :value="artigo.id") {{ artigo.descricao }}
          .input-group
            label(for="placa") 
            | Placa do Veículo
            span.text-danger() *
            input(type="text" id="placa" v-model="placa" required)
          .input-group
            label(for="ano") 
              | Ano do Veículo
              span.text-danger() *
            select(id="ano" v-model="ano" required)
              option(value="" disabled selected) Selecione o ano
              option(v-for="ano in anosDisponiveis" :key="ano" :value="ano") {{ ano }}

          .input-group
            label(for="marca") 
            | Marca
            span.text-danger() *
            input(type="text" id="marca" v-model="marca" required)
          .input-group
            label(for="modelo") 
            | Modelo
            span.text-danger() *
            input(type="text" id="modelo" v-model="modelo" required)
          .input-group
            label(for="cor") 
            | Cor
            span.text-danger() *
            input(type="text" id="cor" v-model="cor" required)

      template(v-if="etapa === 4")
        .step-content(:class="{'active-step': etapa === 4}")
          .input-group
            label(for="dataOcorrencia") 
            | Data da Ocorrência
            span.text-danger() *
            input(type="date" id="dataOcorrencia" v-model="dataOcorrencia" required)
            
          .input-group
            label(for="horaOcorrencia") 
            | Hora da Ocorrência
            span.text-danger() *
            input(type="time" id="horaOcorrencia" v-model="horaOcorrencia" required)
            
          .input-group
            label(for="descricao") 
            | Descrição
            span.text-danger() *
            textarea(id="descricao" v-model="descricao" required)

      template(v-if="etapa === 5")
        .step-content(:class="{'active-step': etapa === 5}")
          .termo-container
            h2 Termo de Envio de Denúncia
            p Ao prosseguir, você confirma que as informações fornecidas são verdadeiras e que entende as implicações legais da denúncia falsa.
            
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
import { ref, onMounted, watch, computed } from 'vue'
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
const ano = ref<number | null>(null) // ✅ esta é a correta
const marca = ref('')
const modelo = ref('')
const cor = ref('')
const horaOcorrencia = ref('')
const dataOcorrencia = ref('')
const descricao = ref('')
const anonimo = ref(false)
const tipoOcorrencia = ref('')
const artigos = ref([])
const artigoSelecionadoId = ref(null)
const receberAlertas = ref(true) // valor padrão: sim
const anoAtual = new Date().getFullYear()
const anosDisponiveis = ref<number[]>([])
const formattedCep = ref('')

const formatCepInput = (event: Event) => {
  const input = event.target as HTMLInputElement
  const raw = input.value.replace(/\D/g, '') // Remove tudo que não é número

  // Formata com traço se possível
  if (raw.length <= 5) {
    formattedCep.value = raw
  } else {
    formattedCep.value = `${raw.slice(0, 5)}-${raw.slice(5, 8)}`
  }

  // Atualiza o CEP limpo para busca
  cep.value = raw.slice(0, 8)
}
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
const progresso = computed(() => {
  // Total de 5 etapas: 0%, 25%, 50%, 75%, 100%
  return ((etapa.value - 1) / 4) * 100 // Ajusta a porcentagem de acordo com a etapa
})

// Função para buscar endereço usando o CEP
const buscarEndereco = async () => {
  if (cep.value.length === 8) {
    try {
      store.startLoading() // Inicia o loading
      const response = await axios.get(`https://viacep.com.br/ws/${cep.value}/json/`)
      logradouro.value = response.data.logradouro || ''
      bairro.value = response.data.bairro || ''
      cidade.value = response.data.localidade || ''
      estado.value = response.data.uf || ''
      store.stopLoading() // Para o loading quando a ação terminar
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
  if (anonimo.value) return true

  const nomeValido = usuario.value.username.trim() !== ''
  const cpfValido = validarCPF(usuario.value.cpf)
  const emailValido = validarEmail(usuario.value.email)

  return nomeValido && cpfValido && emailValido
})

// Validação da etapa 2
const etapa3Valida = computed(() => {
  return (
    placa.value.trim() !== '' &&
    ano.value !== null &&
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
      idUsuario: usuario.value.id || 1, // Se o usuário estiver logado, pega o id, caso contrário, usa null
      username: anonimo.value ? 'Anônimo' : usuario.value.username,
      cpf: anonimo.value ? null : usuario.value.cpf,
      email: anonimo.value ? null : usuario.value.email,
      descricao: descricao.value,
      statusDenuncia: 'Em andamento',
      horaOcorrencia: horaOcorrencia.value,
      dataHora: `${dataOcorrencia.value}T${horaOcorrencia.value}`,
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
  for (let ano = anoAtual; ano >= anoAtual - 10; ano--) {
    anosDisponiveis.value.push(ano)
  }
})

function validarEmail(email: string): boolean {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return re.test(email.toLowerCase())
}

function validarCPF(cpf: string): boolean {
  cpf = cpf.replace(/[^\d]+/g, '')

  if (!cpf || cpf.length !== 11 || /^(\d)\1+$/.test(cpf)) return false

  let soma = 0
  for (let i = 0; i < 9; i++) soma += parseInt(cpf.charAt(i)) * (10 - i)
  let resto = 11 - (soma % 11)
  if (resto === 10 || resto === 11) resto = 0
  if (resto !== parseInt(cpf.charAt(9))) return false

  soma = 0
  for (let i = 0; i < 10; i++) soma += parseInt(cpf.charAt(i)) * (11 - i)
  resto = 11 - (soma % 11)
  if (resto === 10 || resto === 11) resto = 0
  return resto === parseInt(cpf.charAt(10))
}

const mostrarAsteriscos = computed(() => {
  return usuario.value.username.trim() !== ''
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
  margin-top: 10px;
}

.input-group input {
  margin-bottom: 1.2rem;
  margin-top: 6px;
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
textarea,
select {
  width: 100%;
  padding: 0.7rem;
  font-size: 0.9rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  transition: border-color 0.3s;
}

input:focus,
textarea:focus,
select:focus {
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

/* Termo de aceite */
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

/* Alertas */
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
  accent-color: #28a745;
  cursor: pointer;
}

.alertas-checkbox span {
  font-size: 0.95rem;
  color: #333;
}

.timeline {
  display: flex;
  justify-content: space-between;
  position: relative;
  margin: 40px 0;
  align-items: center;
  width: 100%;
}
.progress-line {
  position: absolute;
  top: 20%; /* A linha passa no meio dos ícones */
  left: 10%;
  right: 10%;
  height: 3px;
  background-color: #ccc; /* Linha de progresso cinza */
  z-index: 0;
  transition: width 0.4s ease;
  transform: translateY(-50%);
  width: 80%; /* Linha cinza ocupa toda a largura */
}

.progress-line .fill {
  background-color: green; /* Preenchimento verde */
  height: 3px;
  width: 0%; /* Inicialmente, o verde começa com 0% */
  transition: width 0.4s ease;
}

/* Estilo das etapas */
.step {
  position: relative;
  text-align: center;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 2;
}

.step i {
  background-color: #ccc;
  color: white;
  border-radius: 50%;
  padding: 10px;
  font-size: 1.2rem;
  margin-bottom: 8px;
  display: inline-block;
  transition: all 0.3s ease;
  position: absolute; /* Coloca o ícone no meio da linha */
  top: -15px; /* Distância da linha */
}

.step.active i {
  background-color: #28a745; /* Cor verde para a etapa ativa */
}

.step span {
  display: block;
  font-size: 0.9rem;
  color: #333;
  margin-top: 30px; /* Ajusta a distância do texto em relação ao ícone */
}

.step.completed i {
  background-color: #28a745; /* Cor verde para as etapas completadas */
}

.progress-line.completed {
  background-color: #28a745;
}

/* Remover o ícone de check para etapas completadas */
.step.completed::before {
  content: ''; /* Remover ícone de check */
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

.text-danger {
  color: red;
  margin-left: 4px;
}
</style>
