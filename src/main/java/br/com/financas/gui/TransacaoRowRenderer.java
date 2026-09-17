package br.com.financas.gui;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;

/**
 * Renderer customizado: pinta a linha de verde clarinho para Receita e vermelho
 * clarinho para Despesa, dando um feedback visual imediato do tipo de
 * lançamento
 * sem precisar ler a coluna "Tipo".
 *
 * BOA PRÁTICA (Swing): a coluna "Tipo" é a coluna de índice 1 no
 * TransacaoTableModel;
 * usamos o próprio valor renderizado da tabela para decidir a cor, em vez de
 * guardar
 * estado duplicado aqui — assim o renderer nunca fica dessincronizado do
 * modelo.
 */
public class TransacaoRowRenderer extends DefaultTableCellRenderer {

    private static final Color VERDE_CLARO = new Color(224, 247, 224);
    private static final Color VERMELHO_CLARO = new Color(253, 224, 224);
    private static final Color SELECAO = new Color(184, 207, 229);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (isSelected) {
            c.setBackground(SELECAO);
            return c;
        }

        Object tipo = table.getModel().getValueAt(table.convertRowIndexToModel(row), 1);
        if ("RECEITA".equals(tipo)) {
            c.setBackground(VERDE_CLARO);
        } else if ("DESPESA".equals(tipo)) {
            c.setBackground(VERMELHO_CLARO);
        } else {
            c.setBackground(Color.WHITE);
        }
        return c;
    }
}
