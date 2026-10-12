package zcashflow.dao;

import zcashflow.model.Categoria;
import zcashflow.model.Movimentacao;
import zcashflow.model.Usuario;
import zcashflow.util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de acesso a dados (DAO) para a tabela "movimentacoes".
 *
 * Implementa, entre outros, os requisitos:
 * RF001 - Cadastro de Entradas e Saídas
 * RF003 - Extrato Automatizado
 * RF004 - Cálculo de Saldo em Tempo Real
 */
public class MovimentacaoDAO {

    /**
     * RF001 - Cadastro de Entradas e Saídas.
     * Insere uma nova movimentação associada a um usuário e a uma categoria.
     */
    public void cadastrar(Movimentacao mov) throws SQLException {
        String sql = "INSERT INTO movimentacoes (id_usuario, id_categoria, valor, data_operacao, descricao) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexao.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, mov.getUsuario().getIdUsuario());
            stmt.setInt(2, mov.getCategoria().getIdCategoria());
            stmt.setDouble(3, mov.getValor());
            stmt.setDate(4, java.sql.Date.valueOf(mov.getDataOperacao()));
            stmt.setString(5, mov.getDescricao());
            stmt.executeUpdate();
        }
    }

    /**
     * RF003 - Extrato Automatizado.
     * Lista todas as movimentações de um usuário, já com categoria carregada.
     */
    public List<Movimentacao> listarPorUsuario(int idUsuario) throws SQLException {
        List<Movimentacao> lista = new ArrayList<>();
        String sql = "SELECT m.id_movimentacao, m.valor, m.data_operacao, m.descricao, "
                + "c.id_categoria, c.nome_categoria, c.tipo_fluxo "
                + "FROM movimentacoes m "
                + "JOIN categorias c ON c.id_categoria = m.id_categoria "
                + "WHERE m.id_usuario = ? "
                + "ORDER BY m.data_operacao DESC";
        try (Connection con = Conexao.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Categoria categoria = new Categoria(
                            rs.getInt("id_categoria"),
                            rs.getString("nome_categoria"),
                            Categoria.TipoFluxo.valueOf(rs.getString("tipo_fluxo"))
                    );
                    Usuario usuario = new Usuario();
                    usuario.setIdUsuario(idUsuario);

                    Movimentacao mov = new Movimentacao(
                            usuario,
                            categoria,
                            rs.getDouble("valor"),
                            rs.getDate("data_operacao").toLocalDate(),
                            rs.getString("descricao")
                    );
                    mov.setIdMovimentacao(rs.getInt("id_movimentacao"));
                    lista.add(mov);
                }
            }
        }
        return lista;
    }

    /**
     * RF004 - Cálculo de Saldo em Tempo Real.
     * Recalcula o saldo (entradas - saídas) direto no banco de dados,
     * toda vez que é chamado após uma nova movimentação confirmada.
     */
    public double calcularSaldo(int idUsuario) throws SQLException {
        String sql = "SELECT "
                + "COALESCE(SUM(CASE WHEN c.tipo_fluxo = 'ENTRADA' THEN m.valor ELSE 0 END), 0) - "
                + "COALESCE(SUM(CASE WHEN c.tipo_fluxo = 'SAIDA' THEN m.valor ELSE 0 END), 0) AS saldo "
                + "FROM movimentacoes m "
                + "JOIN categorias c ON c.id_categoria = m.id_categoria "
                + "WHERE m.id_usuario = ?";
        try (Connection con = Conexao.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("saldo");
                }
            }
        }
        return 0.0;
    }
}
