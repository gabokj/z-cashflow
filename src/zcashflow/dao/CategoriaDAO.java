package zcashflow.dao;

import zcashflow.model.Categoria;
import zcashflow.util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de acesso a dados (DAO) para a tabela "categorias".
 */
public class CategoriaDAO {

    public void cadastrar(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO categorias (nome_categoria, tipo_fluxo) VALUES (?, ?)";
        try (Connection con = Conexao.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, categoria.getNomeCategoria());
            stmt.setString(2, categoria.getTipoFluxo().name());
            stmt.executeUpdate();
        }
    }

    public List<Categoria> listarTodas() throws SQLException {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT id_categoria, nome_categoria, tipo_fluxo FROM categorias ORDER BY nome_categoria";
        try (Connection con = Conexao.conectar();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categorias.add(new Categoria(
                        rs.getInt("id_categoria"),
                        rs.getString("nome_categoria"),
                        Categoria.TipoFluxo.valueOf(rs.getString("tipo_fluxo"))
                ));
            }
        }
        return categorias;
    }
}
