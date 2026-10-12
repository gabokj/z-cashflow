package zcashflow.service;

import zcashflow.dao.CategoriaDAO;
import zcashflow.model.Categoria;
import zcashflow.model.Categoria.TipoFluxo;
import zcashflow.util.AppException;

import java.util.List;

/**
 * Regras de negócio das categorias. Qualquer usuário pode consultar;
 * somente administradores podem criar, alterar ou excluir.
 */
public class CategoriaService {

    private final CategoriaDAO dao = new CategoriaDAO();

    public List<Categoria> listar() throws AppException {
        return dao.listar(null);
    }

    public List<Categoria> listarPorTipo(TipoFluxo tipo) throws AppException {
        return dao.listar(tipo);
    }

    public Categoria criar(String nome, TipoFluxo tipo) throws AppException {
        Sessao.getInstance().exigirAdmin();
        Categoria categoria = new Categoria(validarNome(nome), validarTipo(tipo));
        dao.inserir(categoria);
        return categoria;
    }

    public void atualizar(int idCategoria, String nome, TipoFluxo tipo) throws AppException {
        Sessao.getInstance().exigirAdmin();
        Categoria existente = dao.buscarPorId(idCategoria);
        if (existente == null) {
            throw new AppException("Categoria não encontrada. Atualize a lista e tente novamente.");
        }
        TipoFluxo novoTipo = validarTipo(tipo);
        // Mudar o tipo de uma categoria em uso inverteria o efeito de lançamentos antigos no saldo.
        if (existente.getTipoFluxo() != novoTipo && dao.contarMovimentacoes(idCategoria) > 0) {
            throw new AppException("O tipo não pode ser alterado: a categoria já possui movimentações.");
        }
        dao.atualizar(new Categoria(idCategoria, validarNome(nome), novoTipo));
    }

    public void excluir(int idCategoria) throws AppException {
        Sessao.getInstance().exigirAdmin();
        int emUso = dao.contarMovimentacoes(idCategoria);
        if (emUso > 0) {
            throw new AppException("Esta categoria possui " + emUso
                    + " movimentação(ões) e não pode ser excluída.");
        }
        dao.excluir(idCategoria);
    }

    private String validarNome(String nome) throws AppException {
        String limpo = nome == null ? "" : nome.trim();
        if (limpo.length() < 2 || limpo.length() > 50) {
            throw new AppException("O nome da categoria deve ter entre 2 e 50 caracteres.");
        }
        return limpo;
    }

    private TipoFluxo validarTipo(TipoFluxo tipo) throws AppException {
        if (tipo == null) {
            throw new AppException("Selecione o tipo da categoria (entrada ou saída).");
        }
        return tipo;
    }
}
