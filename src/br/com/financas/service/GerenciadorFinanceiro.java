package br.com.financas.service;

import br.com.financas.model.Categoria;
import br.com.financas.model.Receita;
import br.com.financas.model.Transacao;

import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Camada de SERVIÇO (regra de negócio).
 *
 * BOA PRÁTICA - Separação de responsabilidades (Single Responsibility Principle):
 * esta classe NÃO sabe nada sobre Scanner/console (isso é do Main) nem sobre
 * arquivos/CSV (isso é do Repository). Ela só conhece regras de negócio: como
 * adicionar, remover, filtrar e calcular totais sobre a lista de transações.
 * Isso torna a lógica testável e reutilizável (poderia ser usada por uma API REST
 * no futuro, por exemplo, sem alterar uma linha sequer desta classe).
 */
public class GerenciadorFinanceiro {

    private final List<Transacao> transacoes;

    public GerenciadorFinanceiro() {
        this.transacoes = new ArrayList<>();
    }

    /**
     * Permite inicializar o gerenciador já com uma lista de transações
     * (normalmente vinda do repositório, ao carregar o arquivo CSV na inicialização).
     */
    public GerenciadorFinanceiro(List<Transacao> transacoesIniciais) {
        this.transacoes = new ArrayList<>(transacoesIniciais);
    }

    public void adicionar(Transacao transacao) {
        if (transacao == null) {
            throw new IllegalArgumentException("A transação não pode ser nula.");
        }
        transacoes.add(transacao);
    }

    /**
     * Remove uma transação pelo ID.
     *
     * @throws NoSuchElementException se nenhuma transação com esse ID existir,
     *         permitindo que a camada de apresentação (Main) trate o erro de forma
     *         amigável para o usuário.
     */
    public Transacao removerPorId(Long id) {
        Transacao alvo = buscarPorId(id);
        transacoes.remove(alvo);
        return alvo;
    }

    public Transacao buscarPorId(Long id) {
        return transacoes.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Nenhuma transação encontrada com o ID " + id));
    }

    /**
     * Retorna uma cópia da lista (ordenada por data) para evitar que código externo
     * modifique a lista interna diretamente, quebrando o encapsulamento do serviço.
     */
    public List<Transacao> listarTodas() {
        return transacoes.stream()
                .sorted(Comparator.comparing(Transacao::getData))
                .collect(Collectors.toList());
    }

    public List<Transacao> filtrarPorMesAno(Month mes, Year ano) {
        return transacoes.stream()
                .filter(t -> t.getData().getMonth() == mes && t.getData().getYear() == ano.getValue())
                .sorted(Comparator.comparing(Transacao::getData))
                .collect(Collectors.toList());
    }

    public List<Transacao> filtrarPorCategoria(Categoria categoria) {
        return transacoes.stream()
                .filter(t -> t.getCategoria() == categoria)
                .sorted(Comparator.comparing(Transacao::getData))
                .collect(Collectors.toList());
    }

    /**
     * POLIMORFISMO em ação: percorremos uma lista de Transacao (tipo base) e
     * usamos "instanceof" / getTipo() apenas para SOMAR; o comportamento de
     * "como se formata" ou "quais atributos tem" continua encapsulado em cada subclasse.
     */
    public double calcularTotalReceitas() {
        return transacoes.stream()
                .filter(t -> t instanceof Receita)
                .mapToDouble(Transacao::getValor)
                .sum();
    }

    public double calcularTotalDespesas() {
        return transacoes.stream()
                .filter(t -> !(t instanceof Receita))
                .mapToDouble(Transacao::getValor)
                .sum();
    }

    public double calcularSaldo() {
        return calcularTotalReceitas() - calcularTotalDespesas();
    }

    public int totalDeTransacoes() {
        return transacoes.size();
    }
}
