packege br.com.finacas.model;

public enum Categoria {

    ALIMENTACAO("Alimentação"),
    MORADIA("Moradia"),
    TRANSPORTE("Transporte"),
    LAZER("Lazer"),
    SAUDE("Saúde"),
    EDUCACAO("Educação"),
    SALARIO("Salário"),
    INVESTIMENTO("Investimento"),
    OUTROS("Outros");

    private final String descricao;

    Categoria(String descricao){
        this.descricao = descricao;
    }

    public String getDescricao(){
        return descricao;
    }

    @Override
    public String toString(){
        return descricao;
    }

}
