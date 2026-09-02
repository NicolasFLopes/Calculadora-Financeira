package br.com.financas.gui;

import br.com.financas.model.Transacao;
import br.com.financas.repository.TransacaoRepository;
import br.com.financas.service.GerenciadorFinanceiro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

/**
 * Janela principal da aplicação (Swing).
 *
 * ARQUITETURA: a MainFrame é só "cola" entre a UI e as camadas já existentes
 * (GerenciadorFinanceiro para regras de negócio, TransacaoRepository para
 * persistência) — exatamente o mesmo papel que a classe app.Main tinha na
 * versão console. Nenhuma regra de negócio foi duplicada aqui.
 */
public class MainFrame extends JFrame {

    private final TransacaoRepository repository = new TransacaoRepository();
    private final GerenciadorFinanceiro gerenciador;

    private final TransacaoTableModel tableModel = new TransacaoTableModel();
    private final JTable tabela = new JTable(tableModel);

    private final JLabel labelTotalReceitas = new JLabel();
    private final JLabel labelTotalDespesas = new JLabel();
    private final JLabel labelSaldo = new JLabel();

    private final JComboBox<String> comboMes = new JComboBox<>(nomesDosMeses());
    private final JSpinner spinnerAno = new JSpinner(new SpinnerNumberModel(Year.now().getValue(), 1900, 2100, 1));
    private boolean filtroAtivo = false;

    public MainFrame() {
        super("Calculadora de Finanças Pessoais");

        // Carrega os dados persistidos assim que a janela é criada — mesmo
        // comportamento da versão console.
        List<Transacao> transacoesCarregadas = repository.carregar();
        this.gerenciador = new GerenciadorFinanceiro(transacoesCarregadas);

        montarInterface();
        atualizarTabela(gerenciador.listarTodas());
        atualizarResumo();

        // Salva automaticamente ao fechar a janela, assim como a opção "0" do console.
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salvarESair();
            }
        });

        setSize(950, 550);
        setLocationRelativeTo(null); // centraliza na tela
    }

    private void montarInterface() {
        setLayout(new BorderLayout(8, 8));

        add(montarBarraDeAcoes(), BorderLayout.NORTH);
        add(montarPainelTabela(), BorderLayout.CENTER);
        add(montarPainelResumo(), BorderLayout.SOUTH);
    }

    // ---------------------------------------------------------------
    // Barra superior: cadastro e filtro
    // ---------------------------------------------------------------

    private JPanel montarBarraDeAcoes() {
        JPanel painel = new JPanel(new BorderLayout());

        JPanel painelCadastro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton botaoNovaReceita = new JButton("+ Receita");
        JButton botaoNovaDespesa = new JButton("+ Despesa");
        JButton botaoRemover = new JButton("Remover Selecionada");

        botaoNovaReceita.addActionListener(e -> cadastrarReceita());
        botaoNovaDespesa.addActionListener(e -> cadastrarDespesa());
        botaoRemover.addActionListener(e -> removerSelecionada());

        painelCadastro.add(botaoNovaReceita);
        painelCadastro.add(botaoNovaDespesa);
        painelCadastro.add(botaoRemover);

        JPanel painelFiltro = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton botaoFiltrar = new JButton("Filtrar Mês/Ano");
        JButton botaoLimparFiltro = new JButton("Limpar Filtro");

        botaoFiltrar.addActionListener(e -> aplicarFiltroMesAno());
        botaoLimparFiltro.addActionListener(e -> limparFiltro());

        painelFiltro.add(new JLabel("Mês:"));
        painelFiltro.add(comboMes);
        painelFiltro.add(new JLabel("Ano:"));
        painelFiltro.add(spinnerAno);
        painelFiltro.add(botaoFiltrar);
        painelFiltro.add(botaoLimparFiltro);

        painel.add(painelCadastro, BorderLayout.WEST);
        painel.add(painelFiltro, BorderLayout.EAST);
        return painel;
    }

    // ---------------------------------------------------------------
    // Tabela central
    // ---------------------------------------------------------------

    private JScrollPane montarPainelTabela() {
        tabela.setDefaultRenderer(Object.class, new TransacaoRowRenderer());
        tabela.setRowHeight(24);
        tabela.setAutoCreateRowSorter(true); // permite ordenar clicando no cabeçalho da coluna
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return new JScrollPane(tabela);
    }

    // ---------------------------------------------------------------
    // Painel inferior: resumo financeiro (sempre visível e atualizado)
    // ---------------------------------------------------------------

    private JPanel montarPainelResumo() {
        JPanel painel = new JPanel(new GridLayout(1, 3, 16, 0));
        painel.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));

        labelTotalReceitas.setFont(labelTotalReceitas.getFont().deriveFont(Font.BOLD, 14f));
        labelTotalDespesas.setFont(labelTotalDespesas.getFont().deriveFont(Font.BOLD, 14f));
        labelSaldo.setFont(labelSaldo.getFont().deriveFont(Font.BOLD, 14f));

        painel.add(labelTotalReceitas);
        painel.add(labelTotalDespesas);
        painel.add(labelSaldo);
        return painel;
    }

    // ---------------------------------------------------------------
    // Ações
    // ---------------------------------------------------------------

    private void cadastrarReceita() {
        CadastroReceitaDialog dialogo = new CadastroReceitaDialog(this);
        dialogo.setVisible(true); // bloqueia aqui até o diálogo fechar (é modal)

        if (dialogo.getResultado() != null) {
            gerenciador.adicionar(dialogo.getResultado());
            limparFiltro(); // volta a exibir tudo, incluindo o item recém-criado
            JOptionPane.showMessageDialog(this, "Receita cadastrada com sucesso!");
        }
    }

    private void cadastrarDespesa() {
        CadastroDespesaDialog dialogo = new CadastroDespesaDialog(this);
        dialogo.setVisible(true);

        if (dialogo.getResultado() != null) {
            gerenciador.adicionar(dialogo.getResultado());
            limparFiltro();
            JOptionPane.showMessageDialog(this, "Despesa cadastrada com sucesso!");
        }
    }

    private void removerSelecionada() {
        int linhaView = tabela.getSelectedRow();
        if (linhaView == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma transação na tabela primeiro.",
                    "Nenhuma seleção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Como a tabela pode estar ordenada (RowSorter), é preciso converter o
        // índice visível (view) para o índice real do modelo antes de buscar o dado.
        int linhaModelo = tabela.convertRowIndexToModel(linhaView);
        Transacao transacao = tableModel.getTransacaoNaLinha(linhaModelo);

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Remover a transação:\n" + transacao + " ?",
                "Confirmar remoção", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            gerenciador.removerPorId(transacao.getId());
            reaplicarVisualizacaoAtual();
        } catch (NoSuchElementException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aplicarFiltroMesAno() {
        Month mes = Month.of(comboMes.getSelectedIndex() + 1);
        int ano = (Integer) spinnerAno.getValue();

        List<Transacao> filtradas = gerenciador.filtrarPorMesAno(mes, Year.of(ano));
        filtroAtivo = true;
        atualizarTabela(filtradas);

        if (filtradas.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nenhuma transação encontrada para " + comboMes.getSelectedItem() + "/" + ano + ".",
                    "Filtro sem resultados", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void limparFiltro() {
        filtroAtivo = false;
        atualizarTabela(gerenciador.listarTodas());
    }

    /** Depois de adicionar/remover, reaplica o mesmo filtro que estava ativo (se houver). */
    private void reaplicarVisualizacaoAtual() {
        if (filtroAtivo) {
            aplicarFiltroMesAno();
        } else {
            atualizarTabela(gerenciador.listarTodas());
        }
    }

    private void atualizarTabela(List<Transacao> transacoes) {
        tableModel.setTransacoes(transacoes);
        atualizarResumo();
    }

    /**
     * O resumo financeiro (receitas/despesas/saldo) é sempre calculado sobre
     * TODAS as transações — não sobre o resultado do filtro — para que o usuário
     * nunca confunda "saldo do mês filtrado" com "saldo geral".
     */
    private void atualizarResumo() {
        double totalReceitas = gerenciador.calcularTotalReceitas();
        double totalDespesas = gerenciador.calcularTotalDespesas();
        double saldo = gerenciador.calcularSaldo();

        labelTotalReceitas.setText(String.format("Receitas: R$ %,.2f", totalReceitas));
        labelTotalDespesas.setText(String.format("Despesas: R$ %,.2f", totalDespesas));
        labelSaldo.setText(String.format("Saldo: %sR$ %,.2f", saldo < 0 ? "-" : "", Math.abs(saldo)));
        labelSaldo.setForeground(saldo >= 0 ? new Color(0, 128, 0) : Color.RED);
    }

    private void salvarESair() {
        repository.salvar(gerenciador.listarTodas());
        dispose();
        System.exit(0);
    }

    private static String[] nomesDosMeses() {
        String[] nomes = new String[12];
        for (int i = 0; i < 12; i++) {
            String nome = Month.of(i + 1).getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
            nomes[i] = nome.substring(0, 1).toUpperCase(Locale.ROOT) + nome.substring(1);
        }
        return nomes;
    }
}
