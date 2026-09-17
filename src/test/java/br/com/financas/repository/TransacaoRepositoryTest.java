package br.com.financas.repository;

import br.com.financas.model.Categoria;
import br.com.financas.model.Despesa;
import br.com.financas.model.Despesa.FormaPagamento;
import br.com.financas.model.Receita;
import br.com.financas.model.Transacao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransacaoRepositoryTest {

    @TempDir
    Path diretorioTemporario;

    @Test
    void deveSalvarECarregarTransacoes() {
        Path arquivo =
                diretorioTemporario.resolve(
                        "financas-teste.csv"
                );

        TransacaoRepository repository =
                new TransacaoRepository(
                        arquivo.toString()
                );

        Receita receita = new Receita(
                100L,
                "Salário",
                new BigDecimal("5000.00"),
                LocalDate.of(2026, 9, 5),
                Categoria.SALARIO,
                "Empresa"
        );

        Despesa despesa = new Despesa(
                101L,
                "Mercado",
                new BigDecimal("250.90"),
                LocalDate.of(2026, 9, 10),
                Categoria.ALIMENTACAO,
                FormaPagamento.PIX
        );

        repository.salvar(
                List.of(receita, despesa)
        );

        List<Transacao> carregadas =
                repository.carregar();

        assertEquals(2, carregadas.size());

        assertInstanceOf(
                Receita.class,
                carregadas.get(0)
        );

        Receita receitaCarregada =
                (Receita) carregadas.get(0);

        assertEquals(
                receita.getId(),
                receitaCarregada.getId()
        );

        assertEquals(
                receita.getDescricao(),
                receitaCarregada.getDescricao()
        );

        assertEquals(
                0,
                receita.getValor()
                        .compareTo(
                                receitaCarregada.getValor()
                        )
        );

        assertEquals(
                receita.getData(),
                receitaCarregada.getData()
        );

        assertEquals(
                receita.getCategoria(),
                receitaCarregada.getCategoria()
        );

        assertEquals(
                receita.getFonte(),
                receitaCarregada.getFonte()
        );

        assertInstanceOf(
                Despesa.class,
                carregadas.get(1)
        );

        Despesa despesaCarregada =
                (Despesa) carregadas.get(1);

        assertEquals(
                despesa.getId(),
                despesaCarregada.getId()
        );

        assertEquals(
                despesa.getDescricao(),
                despesaCarregada.getDescricao()
        );

        assertEquals(
                0,
                despesa.getValor()
                        .compareTo(
                                despesaCarregada.getValor()
                        )
        );

        assertEquals(
                despesa.getData(),
                despesaCarregada.getData()
        );

        assertEquals(
                despesa.getCategoria(),
                despesaCarregada.getCategoria()
        );

        assertEquals(
                despesa.getFormaPagamento(),
                despesaCarregada.getFormaPagamento()
        );
    }

    @Test
    void deveRetornarListaVaziaQuandoArquivoNaoExiste() {
        Path arquivo =
                diretorioTemporario.resolve(
                        "arquivo-inexistente.csv"
                );

        TransacaoRepository repository =
                new TransacaoRepository(
                        arquivo.toString()
                );

        List<Transacao> carregadas =
                repository.carregar();

        assertNotNull(carregadas);
        assertTrue(carregadas.isEmpty());
    }
}