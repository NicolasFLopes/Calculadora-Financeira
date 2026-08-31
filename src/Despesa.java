public class Despesa extends Transacao {

    public enum FormaPagamento {
        PIX, CARTAO_CREDITO, CARTAO_DEBITO, DINHEIRO, BOLETO, CHEQUE
    }

    private FormaPagameto formaPagamento;

    public Despesa(String descricao, double valor, LocalDate data, Categoria categoria, FormaPagamento formaPagamento) {
        super(descricao, valor, data, categoria);
        this.formaPagamento = formaPagamento;
    }

    @Override
    public String getTipo() {
        return "Despesa";
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("| Forma de Pagamento: %s", formaPagamento);
    }
}
