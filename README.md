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
