public class Receita extends Transacao {

    private String fonte;

    public Receita(String descricao, double valor, LocalDate data, Categoria categoria) {
        super(descricao, valor, data, categoria);
        this.fonte = fonte;
    }

    @Override
    public String getTipo() {
        return "Receita";
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("| Fonte: %s", fonte);
    }

}
