package br.com.financas.model;

/**
 * Enum que representa as categorias possíveis de uma transação financeira.
 *
 * BOA PRÁTICA: usar Enum em vez de String/int para categorias evita "magic strings",
 * garante segurança de tipos em tempo de compilação e centraliza a descrição amigável
 * (em português) junto com a constante, evitando duplicação de lógica de formatação
 * espalhada pelo sistema.
 */
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

    Categoria(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        // Sobrescrever toString() permite que o enum seja impresso de forma amigável
        // diretamente em menus e relatórios, sem precisar chamar getDescricao() sempre.
        return descricao;
    }
}
