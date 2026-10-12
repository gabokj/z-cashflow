package zcashflow.service;

import zcashflow.dao.FiltroMovimentacao;
import zcashflow.dao.MovimentacaoDAO;
import zcashflow.model.Categoria;
import zcashflow.model.Movimentacao;
import zcashflow.model.ResumoMensal;
import zcashflow.model.Usuario;
import zcashflow.util.AppException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Regras de negócio das movimentações financeiras do usuário logado.
 */
public class MovimentacaoService {

    private static final BigDecimal VALOR_MAXIMO = new BigDecimal("99999999.99"); // DECIMAL(10,2)
    private static final int TAMANHO_MAXIMO_DESCRICAO = 255;

    private final MovimentacaoDAO dao = new MovimentacaoDAO();

    /** RF001 - cadastra uma entrada ou saída (o tipo vem da categoria escolhida). */
    public Movimentacao registrar(Categoria categoria, BigDecimal valor, LocalDate data, String descricao)
            throws AppException {
        Usuario usuario = Sessao.getInstance().exigirUsuario();
        Movimentacao mov = montar(usuario, categoria, valor, data, descricao);
        dao.inserir(mov);
        return mov;
    }

    public void atualizar(int idMovimentacao, Categoria categoria, BigDecimal valor, LocalDate data,
                          String descricao) throws AppException {
        Usuario usuario = Sessao.getInstance().exigirUsuario();
        Movimentacao mov = montar(usuario, categoria, valor, data, descricao);
        mov.setIdMovimentacao(idMovimentacao);
        dao.atualizar(mov);
    }

    public void excluir(int idMovimentacao) throws AppException {
        Usuario usuario = Sessao.getInstance().exigirUsuario();
        dao.excluir(idMovimentacao, usuario.getIdUsuario());
    }

    /** RF003 - extrato com filtros; o filtro recebido já deve estar vinculado ao usuário logado. */
    public List<Movimentacao> listar(FiltroMovimentacao filtro) throws AppException {
        if (filtro.getDataInicio() != null && filtro.getDataFim() != null
                && filtro.getDataInicio().isAfter(filtro.getDataFim())) {
            throw new AppException("A data inicial não pode ser posterior à data final.");
        }
        return dao.listar(filtro);
    }

    /** Cria um filtro já vinculado ao usuário logado. */
    public FiltroMovimentacao novoFiltro() throws AppException {
        return new FiltroMovimentacao(Sessao.getInstance().exigirUsuario().getIdUsuario());
    }

    public List<Movimentacao> recentes(int quantidade) throws AppException {
        return dao.listar(novoFiltro().limite(quantidade));
    }

    /** RF004 - saldo atual do usuário logado. */
    public BigDecimal saldo() throws AppException {
        return dao.calcularSaldo(Sessao.getInstance().exigirUsuario().getIdUsuario());
    }

    public List<ResumoMensal> resumoMensal(int meses) throws AppException {
        return dao.resumoUltimosMeses(Sessao.getInstance().exigirUsuario().getIdUsuario(), meses);
    }

    private Movimentacao montar(Usuario usuario, Categoria categoria, BigDecimal valor, LocalDate data,
                                String descricao) throws AppException {
        if (categoria == null) {
            throw new AppException("Selecione uma categoria.");
        }
        if (valor == null || valor.signum() <= 0) {
            throw new AppException("O valor deve ser maior que zero.");
        }
        if (valor.compareTo(VALOR_MAXIMO) > 0) {
            throw new AppException("O valor máximo permitido é R$ 99.999.999,99.");
        }
        if (data == null) {
            throw new AppException("Informe a data da movimentação.");
        }
        String texto = descricao == null ? "" : descricao.trim();
        if (texto.isEmpty()) {
            throw new AppException("Informe uma descrição.");
        }
        if (texto.length() > TAMANHO_MAXIMO_DESCRICAO) {
            throw new AppException("A descrição deve ter no máximo " + TAMANHO_MAXIMO_DESCRICAO + " caracteres.");
        }
        return new Movimentacao(usuario, categoria, valor, data, texto);
    }
}
