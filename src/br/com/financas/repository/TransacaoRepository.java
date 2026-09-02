package br.com.financas.repository;

import br.com.financas.model.Categoria;
import br.com.financas.model.Despesa;
import br.com.financas.model.Despesa.FormaPagamento;
import br.com.financas.model.Receita;
import br.com.financas.model.Transacao;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Camada de PERSISTÊNCIA (Repository Pattern).
 *
 * BOA PRÁTICA: isola COMPLETAMENTE o "como" os dados são salvos (formato CSV, caminho
 * do arquivo, encoding) do restante da aplicação. Se amanhã quisermos trocar CSV por
 * banco de dados, só esta classe precisa mudar — Main e GerenciadorFinanceiro
 * continuam iguais, pois dependem apenas de List<Transacao>.
 *
 * Usa java.nio.file (Files/Path), a API moderna de I/O do Java, preferível ao antigo
 * java.io.File por oferecer melhor tratamento de exceções e métodos utilitários.
 */
public class TransacaoRepository {

    private static final String DELIMITADOR = ";";
    private static final String CABECALHO = "id;tipo;descricao;valor;data;categoria;detalhe";

    private final Path arquivo;

    public TransacaoRepository() {
        this("financas.csv");
    }

    public TransacaoRepository(String caminhoArquivo) {
        this.arquivo = Path.of(caminhoArquivo);
    }

    /**
     * Salva a lista de transações no arquivo CSV, sobrescrevendo o conteúdo anterior.
     * Cada subclasse é serializada em uma linha, com uma coluna "detalhe" que guarda
     * o atributo específico (fonte da Receita, ou forma de pagamento da Despesa).
     */
    public void salvar(List<Transacao> transacoes) {
        List<String> linhas = new ArrayList<>();
        linhas.add(CABECALHO);

        for (Transacao t : transacoes) {
            String detalhe;
            if (t instanceof Receita receita) {
                detalhe = receita.getFonte();
            } else if (t instanceof Despesa despesa) {
                detalhe = despesa.getFormaPagamento().name();
            } else {
                detalhe = "";
            }

            String linha = String.join(DELIMITADOR,
                    String.valueOf(t.getId()),
                    t.getTipo(),
                    escapar(t.getDescricao()),
                    String.valueOf(t.getValor()),
                    t.getData().toString(), // formato ISO-8601 (yyyy-MM-dd), ideal para persistência
                    t.getCategoria().name(),
                    escapar(detalhe));
            linhas.add(linha);
        }

        try {
            Files.write(arquivo, linhas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            // Convertida para exceção não verificada: uma falha ao salvar é um erro grave
            // que a camada de apresentação deve tratar, mas o Repository não deveria forçar
            // todo o resto do sistema a declarar "throws IOException".
            throw new UncheckedIOException("Erro ao salvar transações no arquivo: " + arquivo, e);
        }
    }

    /**
     * Carrega as transações do arquivo CSV. Se o arquivo ainda não existir
     * (primeira execução do programa), retorna uma lista vazia em vez de lançar erro.
     */
    public List<Transacao> carregar() {
        List<Transacao> transacoes = new ArrayList<>();

        if (!Files.exists(arquivo)) {
            return transacoes; // Primeira execução: nada para carregar ainda.
        }

        try {
            List<String> linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);
            for (int i = 1; i < linhas.size(); i++) { // i=1 para pular o cabeçalho
                String linha = linhas.get(i);
                if (linha.isBlank()) continue;

                try {
                    transacoes.add(converterLinha(linha));
                } catch (Exception e) {
                    // Uma linha corrompida não deve impedir o carregamento das demais;
                    // registramos o problema e seguimos em frente (resiliência a dados ruins).
                    System.out.println("[Aviso] Linha ignorada por estar corrompida (linha " + (i + 1) + "): " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao ler o arquivo de transações: " + arquivo, e);
        }

        return transacoes;
    }

    /**
     * Converte uma linha do CSV de volta para o objeto de domínio correto
     * (Receita ou Despesa), demonstrando o uso de POLIMORFISMO também na desserialização:
     * o tipo de objeto criado depende de um dado (a coluna "tipo"), mas o restante do
     * sistema volta a tratar tudo como Transacao.
     */
    private Transacao converterLinha(String linha) {
        String[] partes = linha.split(DELIMITADOR, -1);
        if (partes.length < 7) {
            throw new IllegalArgumentException("Número de colunas inválido.");
        }

        Long id = Long.parseLong(partes[0].trim());
        String tipo = partes[1].trim();
        String descricao = desescapar(partes[2]);
        double valor = Double.parseDouble(partes[3].trim().replace(",", "."));
        LocalDate data = LocalDate.parse(partes[4].trim());
        Categoria categoria = Categoria.valueOf(partes[5].trim());
        String detalhe = desescapar(partes[6]);

        return switch (tipo.toUpperCase(Locale.ROOT)) {
            case "RECEITA" -> new Receita(id, descricao, valor, data, categoria, detalhe);
            case "DESPESA" -> new Despesa(id, descricao, valor, data, categoria, FormaPagamento.valueOf(detalhe));
            default -> throw new IllegalArgumentException("Tipo de transação desconhecido: " + tipo);
        };
    }

    // Escapa o delimitador dentro de textos livres (descrição/fonte), trocando ";" por um
    // caractere improvável de aparecer digitado pelo usuário, evitando quebrar o parsing do CSV.
    private String escapar(String texto) {
        return texto == null ? "" : texto.replace(DELIMITADOR, ",");
    }

    private String desescapar(String texto) {
        return texto == null ? "" : texto.trim();
    }
}
