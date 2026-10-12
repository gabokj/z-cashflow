package zcashflow.dao;

import zcashflow.model.Categoria;
import zcashflow.model.Categoria.TipoFluxo;
import zcashflow.util.AppException;
import zcashflow.util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso a dados da tabela "categorias".
 */
public class CategoriaDAO {

    private static final int ERRO_DUPLICADO = 1062;
    private static final int ERRO_CHAVE_ESTRANGEIRA = 1451;

    public void inserir(Categoria categoria) throws AppException {
        String sql = "INSERT INTO categorias (nome_categoria, tipo_fluxo) VALUES (?, ?)";
        try (Connection con = Conexao.conectar();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, categoria.getNomeCategoria());
            stmt.setString(2, categoria.getTipoFluxo().name());
            stmt.executeUpdate();
            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    categoria.setIdCategoria(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw traduzir(e, "Não foi possível cadastrar a categoria.");
        }
    }

    public void atualizar(Categoria categoria) throws AppException {
        String sql = "UPDATE categorias SET nome_categoria = ?, tipo_fluxo = ? WHERE id_categoria = ?";
        try (Connection con = Conexao.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, categoria.getNomeCategoria());
            stmt.setString(2, categoria.getTipoFluxo().name());
            stmt.setInt(3, categoria.getIdCategoria());
            if (stmt.executeUpdate() == 0) {
                throw new AppException("Categoria não encontrada. Atualize a lista e tente novamente.");
            }
        } catch (SQLException e) {
            throw traduzir(e, "Não foi possível atualizar a categoria.");
        }
    }

    public void excluir(int idCategoria) throws AppException {
        String sql = "DELETE FROM categorias WHERE id_categoria = ?";
        try (Connection con = Conexao.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idCategoria);
            if (stmt.executeUpdate() == 0) {
                throw new AppException("Categoria não encontrada. Atualize a lista e tente novamente.");
            }
        } catch (SQLException e) {
            throw traduzir(e, "Não foi possível excluir a categoria.");
        }
    }

    public Categoria buscarPorId(int idCategoria) throws AppException {
        String sql = "SELECT id_categoria, nome_categoria, tipo_fluxo FROM categorias WHERE id_categoria = ?";
        try (Connection con = Conexao.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idCategoria);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new AppException("Não foi possível consultar a categoria.", e);
        }
    }

    /** Lista todas as categorias (ordem alfabética); se tipo != null, apenas as do tipo informado. */
    public List<Categoria> listar(TipoFluxo tipo) throws AppException {
        String sql = "SELECT id_categoria, nome_categoria, tipo_fluxo FROM categorias "
                + (tipo != null ? "WHERE tipo_fluxo = ? " : "")
                + "ORDER BY nome_categoria";
        List<Categoria> lista = new ArrayList<>();
        try (Connection con = Conexao.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            if (tipo != null) {
                stmt.setString(1, tipo.name());
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new AppException("Não foi possível listar as categorias.", e);
        }
        return lista;
    }

    /** Quantidade de movimentações que usam a categoria (regra: não excluir/mudar tipo se em uso). */
    public int contarMovimentacoes(int idCategoria) throws AppException {
        String sql = "SELECT COUNT(*) FROM movimentacoes WHERE id_categoria = ?";
        try (Connection con = Conexao.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idCategoria);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new AppException("Não foi possível consultar o uso da categoria.", e);
        }
    }

    private Categoria mapear(ResultSet rs) throws SQLException {
        return new Categoria(rs.getInt("id_categoria"), rs.getString("nome_categoria"),
                TipoFluxo.valueOf(rs.getString("tipo_fluxo")));
    }

    private AppException traduzir(SQLException e, String padrao) {
        if (e.getErrorCode() == ERRO_DUPLICADO) {
            return new AppException("Já existe uma categoria com esse nome e tipo.", e);
        }
        if (e.getErrorCode() == ERRO_CHAVE_ESTRANGEIRA) {
            return new AppException("Esta categoria possui movimentações e não pode ser excluída.", e);
        }
        return new AppException(padrao, e);
    }
}
