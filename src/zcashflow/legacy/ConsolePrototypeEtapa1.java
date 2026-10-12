package zcashflow.legacy;

import zcashflow.dao.CategoriaDAO;
import zcashflow.dao.MovimentacaoDAO;
import zcashflow.model.Categoria;
import zcashflow.model.Movimentacao;
import zcashflow.model.Usuario;
import zcashflow.util.Conexao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Protótipo de console da Etapa 1 do Projeto Integrador (preservado para
 * referência). Implementa via console:
 *   RF001 - Cadastro de Entradas e Saídas
 *   RF003 - Extrato Automatizado (formato DD/MM/AAAA)
 *   RF004 - Cálculo de Saldo em Tempo Real
 *
 * Tenta conectar ao MySQL "z_cashflow"; se indisponível, cai em MODO
 * DEMONSTRAÇÃO (lista em memória). A partir da Etapa 3 a classe principal
 * do sistema é zcashflow.Main (interface gráfica).
 */
public class ConsolePrototypeEtapa1 {

    private static final Scanner SC = new Scanner(System.in);
    private static final MovimentacaoDAO MOVIMENTACAO_DAO = new MovimentacaoDAO();
    private static final CategoriaDAO CATEGORIA_DAO = new CategoriaDAO();

    private static final List<Movimentacao> MOVIMENTACOES_MEMORIA = new ArrayList<>();
    private static Usuario usuarioLogado;
    private static boolean modoDemonstracao = false;

    public static void main(String[] args) {
        System.out.println("=======================================");
        System.out.println("        Z-CASHFLOW - PROTOTIPO         ");
        System.out.println("=======================================");

        verificarConexaoBanco();
        usuarioLogado = new Usuario(1, "Gabriel T. Ricca", "gabriel@zmail.com", "***", LocalDate.now());

        int opcao;
        do {
            exibirMenu();
            opcao = lerInteiro("Escolha uma opcao: ");
            switch (opcao) {
                case 1 -> cadastrarMovimentacao();
                case 2 -> exibirExtrato();
                case 3 -> exibirSaldo();
                case 0 -> System.out.println("Encerrando Z-CashFlow. Ate logo!");
                default -> System.out.println("Opcao invalida. Tente novamente.");
            }
        } while (opcao != 0);

        SC.close();
    }

    private static void verificarConexaoBanco() {
        try {
            Conexao.conectar().close();
            modoDemonstracao = false;
            System.out.println("[OK] Conectado ao banco de dados MySQL 'z_cashflow'.");
        } catch (SQLException e) {
            modoDemonstracao = true;
            System.out.println("[AVISO] Nao foi possivel conectar ao MySQL (" + e.getMessage() + ").");
            System.out.println("[AVISO] Executando em MODO DEMONSTRACAO (dados em memoria).");
        }
    }

    private static void exibirMenu() {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("Usuario: " + usuarioLogado.getNome());
        System.out.println("1 - Cadastrar movimentacao (entrada/saida)   [RF001]");
        System.out.println("2 - Ver extrato de movimentacoes             [RF003]");
        System.out.println("3 - Ver saldo em tempo real                  [RF004]");
        System.out.println("0 - Sair");
    }

    private static void cadastrarMovimentacao() {
        try {
            List<Categoria> categorias = listarCategoriasDisponiveis();
            if (categorias.isEmpty()) {
                System.out.println("Nenhuma categoria cadastrada.");
                return;
            }

            System.out.println("\nCategorias disponiveis:");
            for (int i = 0; i < categorias.size(); i++) {
                System.out.printf("%d - %s%n", i + 1, categorias.get(i));
            }

            int escolha = lerInteiro("Escolha a categoria: ") - 1;
            if (escolha < 0 || escolha >= categorias.size()) {
                System.out.println("Categoria invalida.");
                return;
            }
            Categoria categoriaEscolhida = categorias.get(escolha);

            double valor = lerDouble("Valor (R$): ");
            System.out.print("Descricao: ");
            String descricao = SC.nextLine();

            Movimentacao mov = new Movimentacao(usuarioLogado, categoriaEscolhida, valor,
                    LocalDate.now(), descricao);

            if (modoDemonstracao) {
                mov.setIdMovimentacao(MOVIMENTACOES_MEMORIA.size() + 1);
                MOVIMENTACOES_MEMORIA.add(mov);
            } else {
                MOVIMENTACAO_DAO.cadastrar(mov);
            }

            System.out.println("Movimentacao cadastrada com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar movimentacao: " + e.getMessage());
        }
    }

    private static void exibirExtrato() {
        try {
            List<Movimentacao> movimentacoes = modoDemonstracao
                    ? MOVIMENTACOES_MEMORIA
                    : MOVIMENTACAO_DAO.listarPorUsuario(usuarioLogado.getIdUsuario());

            System.out.println("\n--- EXTRATO ---");
            if (movimentacoes.isEmpty()) {
                System.out.println("Nenhuma movimentacao encontrada.");
                return;
            }
            for (Movimentacao mov : movimentacoes) {
                System.out.println(mov);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar extrato: " + e.getMessage());
        }
    }

    private static void exibirSaldo() {
        try {
            double saldo;
            if (modoDemonstracao) {
                saldo = MOVIMENTACOES_MEMORIA.stream()
                        .mapToDouble(m -> m.getCategoria().getTipoFluxo() == Categoria.TipoFluxo.ENTRADA
                                ? m.getValor() : -m.getValor())
                        .sum();
            } else {
                saldo = MOVIMENTACAO_DAO.calcularSaldo(usuarioLogado.getIdUsuario());
            }
            System.out.printf(Locale.of("pt", "BR"), "%nSaldo atual: R$ %.2f%n", saldo);
        } catch (SQLException e) {
            System.out.println("Erro ao calcular saldo: " + e.getMessage());
        }
    }

    private static List<Categoria> listarCategoriasDisponiveis() throws SQLException {
        if (modoDemonstracao) {
            List<Categoria> categorias = new ArrayList<>();
            categorias.add(new Categoria(1, "Salario", Categoria.TipoFluxo.ENTRADA));
            categorias.add(new Categoria(2, "Freelance", Categoria.TipoFluxo.ENTRADA));
            categorias.add(new Categoria(5, "Moradia", Categoria.TipoFluxo.SAIDA));
            categorias.add(new Categoria(6, "Alimentacao", Categoria.TipoFluxo.SAIDA));
            categorias.add(new Categoria(7, "Transporte", Categoria.TipoFluxo.SAIDA));
            categorias.add(new Categoria(9, "Lazer", Categoria.TipoFluxo.SAIDA));
            return categorias;
        }
        return CATEGORIA_DAO.listarTodas();
    }

    private static int lerInteiro(String mensagem) {
        System.out.print(mensagem);
        while (!SC.hasNextInt()) {
            System.out.print("Valor invalido. " + mensagem);
            SC.next();
        }
        int valor = SC.nextInt();
        SC.nextLine();
        return valor;
    }

    private static double lerDouble(String mensagem) {
        System.out.print(mensagem);
        while (!SC.hasNextDouble()) {
            System.out.print("Valor invalido. " + mensagem);
            SC.next();
        }
        double valor = SC.nextDouble();
        SC.nextLine();
        return valor;
    }
}
