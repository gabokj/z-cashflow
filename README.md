# Z-CashFlow — Controle de Fluxo de Caixa Pessoal

> Aplicativo desktop em Java para registrar receitas e despesas, acompanhar o saldo em tempo real e entender para onde o dinheiro está indo.

## Status do projeto

🚧 **Em desenvolvimento** — versão 0.4

| Etapa do Projeto Integrador | Entrega | Situação |
|---|---|---|
| 1 | Requisitos, diagrama de classes e protótipo | ✅ Concluída |
| 2 | Projeto de UX/UI (wireframes, cores e acessibilidade) | ✅ Concluída |
| 3 | Interface gráfica com Swing | ✅ Concluída |
| 4 | Integração com MySQL e regras de negócio | ✅ Concluída |
| 5 | Versionamento com Git e GitHub | ✅ Este repositório |

**Próximos passos (planejados):** Metas de Economia, gestão de usuários pelo administrador, regras
completas do RF002, registro de logs, backup e testes automatizados.

## Objetivo do software

O Z-CashFlow reúne receitas e despesas em um só lugar e mostra, de forma clara, para onde o dinheiro
está indo e qual é o saldo real. Ele substitui planilhas e contas feitas à mão: calcula tudo
automaticamente, organiza os lançamentos por categoria e permite acompanhar a situação financeira
para planejar melhor os gastos futuros. Cada pessoa vê apenas os próprios dados.

## Tecnologias

Somente tecnologias abordadas no curso:

| Tecnologia | Uso no projeto |
|---|---|
| **Java 17** (Programação Orientada a Objetos) | Linguagem do sistema |
| **Swing** | Interface gráfica desktop |
| **JDBC** | Conexão e acesso ao banco de dados |
| **MySQL 8** | Banco de dados relacional |
| **MySQL Workbench** | Modelagem e execução do script do banco (`Z-CashFlow.sql`) |
| **NetBeans** | IDE de desenvolvimento (projeto Java com Ant) |
| **UML** | Diagrama de classes (`docs/`) |
| **Git e GitHub** | Controle de versão e trabalho em equipe |

## Time de desenvolvedores

| Nome | Papel |
|---|---|
| Gabriel Teixeira Ricca | Responsável pelo projeto: requisitos, UX/UI, programação Java e banco de dados |

## Funcionalidades do sistema (requisitos)

### Requisitos funcionais (documento de requisitos da Etapa 1)

| ID | Requisito | Situação |
|---|---|---|
| RF001 | **Cadastro de Entradas e Saídas** — registrar crédito/débito com valor, data e categoria (inclui editar e excluir) | ✅ Implementado |
| RF002 | **Validação de Conta Salário** — regras de origem para depósitos de "Salário" | ⏳ Parcial: o sistema só aceita categorias compatíveis com o tipo (ex.: "Salário" é sempre entrada); as regras de origem específicas ainda serão definidas |
| RF003 | **Extrato Automatizado** — lista no padrão DD/MM/AAAA, com filtros por tipo, categoria e período e exportação em CSV | ✅ Implementado |
| RF004 | **Cálculo de Saldo em Tempo Real** — saldo recalculado a cada movimentação confirmada no banco | ✅ Implementado |

### Funcionalidades adicionais já implementadas

- **Login** por e-mail e senha e **criação de conta**, com perfis *Administrador* e *Usuário*.
- **Dashboard** com saldo atual, entradas e saídas do mês, gráfico dos últimos 6 meses e últimas movimentações.
- **Categorias**: todos consultam; somente administradores criam, editam e excluem.
- **Privacidade**: cada usuário só consulta, altera e exclui as próprias movimentações.

### Requisitos não funcionais

| ID | Requisito | Situação |
|---|---|---|
| RNF001 | Confiabilidade e segurança: tratamento de exceções, valores em `BigDecimal`, validações e senhas protegidas por hash | ✅ Atendido |
| RNF002 | Persistência de dados em banco (o documento citava JDBC/JPA; foi usado **JDBC**) | ✅ Atendido |
| RNF003 | Consulta de saldo em menos de 1 segundo (feita por uma única consulta SQL agregada, com índice por usuário/data) | ⏳ Não medido formalmente |

### Previsto no documento e ainda não implementado

Metas de Economia · gestão de usuários e métricas globais para o administrador · registro de logs ·
backup diário.

## Como executar

1. **Banco de dados:** no **MySQL Workbench**, conectado como `root`, abra e execute o arquivo
   `Z-CashFlow.sql`. Ele cria o banco `z_cashflow`, as tabelas, o usuário de aplicação e os dados de teste
   *(recria o banco do zero)*.
2. **Driver JDBC:** copie o `mysql-connector-j-8.x.x.jar` para a pasta `lib/` (veja `lib/LEIA-ME.txt`).
3. **Configuração (opcional):** os valores padrão já funcionam com o script. Se o seu MySQL for diferente,
   copie `zcashflow.properties.example` para `zcashflow.properties` e ajuste.
4. **NetBeans:** *File > Open Project* → pasta do repositório → **Run** (F6). Requer JDK 17 ou superior.

**Login de teste:** `gabriel@zmail.com` / `olaprofessor` (administrador). Os demais usuários de teste
estão descritos em `Z-CashFlow.sql`.

## Estrutura do repositório

```
├── src/zcashflow/
│   ├── Main.java       início do sistema e verificação do banco
│   ├── ui/             telas Swing
│   ├── service/        regras de negócio, validações e permissões
│   ├── dao/            acesso a dados com JDBC
│   ├── model/          classes de domínio
│   └── util/           conexão, hash de senha e formatação
├── docs/               diagrama UML e projeto de UX/UI (wireframes, mockups e paleta)
├── lib/                driver JDBC (não versionado)
├── nbproject/          configuração do projeto NetBeans
└── Z-CashFlow.sql      script do banco de dados
```

Fluxo de dependência: `ui → service → dao → MySQL`.

## Trabalhando em equipe com Git

```bash
git pull                              # traz as alterações da equipe antes de começar
# ...programar...
git add .
git commit -m "feat: descrição curta"  # prefixos: feat, fix, docs, refactor, chore
git push                              # envia para o GitHub
```

Cada etapa concluída está marcada com uma tag (`etapa-1` a `etapa-4`).
