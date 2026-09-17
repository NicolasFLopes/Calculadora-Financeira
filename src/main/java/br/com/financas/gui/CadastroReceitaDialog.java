package br.com.financas.gui;

import br.com.financas.model.Categoria;
import br.com.financas.model.Receita;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.math.BigDecimal;

/**
 * JDialog modal para cadastro de uma Receita.
 *
 * BOA PRÁTICA (separação de responsabilidades): este diálogo SÓ sabe montar o
 * formulário e, ao clicar em "Salvar", construir um objeto Receita (delegando
 * a validação de negócio para os setters da própria classe Receita/Transacao —
 * não duplicamos regras de validação aqui). Se a criação falhar (IllegalArgumentException),
 * mostramos o erro e mantemos o diálogo aberto para o usuário corrigir.
 * A MainFrame não conhece nenhum detalhe de campo de formulário: só chama
 * getResultado() depois que o diálogo fecha.
 */
public class CadastroReceitaDialog extends JDialog {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final JTextField campoDescricao = new JTextField();
    private final JTextField campoValor = new JTextField();
    private final JTextField campoData = new JTextField(LocalDate.now().format(FORMATO_DATA));
    private final JComboBox<Categoria> comboCategoria = new JComboBox<>(Categoria.values());
    private final JTextField campoFonte = new JTextField();

    private Receita resultado; // permanece null se o usuário cancelar

    public CadastroReceitaDialog(Frame owner) {
        super(owner, "Cadastrar Receita", true); // true = modal, bloqueia a janela principal
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

        painelCampos.add(new JLabel("Fonte (ex.: Empresa, Freelance):"));
        painelCampos.add(campoFonte);

        JButton botaoSalvar = new JButton("Salvar");
        JButton botaoCancelar = new JButton("Cancelar");
        botaoSalvar.addActionListener(e -> tentarSalvar());
        botaoCancelar.addActionListener(e -> dispose());

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.add(botaoCancelar);
        painelBotoes.add(botaoSalvar);

        // ENTER no último campo já dispara o salvamento, agilizando o cadastro.
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
            String fonte = campoFonte.getText();

            // Toda a validação "de negócio" (campo vazio, valor <= 0, data futura, etc.)
            // acontece dentro do construtor de Receita — reaproveitando o model.
            this.resultado = new Receita(descricao, valor, data, categoria, fonte);
            dispose();
        } catch (NumberFormatException e) {
            mostrarErro("Valor inválido. Digite apenas números (ex.: 150.90).");
        } catch (DateTimeParseException e) {
            mostrarErro("Data inválida. Use o formato dd/MM/aaaa (ex.: 25/12/2025).");
        } catch (IllegalArgumentException e) {
            // Erros vindos das validações do model (Transacao/Receita).
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

    /** @return a Receita criada, ou null se o usuário cancelou o cadastro. */
    public Receita getResultado() {
        return resultado;
    }
}
