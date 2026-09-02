# Calculadora de Finanças Pessoais (Console + GUI)

Projeto Java 26 demonstrando Herança, Polimorfismo, Encapsulamento, Abstração,
Enum, `java.time`, tratamento de exceções e persistência em arquivo CSV.

> **Sobre o alvo Java 26:** o JDK 26 (GA em 17/03/2026) é uma release de curto
> prazo (não-LTS) que não trouxe nenhuma nova feature *estável* de linguagem —
> os destaques são melhorias de performance (AOT Object Caching, G1 GC),
> previews em amadurecimento (Structured Concurrency, Lazy Constants,
> Primitive Types in Patterns) e remoções que não afetam este projeto
> (Applet API, InfiniBand SDP, RMI Activation). Por isso o código-fonte não
> precisou mudar — só o alvo de compilação, abaixo.

## Estrutura

```
src/br/com/financas/
├── model/
│   ├── Categoria.java
│   ├── Transacao.java
│   ├── Receita.java
│   └── Despesa.java
├── service/
│   └── GerenciadorFinanceiro.java
├── repository/
│   └── TransacaoRepository.java
├── app/
│   └── Main.java              (entrada: versão console/Scanner)
└── gui/
    ├── MainApp.java           (entrada: versão gráfica/Swing)
    ├── MainFrame.java         (janela principal)
    ├── CadastroReceitaDialog.java
    ├── CadastroDespesaDialog.java
    ├── TransacaoTableModel.java
    └── TransacaoRowRenderer.java
```

## Como compilar

Requer o **JDK 26** instalado (não é necessária nenhuma dependência externa —
Swing já faz parte do JDK padrão).

```bash
# A partir da raiz do projeto:
mkdir -p bin
javac --release 26 -d bin -encoding UTF-8 $(find src -name "*.java")
```

`--release 26` garante que o compilador use exatamente a API pública e as
regras de linguagem do Java SE 26 (equivalente a compilar e rodar na mesma
versão). Se preferir Maven/Gradle, basta configurar a *release* do compilador
para `26` (`<maven.compiler.release>26</maven.compiler.release>` ou
`sourceCompatibility = JavaVersion.toVersion(26)`, respectivamente).

## Como executar

**Versão console (Scanner):**
```bash
java -cp bin br.com.financas.app.Main
```

**Versão gráfica (Swing):**
```bash
java -cp bin br.com.financas.gui.MainApp
```

As duas interfaces compartilham exatamente as mesmas camadas `model`,
`service` e `repository` — e o mesmo arquivo `financas.csv` — então dá para
cadastrar uma transação pelo console e ver o mesmo dado carregado na versão
gráfica em outra execução (e vice-versa).

Ao fechar (opção `0` no console, ou fechando a janela na versão gráfica), os
dados são salvos automaticamente em `financas.csv` no diretório de execução,
e serão recarregados na próxima vez que o programa for iniciado.

## Observação

Este projeto não usa nenhum build tool (Maven/Gradle) propositalmente, para
manter o foco em Java puro e nos conceitos de OOP solicitados.
