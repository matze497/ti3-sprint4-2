# Changelog - Funcionalidades de Cadastro e Edição de Pessoas

## Data: 2025-10-26

## Resumo
Implementação completa do módulo de cadastro, listagem e edição de pessoas no sistema Vaztech.

## Alterações no Back-end (Java/Spring Boot)

### 1. Entidade Pessoa (`entity/Pessoa.java`)
- ✅ Adicionado campo `funcao` (VARCHAR 20) - Para armazenar a função da pessoa (Cliente, Fornecedor, Funcionário)
- ✅ Adicionado campo `observacao` (VARCHAR 500) - Para armazenar observações sobre a pessoa

### 2. DTOs
**PessoaAddRequestDTO:**
- ✅ Adicionado campo `funcao` com validação de tamanho máximo (20 caracteres)
- ✅ Adicionado campo `observacao` com validação de tamanho máximo (500 caracteres)

**PessoaUpdateRequestDTO:**
- ✅ Adicionado campo `funcao` com validação de tamanho máximo (20 caracteres)
- ✅ Adicionado campo `observacao` com validação de tamanho máximo (500 caracteres)

**PessoaResponseDTO:**
- ✅ Adicionado campo `funcao`
- ✅ Adicionado campo `observacao`

### 3. Service (`service/PessoaService.java` e `service/impl/PessoaServiceImpl.java`)
- ✅ Implementado método `buscarTodasPessoas()` para listar todas as pessoas cadastradas
- ✅ Atualizado método `criarPessoa()` para salvar função e observação
- ✅ Atualizado método `atualizarPessoa()` para permitir edição de função e observação

### 4. Controller (`controller/PessoaController.java`)
- ✅ Adicionado endpoint GET `/api/pessoa` para buscar todas as pessoas
- ✅ Mantidos endpoints POST e PUT para criar e atualizar pessoas

### 5. Migração de Banco de Dados
- ✅ Criado arquivo `V1_12__alterPessoas_addFuncaoObservacao.sql`
- ✅ Adiciona coluna `funcao` (VARCHAR 20)
- ✅ Adiciona coluna `observacao` (VARCHAR 500)

## Alterações no Front-end (Angular)

### 1. Models (`models/pessoa.model.ts`)
- ✅ Criado arquivo com tipos TypeScript:
  - `PessoaResponse`: Interface para resposta da API
  - `CadastrarPessoaBody`: Interface para cadastro
  - `AlterarPessoaBody`: Interface para edição

### 2. Service (`services/pessoa.service.ts`)
- ✅ Criado serviço com métodos:
  - `buscarPessoas()`: Busca todas as pessoas
  - `cadastrarPessoa()`: Cadastra nova pessoa
  - `editarPessoa()`: Edita pessoa existente

### 3. Componente Pessoas (`pages/hub/pessoas-tabs/`)

**pessoas-tabs.ts:**
- ✅ Implementada lógica de listagem com paginação (6, 12 ou 24 itens por página)
- ✅ Implementada funcionalidade de busca/filtro por nome, função ou CPF/CNPJ
- ✅ Implementado modal de cadastro com validações
- ✅ Implementado modal de edição
- ✅ Integração com ToastService para feedback visual
- ✅ Campos do formulário:
  - Nome (obrigatório)
  - CPF/CNPJ (obrigatório)
  - Função (opcional) - dropdown com opções: Cliente, Fornecedor, Funcionário
  - Observação (opcional) - textarea para texto livre

**pessoas-tabs.html:**
- ✅ Interface com toolbar contendo:
  - Campo de pesquisa
  - Botão "Cadastrar Pessoa"
- ✅ Cards de pessoas mostrando:
  - Nome
  - Função
  - CPF/CNPJ
  - Observação
  - Botão de edição
- ✅ Paginação com opções de 6, 12 ou 24 itens por página
- ✅ Modal de cadastro/edição com formulário responsivo

### 4. Environment
- ✅ Criado arquivo `environments/environment.ts` com configuração da URL da API

## Correções Adicionais

### Front-end
- ✅ Corrigido CSS no arquivo `formulario-estoque.css` (`:host()` → `:host`)
- ✅ Corrigida ordem de imports no `styles.css` para evitar warnings

## Funcionalidades Implementadas

### ✅ Cadastro de Pessoas
- Formulário modal com campos para nome, CPF/CNPJ, função e observação
- Validação de campos obrigatórios (nome e CPF/CNPJ)
- Feedback visual com toast de sucesso ou erro
- Recarregamento automático da lista após cadastro

### ✅ Listagem de Pessoas
- Cards visuais mostrando informações principais
- Paginação configurável (6, 12 ou 24 itens)
- Busca em tempo real por nome, função ou CPF/CNPJ
- Mensagem informativa quando não há pessoas cadastradas

### ✅ Edição de Pessoas
- Botão de edição em cada card
- Modal pré-preenchido com dados atuais
- Atualização de todos os campos (nome, CPF/CNPJ, função, observação)
- Feedback visual após edição

## Testes Realizados

- ✅ Build do projeto Angular concluído com sucesso
- ✅ Todos os imports e dependências estão corretos
- ✅ Componente pessoas-tabs compilado sem erros
- ✅ Integração com API REST configurada

## Observações Importantes

1. **Banco de Dados**: A migration será executada automaticamente pelo Flyway na próxima inicialização do backend
2. **Segurança**: O endpoint GET `/api/pessoa` requer autenticação (Bearer Token)
3. **Validações**: Campos obrigatórios são validados tanto no front-end quanto no back-end
4. **Budget Warning**: O warning sobre tamanho do bundle é normal e não afeta o funcionamento

## Próximos Passos Sugeridos

1. Iniciar o backend Spring Boot para aplicar a migration
2. Testar a criação de pessoas pela interface
3. Testar a edição de pessoas existentes
4. Validar a paginação com diferentes quantidades de registros
5. Testar a funcionalidade de busca
