package br.com.financas.model;

import java.time.LocalDate;

/**
 * Representa uma saída de dinheiro (Despesa).
 *
 * HERANÇA: reaproveita id, descrição, valor, data e categoria de Transacao.
 * POLIMORFISMO: implementa getTipo() retornando "DESPESA".
 */
public class Despesa extends Transacao {

    /**
     * Enum interno (nested enum): faz sentido morar dentro de Despesa porque
     * "forma de pagamento" só existe no contexto de uma despesa (não se paga uma
     * receita com cartão de crédito). Encapsula essa regra de domínio na própria classe.
     */
    public enum FormaPagamento {
        PIX("Pix"),
        CARTAO_CREDITO("Cartão de Crédito"),
        CARTAO_DEBITO("Cartão de Débito"),
        DINHEIRO("Dinheiro"),
        BOLETO("Boleto");

        private final String descricao;

        FormaPagamento(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    private FormaPagamento formaPagamento;

    public Despesa(String descricao, double valor, LocalDate data, Categoria categoria, FormaPagamento formaPagamento) {
        super(descricao, valor, data, categoria);
        setFormaPagamento(formaPagamento);
    }

    /**
     * Construtor usado ao recarregar dados do CSV, preservando o ID original.
     */
    public Despesa(Long id, String descricao, double valor, LocalDate data, Categoria categoria, FormaPagamento formaPagamento) {
        super(id, descricao, valor, data, categoria);
        setFormaPagamento(formaPagamento);
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        if (formaPagamento == null) {
            throw new IllegalArgumentException("A forma de pagamento não pode ser nula.");
        }
        this.formaPagamento = formaPagamento;
    }

    @Override
    public String getTipo() {
        return "DESPESA";
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Pagamento: %s", formaPagamento.getDescricao());
    }
}
