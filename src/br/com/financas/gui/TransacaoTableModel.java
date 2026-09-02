package br.com.financas.gui;

import br.com.financas.model.Despesa;
import br.com.financas.model.Receita;
import br.com.financas.model.Transacao;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * TableModel customizado: adapta a nossa List<Transacao> (modelo de domínio)
 * para o formato que o JTable entende.
 *
 * BOA PRÁTICA: o Swing já nos dá o AbstractTableModel exatamente para não termos
 * que reimplementar listeners de mudança de dados — só precisamos dizer QUANTAS
 * linhas/colunas existem e COMO extrair cada célula, além de chamar
 * fireTableDataChanged() quando os dados mudam, para a tabela se redesenhar sozinha.
 *
 * POLIMORFISMO: getValueAt() trata Transacao de forma genérica e só faz
 * "instanceof" para extrair o único campo que realmente difere entre Receita e
 * Despesa (a coluna "Detalhe"). Todo o resto (id, descrição, valor, data, categoria)
 * vem da própria classe abstrata.
 */
public class TransacaoTableModel extends AbstractTableModel {

    private static final String[] COLUNAS = {
            "ID", "Tipo", "Descrição", "Categoria", "Valor (R$)", "Data", "Detalhe"
    };

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private List<Transacao> transacoes = new ArrayList<>();

    /** Substitui os dados exibidos e notifica o JTable para se redesenhar. */
    public void setTransacoes(List<Transacao> transacoes) {
        this.transacoes = new ArrayList<>(transacoes);
        fireTableDataChanged();
    }

    /**
     * Retorna a Transacao correspondente a uma linha do MODELO (não da view!).
     * Quando há ordenação ativa (RowSorter), a MainFrame é responsável por
     * converter o índice da view para o índice do modelo antes de chamar isto.
     */
    public Transacao getTransacaoNaLinha(int linhaModelo) {
        return transacoes.get(linhaModelo);
    }

    @Override
    public int getRowCount() {
        return transacoes.size();
    }

    @Override
    public int getColumnCount() {
        return COLUNAS.length;
    }

    @Override
    public String getColumnName(int coluna) {
        return COLUNAS[coluna];
    }

    @Override
    public Class<?> getColumnClass(int coluna) {
        // ID como Long faz o JTable ordenar numericamente (e não como texto) ao clicar no cabeçalho.
        return coluna == 0 ? Long.class : String.class;
    }

    @Override
    public boolean isCellEditable(int linha, int coluna) {
        // A tabela é somente leitura: edição acontece via diálogos dedicados,
        // não diretamente na célula, para reaproveitar as mesmas validações do model.
        return false;
    }

    @Override
    public Object getValueAt(int linha, int coluna) {
        Transacao t = transacoes.get(linha);
        switch (coluna) {
            case 0:
                return t.getId();
            case 1:
                return t.getTipo();
            case 2:
                return t.getDescricao();
            case 3:
                return t.getCategoria().getDescricao();
            case 4:
                return String.format("R$ %,.2f", t.getValor());
            case 5:
                return t.getData().format(FORMATO_DATA);
            case 6:
                return extrairDetalhe(t);
            default:
                return "";
        }
    }

    private String extrairDetalhe(Transacao t) {
        if (t instanceof Receita) {
            Receita receita = (Receita) t;
            return "Fonte: " + receita.getFonte();
        }
        if (t instanceof Despesa) {
            Despesa despesa = (Despesa) t;
            return despesa.getFormaPagamento().getDescricao();
        }
        return "";
    }
}
