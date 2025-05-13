<template lang="pug">
  .meus-dados
    h2.titulo Editar Meus Dados

    form(@submit.prevent="salvarDados", v-if="!carregando")
      div.erro(v-if="erro") {{ erro }}

      // Nome, CPF e E-mail são comuns a todos os perfis
      label(for="nome") Nome Completo
      input(type="text" id="nome" v-model="form.username" :readonly="isReadonly" required)
      span.error-message(v-if="nomeError") {{ nomeError }}

      label(for="cpf") CPF
      input(type="text" id="cpf" v-model="form.cpf" maxlength="14" :readonly="isReadonly" required @input="formatarCPF")
      span.error-message(v-if="cpfError") {{ cpfError }}

      label(for="email") E-mail
      input(type="email" id="email" v-model="form.email" :readonly="isReadonly" required)
      span.error-message(v-if="emailError") {{ emailError }}

      // Exibir o perfil atual com destaque
      div.perfil-atual
        p Perfil Atual: 
          span.profile-name {{ perfilAtual.nome || 'Nenhum perfil selecionado' }}

      label(for="role") Perfil de Acesso
      select#role(v-model="form.role")
        option(v-for="(desc, key) in perfisDisponiveis" :key="key" :value="key")
          | {{ desc.nome }}

      // Descrição do perfil fora do select
      .descricao-perfil(v-if="form.role")
        pstrong Descrição:
        p {{ perfisDisponiveis[form.role]?.descricao }}

      // Exibir os campos dependendo do perfil selecionado
      div.input-group(v-if="form.role === 'GESTOR_DE_SEGURANCA_PUBLICA'")
        label(for="departamento") Departamento
        input(type="text" id="departamento" v-model="form.departamento" required)
        span.error-message(v-if="departamentoError") {{ departamentoError }}

      div.input-group(v-if="form.role === 'GESTOR_DE_SEGURANCA_PUBLICA'")
        label(for="cargo") Cargo
        input(type="text" id="cargo" v-model="form.cargo" required)
        span.error-message(v-if="cargoError") {{ cargoError }}

      div.input-group(v-if="form.role === 'POLICIAL' || form.role === 'AGENTE_DE_SEGURANCA' || form.role === 'INVESTIGADOR'")
        label(for="delegacia") Delegacia
        input(type="text" id="delegacia" v-model="form.delegate" required)
        span.error-message(v-if="delegaciaError") {{ delegaciaError }}

      div.input-group(v-if="form.role === 'POLICIAL' || form.role === 'AGENTE_DE_SEGURANCA' || form.role === 'INVESTIGADOR'")
        label(for="distintivo") Distintivo
        input(type="text" id="distintivo" v-model="form.badge" required)
        span.error-message(v-if="distintivoError") {{ distintivoError }}

      div.input-group(v-if="form.role === 'POLICIAL' || form.role === 'AGENTE_DE_SEGURANCA' || form.role === 'INVESTIGADOR'")
        label(for="ra") RA
        input(type="text" id="ra" v-model="form.ra" required)
        span.error-message(v-if="raError") {{ raError }}

      div.input-group.password-group
        label(for="senha") Senha
        div.password-wrapper
          input(:type="showPassword ? 'text' : 'password'" id="senha" v-model="form.password" readonly required)

      button(type="submit") Salvar

    .carregando(v-else) Carregando...
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { toast } from 'vue3-toastify'
import { fetchUserData, updateUserData } from '@/services/authService'

// Tipagem para os valores possíveis de role
type RoleType =
  | 'PUBLICO'
  | 'POLICIAL'
  | 'AGENTE_DE_SEGURANCA'
  | 'INVESTIGADOR'
  | 'GESTOR_DE_SEGURANCA_PUBLICA'

// Dados do formulário
const form = ref({
  id: '',
  username: '',
  cpf: '',
  email: '',
  role: '' as RoleType, // Agora `role` tem tipo explícito
  departamento: '',
  cargo: '',
  delegate: '',
  badge: '',
  ra: '',
  password: '',
})

const perfilAtual = ref({
  nome: '',
  descricao: '',
})

const showPassword = ref(false)
const isReadonly = ref(true) // Define os campos como readonly inicialmente

// Erros
const nomeError = ref('')
const cpfError = ref('')
const emailError = ref('')
const departamentoError = ref('')
const cargoError = ref('')
const delegaciaError = ref('')
const distintivoError = ref('')
const raError = ref('')

// Perfis disponíveis com base no enum RoleType
const perfisDisponiveis: Record<RoleType, { nome: string; descricao: string }> = {
  PUBLICO: {
    nome: 'Público',
    descricao: 'Acesso restrito para o público em geral.',
  },
  POLICIAL: {
    nome: 'Policial',
    descricao:
      'O Policial atua diretamente na linha de frente para garantir a segurança e a ordem pública.',
  },
  AGENTE_DE_SEGURANCA: {
    nome: 'Agente de Segurança',
    descricao: 'Responsável pela segurança e proteção de áreas e operações de segurança.',
  },
  INVESTIGADOR: {
    nome: 'Investigador',
    descricao: 'Responsável pela investigação de crimes e coleta de provas.',
  },
  GESTOR_DE_SEGURANCA_PUBLICA: {
    nome: 'Gestor de Segurança Pública',
    descricao:
      'O Gestor de Segurança Pública é responsável pela administração e coordenação de operações relacionadas à segurança.',
  },
}

// Função para pegar dados do usuário e preencher o formulário
const carregarDados = async () => {
  try {
    const data = await fetchUserData() // Função para buscar os dados do usuário
    form.value = { ...data, role: data.role.name as RoleType } // Atribuir 'role.name' à `form.role`

    // Atualizando o perfil atual
    perfilAtual.value = {
      nome: perfisDisponiveis[form.value.role]?.nome || 'Nenhum perfil selecionado',
      descricao: perfisDisponiveis[form.value.role]?.descricao || '',
    }
  } catch (error) {
    toast.error('Erro ao carregar os dados do usuário.')
  }
}

const roleToTipoMap: Record<RoleType, number> = {
  PUBLICO: 1,
  POLICIAL: 2,
  AGENTE_DE_SEGURANCA: 3,
  INVESTIGADOR: 4,
  GESTOR_DE_SEGURANCA_PUBLICA: 5,
}

const salvarDados = async () => {
  try {
    const {
      id,
      username,
      email,
      cpf,
      role, // role agora é uma string com tipo explícito
      departamento,
      cargo,
      delegate,
      badge,
      ra,
    } = form.value

    const tipo = roleToTipoMap[role] // role agora é uma string com tipo explícito

    if (tipo === undefined) {
      toast.error('Perfil de acesso inválido.')
      return
    }

    // Chama a função de atualização passando os dados individualmente
    const response = await updateUserData(
      id,
      username,
      email,
      cpf,
      tipo, // tipo é o valor de "role" convertido para número
      departamento,
      cargo,
      delegate, // opcional
      badge, // opcional
      ra, // opcional
    )

    toast.success('Dados atualizados com sucesso!')

    // Chama o carregarDados para atualizar o formulário
    await carregarDados() // Recarregar os dados para refletir o novo perfil
    window.location.reload()

    // Adicionalmente, você pode forçar a reatividade para garantir que os campos
    // sejam atualizados imediatamente, se necessário:
    form.value = { ...form.value } // Isso força o Vue a detectar a mudança no objeto
  } catch (error) {
    toast.error('Erro ao salvar os dados.')
  }
}

// Função para formatar o CPF
const formatarCPF = () => {
  let cpf = form.value.cpf.replace(/\D/g, '') // Remove todos os caracteres não numéricos
  if (cpf.length <= 3) return (form.value.cpf = cpf)
  if (cpf.length <= 6) return (form.value.cpf = cpf.replace(/(\d{3})(\d{1,})/, '$1.$2'))
  if (cpf.length <= 9) return (form.value.cpf = cpf.replace(/(\d{3})(\d{3})(\d{1,})/, '$1.$2.$3'))
  return (form.value.cpf = cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{1,})/, '$1.$2.$3-$4'))
}

// Carregar os dados do usuário quando o componente for montado
carregarDados()
</script>

<style scoped>
.meus-dados {
  padding: 2rem;
  max-width: 800px;
  margin: 0 auto;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.titulo {
  text-align: center;
  color: #333;
  margin-bottom: 1.5rem;
  font-size: 1.8rem;
  font-weight: 600;
}

.input-group {
  margin-bottom: 1.5rem;
}

label {
  font-weight: 500;
  font-size: 1rem;
  color: #333;
  display: block;
  margin-bottom: 0.5rem;
}

input,
select {
  width: 100%;
  padding: 0.8rem;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}

input[readonly],
select[disabled] {
  background-color: #f1f1f1;
}

.error-message {
  color: red;
  font-size: 0.875rem;
  margin-top: 0.25rem;
}

.password-group {
  position: relative;
}

.password-wrapper {
  position: relative;
}

.password-wrapper input {
  width: 100%;
  padding-right: 2.5rem; /* espaço pro ícone */
  font-size: 1rem;
}

.toggle-icon {
  position: absolute;
  right: 0.8rem;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  color: #888;
}

button {
  background-color: #42b983;
  color: white;
  cursor: pointer;
  padding: 0.8rem;
  width: 100%;
  font-size: 1.2rem;
  border: none;
  border-radius: 4px;
  transition: background-color 0.3s ease;
}

button:hover {
  background-color: #369f6f;
}

.carregando {
  text-align: center;
  font-size: 1.2rem;
  font-weight: 600;
  color: #888;
}

.descricao-perfil {
  margin-top: 1.5rem;
  padding: 1rem;
  background-color: #e9f7ec;
  border: 1px solid #d4f1d1;
  border-radius: 4px;
}

.descricao-perfil p {
  margin: 0;
  color: #333;
}

.descricao-perfil pstrong {
  font-weight: 600;
}

.perfil-atual {
  margin-bottom: 1.5rem;
  font-size: 1.1rem;
  font-weight: 500;
  margin-top: 5%;
  color: #444;

  /* Adicionando um fundo claro e suave */
  background-color: #f4f4f4;

  /* Centralizando o conteúdo */
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  /* Adicionando uma borda arredondada */
  border-radius: 8px;

  /* Sombra suave para dar mais destaque */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);

  padding: 20px;
  text-align: center; /* Centralizando o texto */
}

/* Estilizando o texto dentro do perfil atual */
.perfil-atual p {
  margin-bottom: 10px; /* Deixando um espaço entre o texto */
  color: #333;
  font-weight: 400;
}

/* Estilizando o nome do perfil */
.profile-name {
  font-weight: 600;
  color: #42b983; /* Usando a cor de destaque */
  font-size: 1.2rem;
  margin-top: 10px;
}

/* Adicionando um ícone para o perfil (opcional) */
.perfil-atual i {
  margin-right: 8px;
  color: #42b983;
  font-size: 1.3rem;
}

.perfil-atual {
  margin-bottom: 1.5rem;
  font-size: 1.1rem;
  font-weight: 500;
  margin-top: 3%;
  color: #444;
  background-color: #f4f4f4;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
  text-align: center;
}

.profile-name {
  font-weight: 600;
  color: #42b983;
  font-size: 1.2rem;
  margin-top: 10px;
}

.descricao-perfil {
  margin-top: 1.5rem;
  padding: 1rem;
  background-color: #e9f7ec;
  border: 1px solid #d4f1d1;
  border-radius: 4px;
}

.descricao-perfil p {
  margin: 0;
  color: #333;
}

.descricao-perfil pstrong {
  font-weight: 600;
}

/* Aumentando a visibilidade do perfil Investigador */
.perfil-investigador {
  margin-top: 1rem;
  padding: 1rem;
  background-color: #d9f1f9;
  border: 1px solid #a8d8e7;
  border-radius: 4px;
}

.perfil-investigador p {
  color: #333;
}
</style>
