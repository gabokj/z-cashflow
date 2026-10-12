package zcashflow.service;

import zcashflow.model.Categoria;
import zcashflow.model.Categoria.TipoFluxo;
import zcashflow.model.Movimentacao;
import zcashflow.model.Usuario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Camada de dados em memória usada pela interface gráfica (Etapa 3).
 *
 * Esta classe SIMULA o comportamento do banco de dados MySQL definido em
 * Z-CashFlow.sql (mesmas entidades e regras), permitindo que todas as telas
 * sejam programadas e testadas sem exigir um servidor MySQL em execução.
 *
 * As classes de acesso a dados via JDBC (pacote zcashflow.dao), criadas na
 * Etapa 1, continuam disponíveis no projeto e podem substituir esta camada
 * quando o sistema for conectado a um banco de dados real.
 *
 * Implementada como Singleton para que o estado (saldo, movimentações)
 * seja compartilhado por todas as telas durante a execução do programa.
 */
public class DadosService {

    private static final DadosService INSTANCIA = new DadosService();

    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Categoria> categorias = new ArrayList<>();
    private final List<Movimentacao> movimentacoes = new ArrayList<>();

    private int proximoIdCategoria = 1;
    private int proximoIdMovimentacao = 1;

    private Usuario usuarioLogado;

    private DadosService() {
        semearDadosIniciais();
    }

    public static DadosService getInstance() {
        return INSTANCIA;
    }

    private void semearDadosIniciais() {
        usuarios.add(new Usuario(1, "Gabriel T. Ricca", "gabriel@zmail.com", "olaprofessor", LocalDate.now()));
        usuarios.add(new Usuario(2, "Erik Manager", "erik@zmail.com", "senha12", LocalDate.now()));
        usuarios.add(new Usuario(3, "Cida Colaboradora", "cida@zmail.com", "senha033", LocalDate.now()));
        usuarios.add(new Usuario(4, "Atila Mentor", "atila@zmail.com", "senha3124", LocalDate.now()));

        categorias.add(novaCategoria("Salário", TipoFluxo.ENTRADA));
        categorias.add(novaCategoria("Freelance", TipoFluxo.ENTRADA));
        categorias.add(novaCategoria("Investimentos", TipoFluxo.ENTRADA));
        categorias.add(novaCategoria("Outros (entrada)", TipoFluxo.ENTRADA));
        categorias.add(novaCategoria("Moradia", TipoFluxo.SAIDA));
        categorias.add(novaCategoria("Alimentação", TipoFluxo.SAIDA));
        categorias.add(novaCategoria("Transporte", TipoFluxo.SAIDA));
        categorias.add(novaCategoria("Saúde", TipoFluxo.SAIDA));
        categorias.add(novaCategoria("Lazer", TipoFluxo.SAIDA));
        categorias.add(novaCategoria("Educação", TipoFluxo.SAIDA));

        Usuario gabriel = usuarios.get(0);
        Usuario erik = usuarios.get(1);
        Usuario cida = usuarios.get(2);
        Usuario atila = usuarios.get(3);

        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.withDayOfMonth(1);

        registrarMovimentacao(gabriel, categoriaPorNome("Salário"), 3500.00,
                inicioMes, "Salário Mensal - Estágio AI");
        registrarMovimentacao(gabriel, categoriaPorNome("Alimentação"), 160.00,
                inicioMes.plusDays(4), "Almoço Executivo + Sobremesa");
        registrarMovimentacao(erik, categoriaPorNome("Investimentos"), 500.00,
                inicioMes.plusDays(9), "Rendimento FIIs");
        registrarMovimentacao(cida, categoriaPorNome("Educação"), 299.90,
                inicioMes.plusDays(11), "Curso Especialização Java");
        registrarMovimentacao(atila, categoriaPorNome("Transporte"), 85.00,
                inicioMes.plusDays(13), "Uber para Mentoria");
        registrarMovimentacao(gabriel, categoriaPorNome("Investimentos"), 500.00,
                inicioMes.plusDays(9), "Rendimento FIIs - cota pessoal");
        registrarMovimentacao(gabriel, categoriaPorNome("Lazer"), 120.00,
                hoje.minusDays(2), "Cinema com amigos");
    }

    private Categoria novaCategoria(String nome, TipoFluxo tipo) {
        return new Categoria(proximoIdCategoria++, nome, tipo);
    }

    private Categoria categoriaPorNome(String nome) {
        return categorias.stream()
                .filter(c -> c.getNomeCategoria().equals(nome))
                .findFirst()
                .orElseThrow();
    }

    // ---------------- Autenticação ----------------

    public Usuario autenticar(String email, String senha) {
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email.trim()) && u.getSenha().equals(senha)) {
                this.usuarioLogado = u;
                return u;
            }
        }
        return null;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public void logout() {
        this.usuarioLogado = null;
    }

    // ---------------- Categorias ----------------

    public List<Categoria> listarCategorias() {
        return categorias.stream()
                .sorted(Comparator.comparing(Categoria::getNomeCategoria))
                .collect(Collectors.toList());
    }

    public List<Categoria> listarCategoriasPorTipo(TipoFluxo tipo) {
        return categorias.stream()
                .filter(c -> c.getTipoFluxo() == tipo)
                .sorted(Comparator.comparing(Categoria::getNomeCategoria))
                .collect(Collectors.toList());
    }

    public Categoria cadastrarCategoria(String nome, TipoFluxo tipo) {
        Categoria c = novaCategoria(nome, tipo);
        categorias.add(c);
        return c;
    }

    // ---------------- Movimentações (RF001, RF003, RF004) ----------------

    public Movimentacao registrarMovimentacao(Usuario usuario, Categoria categoria, double valor,
                                               LocalDate data, String descricao) {
        Movimentacao mov = new Movimentacao(usuario, categoria, valor, data, descricao);
        mov.setIdMovimentacao(proximoIdMovimentacao++);
        movimentacoes.add(mov);
        return mov;
    }

    public List<Movimentacao> listarMovimentacoes(int idUsuario) {
        return movimentacoes.stream()
                .filter(m -> m.getUsuario().getIdUsuario() == idUsuario)
                .sorted(Comparator.comparing(Movimentacao::getDataOperacao).reversed())
                .collect(Collectors.toList());
    }

    public double calcularSaldo(int idUsuario) {
        return movimentacoes.stream()
                .filter(m -> m.getUsuario().getIdUsuario() == idUsuario)
                .mapToDouble(m -> m.getCategoria().getTipoFluxo() == TipoFluxo.ENTRADA ? m.getValor() : -m.getValor())
                .sum();
    }

    public double totalPorTipoNoMes(int idUsuario, TipoFluxo tipo, int ano, int mes) {
        return movimentacoes.stream()
                .filter(m -> m.getUsuario().getIdUsuario() == idUsuario)
                .filter(m -> m.getCategoria().getTipoFluxo() == tipo)
                .filter(m -> m.getDataOperacao().getYear() == ano && m.getDataOperacao().getMonthValue() == mes)
                .mapToDouble(Movimentacao::getValor)
                .sum();
    }
}
