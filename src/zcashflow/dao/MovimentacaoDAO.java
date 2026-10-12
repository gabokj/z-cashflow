package zcashflow.dao;

import zcashflow.model.Categoria;
import zcashflow.model.Categoria.TipoFluxo;
import zcashflow.model.Movimentacao;
import zcashflow.model.ResumoMensal;
import zcashflow.model.Usuario;
import zcashflow.util.AppException;
import zcashflow.util.Conexao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Acesso a dados da tabela "movimentacoes".
 *
 * Todas as operações recebem o id do usuário dono do registro: um usuário
 * nunca consegue ler, alterar ou excluir movimentações de outro.
 */
public class MovimentacaoDAO {

    private static final String SELECT_BASE =
            "SELECT m.id_movimentacao, m.id_usuario, m.valor, m.data_operacao, m.descricao, "
                    + "c.id_categoria, c.nome_categoria, c.tipo_fluxo "
                    + "FROM movimentacoes m JOIN categorias c ON c.id_categoria = m.id_categoria ";

    /** RF001 - cadastro de entradas e saídas. */
    public void inserir(Movimentacao mov) throws AppException {
        String sql = "INSERT INTO movimentacoes (id_usuario, id_categoria, valor, data_operacao, descricao) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexao.conectar();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, mov.getUsuario().getIdUsuario());
            stmt.setInt(2, mov.getCategoria().getIdCategoria());
            stmt.setBigDecimal(3, mov.getValor());
            stmt.setDate(4, Date.valueOf(mov.getDataOperacao()));
            stmt.setString(5, mov.getDescricao());
            stmt.executeUpdate();
            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    mov.setIdMovimentacao(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new AppException("Não foi possível salvar a movimentação.", e);
        }
    }

    public void atualizar(Movimentacao mov) throws AppException {
        String sql = "UPDATE movimentacoes SET id_categoria = ?, valor = ?, data_operacao = ?, descricao = ? "
                + "WHERE id_movimentacao = ? AND id_usuario = ?";
        try (Connection con = Conexao.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, mov.getCategoria().getIdCategoria());
            stmt.setBigDecimal(2, mov.getValor());
            stmt.setDate(3, Date.valueOf(mov.getDataOperacao()));
            stmt.setString(4, mov.getDescricao());
            stmt.setInt(5, mov.getIdMovimentacao());
            stmt.setInt(6, mov.getUsuario().getIdUsuario());
            if (stmt.executeUpdate() == 0) {
                throw new AppException("Movimentação não encontrada. Atualize o extrato e tente novamente.");
            }
        } catch (SQLException e) {
            throw new AppException("Não foi possível atualizar a movimentação.", e);
        }
    }

    public void excluir(int idMovimentacao, int idUsuario) throws AppException {
        String sql = "DELETE FROM movimentacoes WHERE id_movimentacao = ? AND id_usuario = ?";
        try (Connection con = Conexao.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idMovimentacao);
            stmt.setInt(2, idUsuario);
            if (stmt.executeUpdate() == 0) {
                throw new AppException("Movimentação não encontrada. Atualize o extrato e tente novamente.");
            }
        } catch (SQLException e) {
            throw new AppException("Não foi possível excluir a movimentação.", e);
        }
    }

    /** RF003 - extrato: consulta com filtros opcionais, mais recentes primeiro. */
    public List<Movimentacao> listar(FiltroMovimentacao filtro) throws AppException {
        StringBuilder sql = new StringBuilder(SELECT_BASE).append("WHERE m.id_usuario = ? ");
        List<Object> parametros = new ArrayList<>();
        parametros.add(filtro.getIdUsuario());

        if (filtro.getTipo() != null) {
            sql.append("AND c.tipo_fluxo = ? ");
            parametros.add(filtro.getTipo().name());
        }
        if (filtro.getIdCategoria() != null) {
            sql.append("AND m.id_categoria = ? ");
            parametros.add(filtro.getIdCategoria());
        }
        if (filtro.getDataInicio() != null) {
            sql.append("AND m.data_operacao >= ? ");
            parametros.add(Date.valueOf(filtro.getDataInicio()));
        }
        if (filtro.getDataFim() != null) {
            sql.append("AND m.data_operacao <= ? ");
            parametros.add(Date.valueOf(filtro.getDataFim()));
        }
        sql.append("ORDER BY m.data_operacao DESC, m.id_movimentacao DESC ");
        if (filtro.getLimite() != null) {
            sql.append("LIMIT ?");
            parametros.add(filtro.getLimite());
        }

        List<Movimentacao> lista = new ArrayList<>();
        try (Connection con = Conexao.conectar();
             PreparedStatement stmt = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new AppException("Não foi possível consultar as movimentações.", e);
        }
        return lista;
    }

    /** RF004 - saldo = total de entradas - total de saídas, calculado no banco. */
    public BigDecimal calcularSaldo(int idUsuario) throws AppException {
        String sql = "SELECT COALESCE(SUM(CASE WHEN c.tipo_fluxo = 'ENTRADA' THEN m.valor ELSE -m.valor END), 0) "
                + "FROM movimentacoes m JOIN categorias c ON c.id_categoria = m.id_categoria "
                + "WHERE m.id_usuario = ?";
        try (Connection con = Conexao.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            throw new AppException("Não foi possível calcular o saldo.", e);
        }
    }

    /**
     * Totais de entradas e saídas por mês nos últimos {@code meses} meses (inclui o mês atual),
     * em ordem cronológica. Meses sem movimentação aparecem zerados.
     */
    public List<ResumoMensal> resumoUltimosMeses(int idUsuario, int meses) throws AppException {
        YearMonth atual = YearMonth.now();
        YearMonth primeiro = atual.minusMonths(meses - 1L);

        Map<YearMonth, BigDecimal[]> totais = new LinkedHashMap<>();
        for (int i = 0; i < meses; i++) {
            totais.put(primeiro.plusMonths(i), new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
        }

        String sql = "SELECT YEAR(m.data_operacao) AS ano, MONTH(m.data_operacao) AS mes, "
                + "COALESCE(SUM(CASE WHEN c.tipo_fluxo = 'ENTRADA' THEN m.valor END), 0) AS entradas, "
                + "COALESCE(SUM(CASE WHEN c.tipo_fluxo = 'SAIDA' THEN m.valor END), 0) AS saidas "
                + "FROM movimentacoes m JOIN categorias c ON c.id_categoria = m.id_categoria "
                + "WHERE m.id_usuario = ? AND m.data_operacao BETWEEN ? AND ? "
                + "GROUP BY YEAR(m.data_operacao), MONTH(m.data_operacao)";
        try (Connection con = Conexao.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setDate(2, Date.valueOf(primeiro.atDay(1)));
            stmt.setDate(3, Date.valueOf(atual.atEndOfMonth()));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    YearMonth mes = YearMonth.of(rs.getInt("ano"), rs.getInt("mes"));
                    totais.put(mes, new BigDecimal[]{rs.getBigDecimal("entradas"), rs.getBigDecimal("saidas")});
                }
            }
        } catch (SQLException e) {
            throw new AppException("Não foi possível calcular o resumo mensal.", e);
        }

        List<ResumoMensal> resumo = new ArrayList<>();
        totais.forEach((mes, v) -> resumo.add(new ResumoMensal(mes, v[0], v[1])));
        return resumo;
    }

    private Movimentacao mapear(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria(rs.getInt("id_categoria"), rs.getString("nome_categoria"),
                TipoFluxo.valueOf(rs.getString("tipo_fluxo")));
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));

        Movimentacao mov = new Movimentacao(usuario, categoria, rs.getBigDecimal("valor"),
                rs.getDate("data_operacao").toLocalDate(), rs.getString("descricao"));
        mov.setIdMovimentacao(rs.getInt("id_movimentacao"));
        return mov;
    }
}
