package br.com.financas.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public abstract class Transacao {
    private static long contadorid = 0;
    private final Long id;
    private String descricao;
    private double valor;
    private LocalDate data;
    private Categoria categoria;

    public Transacao(String descricao, double valor, LocalDate data, Categoria categoria) {
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor da transação deve ser maior que zero.");
        }
        this.id = ++contadorid;
        this.descricao = Objects.requireNonNull(descricao, "A descrição não pode ser nula.");
        this.valor = valor;
        this.data = Objects.requireNonNull(data, "A data não pode ser nula.");
        this.categoria = Objects.requireNonNull(categoria, "A categoria não pode ser nula.");
    }

    public abstract String getTipo();

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = Objects.requireNonNull(descricao, "A descrição não pode ser nula.");
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor da transação deve ser maior que zero.");
        }
        this.valor = valor;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = Objects.requireNonNull(data, "A data não pode ser nula.");
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = Objects.requireNonNull(categoria, "A categoria não pode ser nula.");
    }

    @Override
    public String toString() {
        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return String.format("[%s] ID: %d | Data: %s | Categoria: %-12s | Descrição: %-20s | Valor: R$ %8.2f",
                getTipo(),
                id,
                data.format(formatoData),
                categoria,
                descricao,
                valor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Transacao transacao = (Transacao) o;
        return Objects.equals(id, transacao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}