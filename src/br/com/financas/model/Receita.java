package br.com.financas.model;

import java.time.LocalDate;

/**
 * Representa uma entrada de dinheiro (Receita).
 *
 * HERANÇA: reaproveita id, descrição, valor, data e categoria de Transacao.
 * POLIMORFISMO: implementa getTipo() de forma específica, retornando "RECEITA".
 */
public class Receita extends Transacao {

    private String fonte; // Ex.: "Empresa X", "Freelance", "Aluguel recebido"

    public Receita(String descricao, double valor, LocalDate data, Categoria categoria, String fonte) {
        super(descricao, valor, data, categoria);
        setFonte(fonte);
    }

    /**
     * Construtor usado ao recarregar dados do CSV, preservando o ID original.
     */
    public Receita(Long id, String descricao, double valor, LocalDate data, Categoria categoria, String fonte) {
        super(id, descricao, valor, data, categoria);
        setFonte(fonte);
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        if (fonte == null || fonte.isBlank()) {
            throw new IllegalArgumentException("A fonte da receita não pode ser nula ou vazia.");
        }
        this.fonte = fonte.trim();
    }

    @Override
    public String getTipo() {
        return "RECEITA";
    }

    @Override
    public String toString() {
        // Reaproveita a formatação padrão da superclasse e acrescenta o dado específico.
        return super.toString() + String.format(" | Fonte: %s", fonte);
    }
}
