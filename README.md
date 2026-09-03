# 💰 Calculadora Financeira

Uma aplicação Java Desktop com interface gráfica (Swing) para gerenciamento de finanças pessoais, controle de receitas e despesas, cálculo de saldo e persistência de dados.

---

## 📌 Visão Geral

A **Calculadora Financeira** é um sistema desktop desenvolvido em **Java** com foco na organização financeira pessoal. A aplicação permite registrar receitas e despesas categorizadas, gerenciar transações financeiras em tempo real e visualizar o saldo atualizado. Os dados são mantidos localmente por meio de persistência em arquivo CSV (`financas.csv`).

---

## 🏗️ Estrutura do Projeto

O código-fonte está organizado em camadas no pacote `br.com.financas`:

```text
src/br/com/financas/
├── app/
│   └── Main.java                       # Ponto de entrada da aplicação
├── gui/                                # Interface Gráfica (Swing)
│   ├── MainApp.java                    # Aplicação principal Swing
│   ├── MainFrame.java                  # Janela principal e tabelas
│   ├── CadastroReceitaDialog.java      # Modal de cadastro de receitas
│   ├── CadastroDespesaDialog.java      # Modal de cadastro de despesas
│   ├── TransacaoTableModel.java        # Modelo de dados da JTable
│   └── TransacaoRowRenderer.java       # Renderização visual das linhas da tabela
├── model/                              # Entidades de Domínio
│   ├── Transacao.java                  # Classe abstrata base para movimentações
│   ├── Receita.java                    # Entidade de entrada de valores
│   ├── Despesa.java                    # Entidade de saída de valores
│   └── Categoria.java                  # Enumeração/Entidade de categorias
├── repository/                         # Camada de Persistência
│   └── TransacaoRepository.java        # Leitura e escrita no arquivo financas.csv
└── service/                            # Regras de Negócio
    └── GerenciadorFinanceiro.java      # Gestão do saldo e lógica de transações

---

✨ Principais Funcionalidades
Gerenciamento de Transações:

Lançamento de receitas e despesas com descrição, valor, data e categoria.

Visualização gráfica em lista/tabela (JTable) com estilo e renderização customizados (TransacaoRowRenderer).

Cálculo de Saldo Automático:

Regra de negócio via GerenciadorFinanceiro que calcula entradas, saídas e o saldo consolidado do usuário.

Persistência de Dados Local:

Armazenamento e carregamento das transações utilizando arquivo CSV (financas.csv).

---

🛠️ Tecnologias Utilizadas
Linguagem: Java (JDK 8+)

Interface Gráfica: Java Swing / AWT

Persistência Atual: Leitura e Escrita de Arquivos CSV (Java I/O)

Paradigma: Orientação a Objetos (Herança, Polimorfismo e Encapsulamento)

---

🚀 Próximas Melhorias e Roadmap
A evolução da Calculadora Financeira inclui a substituição do armazenamento local por banco de dados e a integração com canais de comunicação instantânea:

🗄️ 1. Migração para Banco de Dados SQL
Persistência Relacional: Substituir a leitura/escrita no financas.csv por um banco de dados relacional (ex: PostgreSQL, MySQL ou SQLite).

Mapeamento/DAO: Implementar a camada DAO (Data Access Object) utilizando JDBC ou um framework ORM (como Hibernate/JPA).

Histórico e Filtros: Consultas otimizadas por período, categoria ou tipo de movimentação via SQL.

Multi-usuário: Suporte a autenticação de usuários e isolamento de carteiras financeiras no banco.

📲 2. Integração com WhatsApp
Bot de Lançamentos Rápidos: Criação de uma API/Webhook (utilizando WhatsApp Business API ou Twilio) para permitir o envio de comandos de texto (ex: Receita 1500 Salário ou Despesa 45 Mercado).

Notificações e Resumos: Envio de alertas periódicos com o resumo semanal/mensal de gastos e saldo disponível direto no celular.

Consulta de Saldo: Responder a comandos de mensagem com o saldo atual do usuário.

---

🔧 Como Executar o Projeto

Clone o repositório:

Bash

git clone [https://github.com/NicolasFLopes/Calculadora-Financeira.git](https://github.com/NicolasFLopes/Calculadora-Financeira.git)
Abra o projeto em sua IDE Java preferida (Eclipse, IntelliJ IDEA, NetBeans e VSCode).

Navegue até a classe principal:

Plaintext
src/br/com/financas/app/Main.java
Execute o método main para iniciar a interface gráfica Swing.
