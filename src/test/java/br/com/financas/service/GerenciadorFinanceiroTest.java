package br.com.financas.service;

import br.com.financas.model.Categoria;
import br.com.financas.model.Despesa;
import br.com.financas.model.Despesa.FormaPagamento;
import br.com.financas.model.Receita;
import br.com.financas.model.Transacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class GerenciadorFinanceiroTest {

    private GerenciadorFinanceiro gerenciador;

    @BeforeEach
    void setUp() {
        gerenciador =
                new GerenciadorFinanceiro();
    }

    @Test
    void deveAdicionarUmaTransacao() {
        Receita receita =
                criarReceita(
                        "Salário",
                        "5000.00",
                        LocalDate.of(
                                2026,
                                9,
                                5
                        )
                );

        gerenciador.adicionar(
                receita
        );

        assertEquals(
                1,
                gerenciador.totalDeTransacoes()
        );

        assertEquals(
                receita,
                gerenciador.buscarPorId(
                        receita.getId()
                )
        );
    }

    @Test
    void naoDeveAdicionarTransacaoNula() {
        IllegalArgumentException excecao =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> gerenciador.adicionar(
                                null
                        )
                );

        assertEquals(
                "A transação não pode ser nula.",
                excecao.getMessage()
        );

        assertEquals(
                0,
                gerenciador.totalDeTransacoes()
        );
    }

    @Test
    void deveRemoverTransacaoPorId() {
        Receita receita =
                criarReceita(
                        "Salário",
                        "5000.00",
                        LocalDate.of(
                                2026,
                                9,
                                5
                        )
                );

        gerenciador.adicionar(
                receita
        );

        Transacao removida =
                gerenciador.removerPorId(
                        receita.getId()
                );

        assertEquals(
                receita,
                removida
        );

        assertEquals(
                0,
                gerenciador.totalDeTransacoes()
        );
    }

    @Test
    void deveLancarErroAoBuscarIdInexistente() {
        NoSuchElementException excecao =
                assertThrows(
                        NoSuchElementException.class,
                        () -> gerenciador.buscarPorId(
                                999999L
                        )
                );

        assertTrue(
                excecao.getMessage()
                        .contains("999999")
        );
    }

    @Test
    void deveListarTransacoesOrdenadasPorData() {
        Despesa despesa =
                criarDespesa(
                        "Mercado",
                        "250.00",
                        LocalDate.of(
                                2026,
                                9,
                                10
                        )
                );

        Receita receita =
                criarReceita(
                        "Salário",
                        "5000.00",
                        LocalDate.of(
                                2026,
                                9,
                                5
                        )
                );

        gerenciador.adicionar(
                despesa
        );

        gerenciador.adicionar(
                receita
        );

        List<Transacao> transacoes =
                gerenciador.listarTodas();

        assertEquals(
                List.of(
                        receita,
                        despesa
                ),
                transacoes
        );
    }

    @Test
    void deveFiltrarPorMesEAno() {
        Receita setembro =
                criarReceita(
                        "Salário",
                        "5000.00",
                        LocalDate.of(
                                2026,
                                9,
                                5
                        )
                );

        Despesa agosto =
                criarDespesa(
                        "Mercado",
                        "250.00",
                        LocalDate.of(
                                2026,
                                8,
                                20
                        )
                );

        gerenciador.adicionar(
                setembro
        );

        gerenciador.adicionar(
                agosto
        );

        List<Transacao> filtradas =
                gerenciador.filtrarPorMesAno(
                        Month.SEPTEMBER,
                        Year.of(2026)
                );

        assertEquals(
                1,
                filtradas.size()
        );

        assertEquals(
                setembro,
                filtradas.getFirst()
        );
    }

    @Test
    void deveFiltrarPorCategoria() {
        Receita salario =
                criarReceita(
                        "Salário",
                        "5000.00",
                        LocalDate.of(
                                2026,
                                9,
                                5
                        )
                );

        Despesa mercado =
                criarDespesa(
                        "Mercado",
                        "250.00",
                        LocalDate.of(
                                2026,
                                9,
                                10
                        )
                );

        gerenciador.adicionar(
                salario
        );

        gerenciador.adicionar(
                mercado
        );

        List<Transacao> filtradas =
                gerenciador.filtrarPorCategoria(
                        Categoria.ALIMENTACAO
                );

        assertEquals(
                1,
                filtradas.size()
        );

        assertEquals(
                mercado,
                filtradas.getFirst()
        );
    }

    @Test
    void deveCalcularTotaisESaldo() {
        gerenciador.adicionar(
                criarReceita(
                        "Salário",
                        "5000.00",
                        LocalDate.of(
                                2026,
                                9,
                                5
                        )
                )
        );

        gerenciador.adicionar(
                criarReceita(
                        "Freelance",
                        "800.00",
                        LocalDate.of(
                                2026,
                                9,
                                8
                        )
                )
        );

        gerenciador.adicionar(
                criarDespesa(
                        "Mercado",
                        "250.00",
                        LocalDate.of(
                                2026,
                                9,
                                10
                        )
                )
        );

        gerenciador.adicionar(
                criarDespesa(
                        "Aluguel",
                        "1500.00",
                        LocalDate.of(
                                2026,
                                9,
                                12
                        )
                )
        );

        assertEquals(
                0,
                new BigDecimal("5800.00")
                        .compareTo(
                                gerenciador
                                        .calcularTotalReceitas()
                        )
        );

        assertEquals(
                0,
                new BigDecimal("1750.00")
                        .compareTo(
                                gerenciador
                                        .calcularTotalDespesas()
                        )
        );

        assertEquals(
                0,
                new BigDecimal("4050.00")
                        .compareTo(
                                gerenciador
                                        .calcularSaldo()
                        )
        );
    }

    @Test
    void deveManterPrecisaoDecimalNosCalculosMonetarios() {
        gerenciador.adicionar(
                criarReceita(
                        "Ajuste 1",
                        "0.10",
                        LocalDate.of(
                                2026,
                                9,
                                1
                        )
                )
        );

        gerenciador.adicionar(
                criarReceita(
                        "Ajuste 2",
                        "0.20",
                        LocalDate.of(
                                2026,
                                9,
                                1
                        )
                )
        );

        assertEquals(
                0,
                new BigDecimal("0.30")
                        .compareTo(
                                gerenciador
                                        .calcularTotalReceitas()
                        )
        );

        assertEquals(
                0,
                new BigDecimal("0.30")
                        .compareTo(
                                gerenciador
                                        .calcularSaldo()
                        )
        );
    }

    private Receita criarReceita(
            String descricao,
            String valor,
            LocalDate data
    ) {
        return new Receita(
                descricao,
                new BigDecimal(valor),
                data,
                Categoria.SALARIO,
                "Empresa"
        );
    }

    private Despesa criarDespesa(
            String descricao,
            String valor,
            LocalDate data
    ) {
        return new Despesa(
                descricao,
                new BigDecimal(valor),
                data,
                Categoria.ALIMENTACAO,
                FormaPagamento.PIX
        );
    }
}