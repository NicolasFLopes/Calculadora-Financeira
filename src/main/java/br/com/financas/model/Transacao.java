package br.com.financas.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Classe abstrata que representa uma transação financeira genérica.
 *
 * CONCEITOS DE POO APLICADOS:
 * - ABSTRAÇÃO: "Transacao" é um conceito genérico que nunca deve ser instanciado
 *   diretamente; só faz sentido no mundo real como Receita ou Despesa. Por isso é
 *   abstrata e expõe um método abstrato getTipo().
 * - ENCAPSULAMENTO: todos os atributos são privados e só podem ser alterados através
 *   de setters que validam o estado do objeto (fail-fast), evitando que a aplicação
 *   entre em um estado inconsistente (ex.: valor negativo).
 * - HERANÇA: Receita e Despesa estendem esta classe e reaproveitam atributos e
 *   comportamentos comuns (id, descricao, valor, data, categoria).
 */
public abstract class Transacao {

    // Contador estático usado para gerar IDs auto-incrementais.
    // É estático porque o "próximo ID" é um estado compartilhado por TODAS as
    // transações do sistema, não um estado de uma transação individual.
    private static long proximoId = 1;

    private final Long id;
    private String descricao;
    private BigDecimal valor;
    private LocalDate data;
    private Categoria categoria;

    protected Transacao(String descricao, BigDecimal valor, LocalDate data, Categoria categoria) {
        this.id = proximoId++;
        setDescricao(descricao);
        setValor(valor);
        setData(data);
        setCategoria(categoria);
    }

    /**
     * Construtor usado exclusivamente pelo repositório ao RECARREGAR transações
     * a partir do arquivo CSV, onde o ID já existe e não deve ser gerado novamente.
     * Também é responsável por atualizar o contador estático para não repetir IDs.
     */
    protected Transacao(Long id, String descricao, BigDecimal valor, LocalDate data, Categoria categoria) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido ao reconstruir transação: " + id);
        }
        this.id = id;
        setDescricao(descricao);
        setValor(valor);
        setData(data);
        setCategoria(categoria);

        // Garante que o próximo ID gerado nunca colida com um ID já existente no arquivo.
        if (id >= proximoId) {
            proximoId = id + 1;
        }
    }

    // ----------------- Getters -----------------

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDate getData() {
        return data;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    // ----------------- Setters com validação (encapsulamento real, não "getters/setters anêmicos") -----------------

    public void setDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("A descrição não pode ser nula ou vazia.");
        }
        this.descricao = descricao.trim();
    }

    public void setValor(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da transação deve ser maior que zero.");
        }
        this.valor = valor;
    }

    public void setData(LocalDate data) {
        if (data == null) {
            throw new IllegalArgumentException("A data não pode ser nula.");
        }
        if (data.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data não pode ser futura.");
        }
        this.data = data;
    }

    public void setCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("A categoria não pode ser nula.");
        }
        this.categoria = categoria;
    }

    /**
     * POLIMORFISMO: cada subclasse (Receita/Despesa) implementa esse método de forma
     * diferente, e o restante do sistema (relatórios, CSV, menu) pode chamar
     * transacao.getTipo() sem saber qual é a subclasse concreta em tempo de execução.
     */
    public abstract TipoTransacao getTipo();

    /**
     * Método utilitário reaproveitado pelas subclasses para formatar o valor em
     * moeda brasileira sem duplicar a lógica de formatação em cada toString().
     */
    protected String formatarValor() {
        return String.format("R$ %,.2f", valor);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return String.format("[#%d] %-8s | %-25s | %-15s | %10s | %s",
                id, getTipo(), descricao, categoria.getDescricao(), formatarValor(), data.format(formatter));
    }

    /**
     * equals() e hashCode() baseados apenas no ID: em um domínio de negócio, duas
     * transações são "a mesma transação" se e somente se possuem o mesmo identificador,
     * independentemente de outros atributos terem sido alterados posteriormente.
     * Isso é essencial para operações como List.remove(Transacao) e uso em coleções
     * baseadas em hash (HashSet, HashMap).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transacao)) return false;
        Transacao that = (Transacao) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
