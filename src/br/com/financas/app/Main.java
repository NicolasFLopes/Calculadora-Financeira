package br.com.financas.app;

import br.com.financas.model.Categoria;
import br.com.financas.model.Despesa;
import br.com.financas.model.Despesa.FormaPagamento;
import br.com.financas.model.Receita;
import br.com.financas.model.Transacao;
import br.com.financas.repository.TransacaoRepository;
import br.com.financas.service.GerenciadorFinanceiro;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Camada de APRESENTAÇÃO: interface via console (Scanner).
 *
 * BOA PRÁTICA: Main só orquestra a interação com o usuário (ler entrada, mostrar
 * saída) e delega TODA a regra de negócio ao GerenciadorFinanceiro e TODA a
 * persistência ao TransacaoRepository. Isso mantém a classe de UI simples e focada
 * em uma única responsabilidade: conversa com o usuário.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final TransacaoRepository repository = new TransacaoRepository();
    private static GerenciadorFinanceiro gerenciador;

    public static void main(String[] args) {
        // Carrega os dados persistidos assim que o programa inicia.
        List<Transacao> transacoesCarregadas = repository.carregar();
        gerenciador = new GerenciadorFinanceiro(transacoesCarregadas);

        System.out.println("=======================================================");
        System.out.println(" Bem-vindo à Calculadora de Finanças Pessoais");
        System.out.println(" " + transacoesCarregadas.size() + " transação(ões) carregada(s) do arquivo.");
        System.out.println("=======================================================");

        boolean continuar = true;
        while (continuar) {
            exibirMenu();
            int opcao = lerOpcaoMenu();

            switch (opcao) {
                case 1:
                    cadastrarReceita();
                    break;
                case 2:
                    cadastrarDespesa();
                    break;
                case 3:
                    listarTodas();
                    break;
                case 4:
                    filtrarPorMesAno();
                    break;
                case 5:
                    exibirResumoFinanceiro();
                    break;
                case 6:
                    removerTransacao();
                    break;
                case 0:
                    continuar = encerrarPrograma();
                    break;
                default:
                    System.out.println(">> Opção inválida. Escolha um número entre 0 e 6.");
                    break;
            }
        }

        scanner.close();
    }

    private static void exibirMenu() {
        System.out.println("\n--------------- MENU ---------------");
        System.out.println("1. Cadastrar Receita");
        System.out.println("2. Cadastrar Despesa");
        System.out.println("3. Listar Todas as Transações");
        System.out.println("4. Filtrar por Mês/Ano");
        System.out.println("5. Exibir Resumo Financeiro (Saldo Total)");
        System.out.println("6. Remover Transação por ID");
        System.out.println("0. Sair (e salvar dados)");
        System.out.println("-------------------------------------");
        System.out.print("Escolha uma opção: ");
    }

    // ---------------------------------------------------------------
    // Leitura robusta de entrada — tratamento de exceções centralizado
    // ---------------------------------------------------------------

    private static int lerOpcaoMenu() {
        String entrada = scanner.nextLine().trim();
        try {
            return Integer.parseInt(entrada);
        } catch (NumberFormatException e) {
            return -1; // Sinaliza opção inválida sem derrubar o programa.
        }
    }

    /**
     * Lê um double de forma robusta, repetindo a pergunta até o usuário digitar
     * um número válido. Aceita tanto ponto quanto vírgula como separador decimal,
     * já que o usuário brasileiro tende a digitar vírgula por hábito.
     */
    private static double lerValorMonetario(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String entrada = scanner.nextLine().trim().replace(",", ".");
            try {
                double valor = Double.parseDouble(entrada);
                if (valor <= 0) {
                    System.out.println(">> O valor deve ser maior que zero. Tente novamente.");
                    continue;
                }
                return valor;
            } catch (NumberFormatException e) {
                System.out.println(">> Valor inválido. Digite apenas números (ex.: 150.90). Tente novamente.");
            }
        }
    }

    /**
     * Lê uma data no formato dd/MM/yyyy, repetindo a pergunta em caso de formato inválido.
     * Também aceita ENTER em branco para assumir a data de hoje, agilizando o cadastro.
     */
    private static LocalDate lerData(String mensagem) {
        while (true) {
            System.out.print(mensagem + " (dd/MM/aaaa, ou ENTER para hoje): ");
            String entrada = scanner.nextLine().trim();
            if (entrada.isEmpty()) {
                return LocalDate.now();
            }
            try {
                LocalDate data = LocalDate.parse(entrada, FORMATO_DATA);
                if (data.isAfter(LocalDate.now())) {
                    System.out.println(">> A data não pode ser futura. Tente novamente.");
                    continue;
                }
                return data;
            } catch (DateTimeParseException e) {
                System.out.println(">> Formato de data inválido. Use dd/MM/aaaa (ex.: 25/12/2025). Tente novamente.");
            }
        }
    }

    private static String lerTexto(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String entrada = scanner.nextLine().trim();
            if (!entrada.isEmpty()) {
                return entrada;
            }
            System.out.println(">> Este campo não pode ficar em branco. Tente novamente.");
        }
    }

    private static Categoria lerCategoria() {
        Categoria[] categorias = Categoria.values();
        while (true) {
            System.out.println("Categorias disponíveis:");
            for (int i = 0; i < categorias.length; i++) {
                System.out.printf("  %d - %s%n", i + 1, categorias[i].getDescricao());
            }
            System.out.print("Escolha a categoria (número): ");
            String entrada = scanner.nextLine().trim();
            try {
                int indice = Integer.parseInt(entrada) - 1;
                if (indice >= 0 && indice < categorias.length) {
                    return categorias[indice];
                }
                System.out.println(">> Opção fora do intervalo. Tente novamente.");
            } catch (NumberFormatException e) {
                System.out.println(">> Digite apenas o número correspondente à categoria.");
            }
        }
    }

    private static FormaPagamento lerFormaPagamento() {
        FormaPagamento[] formas = FormaPagamento.values();
        while (true) {
            System.out.println("Formas de pagamento disponíveis:");
            for (int i = 0; i < formas.length; i++) {
                System.out.printf("  %d - %s%n", i + 1, formas[i].getDescricao());
            }
            System.out.print("Escolha a forma de pagamento (número): ");
            String entrada = scanner.nextLine().trim();
            try {
                int indice = Integer.parseInt(entrada) - 1;
                if (indice >= 0 && indice < formas.length) {
                    return formas[indice];
                }
                System.out.println(">> Opção fora do intervalo. Tente novamente.");
            } catch (NumberFormatException e) {
                System.out.println(">> Digite apenas o número correspondente à forma de pagamento.");
            }
        }
    }

    // ---------------------------------------------------------------
    // Ações do menu
    // ---------------------------------------------------------------

    private static void cadastrarReceita() {
        System.out.println("\n--- Cadastro de Receita ---");
        try {
            String descricao = lerTexto("Descrição: ");
            double valor = lerValorMonetario("Valor (R$): ");
            LocalDate data = lerData("Data");
            Categoria categoria = lerCategoria();
            String fonte = lerTexto("Fonte (ex.: Empresa, Freelance): ");

            Receita receita = new Receita(descricao, valor, data, categoria, fonte);
            gerenciador.adicionar(receita);
            System.out.println(">> Receita cadastrada com sucesso! " + receita);
        } catch (IllegalArgumentException e) {
            // Captura qualquer validação lançada pelos setters do model (defesa em profundidade).
            System.out.println(">> Não foi possível cadastrar a receita: " + e.getMessage());
        }
    }

    private static void cadastrarDespesa() {
        System.out.println("\n--- Cadastro de Despesa ---");
        try {
            String descricao = lerTexto("Descrição: ");
            double valor = lerValorMonetario("Valor (R$): ");
            LocalDate data = lerData("Data");
            Categoria categoria = lerCategoria();
            FormaPagamento formaPagamento = lerFormaPagamento();

            Despesa despesa = new Despesa(descricao, valor, data, categoria, formaPagamento);
            gerenciador.adicionar(despesa);
            System.out.println(">> Despesa cadastrada com sucesso! " + despesa);
        } catch (IllegalArgumentException e) {
            System.out.println(">> Não foi possível cadastrar a despesa: " + e.getMessage());
        }
    }

    private static void listarTodas() {
        System.out.println("\n--- Todas as Transações ---");
        List<Transacao> transacoes = gerenciador.listarTodas();
        if (transacoes.isEmpty()) {
            System.out.println("Nenhuma transação cadastrada ainda.");
            return;
        }
        transacoes.forEach(System.out::println);
        System.out.println("Total de transações: " + transacoes.size());
    }

    private static void filtrarPorMesAno() {
        System.out.println("\n--- Filtrar por Mês/Ano ---");
        int mesNumero = lerInteiroEntre("Mês (1-12): ", 1, 12);
        int ano = lerInteiroEntre("Ano (ex.: 2025): ", 1900, 2100);

        Month mes = Month.of(mesNumero);
        List<Transacao> filtradas = gerenciador.filtrarPorMesAno(mes, Year.of(ano));

        if (filtradas.isEmpty()) {
            System.out.println(">> Nenhuma transação encontrada para " + mes + "/" + ano + ".");
            return;
        }
        filtradas.forEach(System.out::println);
        System.out.println("Total de transações no período: " + filtradas.size());
    }

    private static int lerInteiroEntre(String mensagem, int min, int max) {
        while (true) {
            System.out.print(mensagem);
            String entrada = scanner.nextLine().trim();
            try {
                int valor = Integer.parseInt(entrada);
                if (valor < min || valor > max) {
                    System.out.printf(">> Digite um valor entre %d e %d.%n", min, max);
                    continue;
                }
                return valor;
            } catch (NumberFormatException e) {
                System.out.println(">> Entrada inválida. Digite apenas números.");
            }
        }
    }

    private static void exibirResumoFinanceiro() {
        System.out.println("\n--- Resumo Financeiro ---");
        double totalReceitas = gerenciador.calcularTotalReceitas();
        double totalDespesas = gerenciador.calcularTotalDespesas();
        double saldo = gerenciador.calcularSaldo();

        System.out.printf("Total de Receitas: R$ %,.2f%n", totalReceitas);
        System.out.printf("Total de Despesas: R$ %,.2f%n", totalDespesas);
        System.out.println("-------------------------------------");
        if (saldo >= 0) {
            System.out.printf("Saldo Final: R$ %,.2f (positivo)%n", saldo);
        } else {
            System.out.printf("Saldo Final: -R$ %,.2f (negativo)%n", Math.abs(saldo));
        }
    }

    private static void removerTransacao() {
        System.out.println("\n--- Remover Transação ---");
        System.out.print("Digite o ID da transação a ser removida: ");
        String entrada = scanner.nextLine().trim();
        try {
            Long id = Long.parseLong(entrada);
            Transacao removida = gerenciador.removerPorId(id);
            System.out.println(">> Transação removida com sucesso: " + removida);
        } catch (NumberFormatException e) {
            System.out.println(">> ID inválido. Digite apenas números.");
        } catch (NoSuchElementException e) {
            System.out.println(">> " + e.getMessage());
        }
    }

    private static boolean encerrarPrograma() {
        repository.salvar(gerenciador.listarTodas());
        System.out.println("\n>> Dados salvos com sucesso em 'financas.csv'.");
        System.out.println(">> Até logo!");
        return false; // encerra o loop principal
    }
}
