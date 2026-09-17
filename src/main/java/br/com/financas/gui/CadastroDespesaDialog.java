package br.com.financas.gui;

import br.com.financas.model.Categoria;
import br.com.financas.model.Despesa;
import br.com.financas.model.Despesa.FormaPagamento;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.math.BigDecimal;

/**
 * JDialog modal para cadastro de uma Despesa.
 * Segue exatamente a mesma estrutura de CadastroReceitaDialog, mas troca o
 * campo "Fonte" (texto livre) por um JComboBox de FormaPagamento (enum interno
 * de Despesa) — reforçando, na própria UI, a diferença de modelagem entre as
 * duas subclasses de Transacao.
 */
public class CadastroDespesaDialog extends JDialog {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final JTextField campoDescricao = new JTextField();
    private final JTextField campoValor = new JTextField();
    private final JTextField campoData = new JTextField(LocalDate.now().format(FORMATO_DATA));
    private final JComboBox<Categoria> comboCategoria = new JComboBox<>(Categoria.values());
    private final JComboBox<FormaPagamento> comboFormaPagamento = new JComboBox<>(FormaPagamento.values());

    private Despesa resultado;

    public CadastroDespesaDialog(Frame owner) {
        super(owner, "Cadastrar Despesa", true);
        montarInterface();
        pack();
        setLocationRelativeTo(owner);
    }

    private void montarInterface() {
        JPanel painelCampos = new JPanel(new GridLayout(0, 2, 8, 8));
        painelCampos.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        painelCampos.add(new JLabel("Descrição:"));
        painelCampos.add(campoDescricao);

        painelCampos.add(new JLabel("Valor (R$):"));
        painelCampos.add(campoValor);

        painelCampos.add(new JLabel("Data (dd/MM/aaaa):"));
        painelCampos.add(campoData);

        painelCampos.add(new JLabel("Categoria:"));
        painelCampos.add(comboCategoria);

        painelCampos.add(new JLabel("Forma de pagamento:"));
        painelCampos.add(comboFormaPagamento);

        JButton botaoSalvar = new JButton("Salvar");
        JButton botaoCancelar = new JButton("Cancelar");
        botaoSalvar.addActionListener(e -> tentarSalvar());
        botaoCancelar.addActionListener(e -> dispose());

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.add(botaoCancelar);
        painelBotoes.add(botaoSalvar);

        getRootPane().setDefaultButton(botaoSalvar);

        setLayout(new BorderLayout());
        add(painelCampos, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void tentarSalvar() {
        try {
            String descricao = campoDescricao.getText();
            BigDecimal valor = parseValor(campoValor.getText());
            LocalDate data = parseData(campoData.getText());
            Categoria categoria = (Categoria) comboCategoria.getSelectedItem();
            FormaPagamento formaPagamento = (FormaPagamento) comboFormaPagamento.getSelectedItem();

            this.resultado = new Despesa(descricao, valor, data, categoria, formaPagamento);
            dispose();
        } catch (NumberFormatException e) {
            mostrarErro("Valor inválido. Digite apenas números (ex.: 150.90).");
        } catch (DateTimeParseException e) {
            mostrarErro("Data inválida. Use o formato dd/MM/aaaa (ex.: 25/12/2025).");
        } catch (IllegalArgumentException e) {
            mostrarErro(e.getMessage());
        }
    }

    private BigDecimal parseValor(String texto) {
        return new BigDecimal(texto.trim().replace(",", "."));
    }

    private LocalDate parseData(String texto) {
        return LocalDate.parse(texto.trim(), FORMATO_DATA);
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro de validação", JOptionPane.ERROR_MESSAGE);
    }

    public Despesa getResultado() {
        return resultado;
    }
}
