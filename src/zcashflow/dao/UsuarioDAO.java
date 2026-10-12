package zcashflow.dao;

import zcashflow.model.Usuario;
import zcashflow.util.AppException;
import zcashflow.util.Conexao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Acesso a dados da tabela "usuarios".
 */
public class UsuarioDAO {

    private static final int ERRO_DUPLICADO = 1062;

    public void inserir(Usuario usuario) throws AppException {
        String sql = "INSERT INTO usuarios (nome, email, senha_hash, perfil, data_cadastro) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexao.conectar();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenhaHash());
            stmt.setString(4, usuario.getPerfil().name());
            stmt.setDate(5, Date.valueOf(usuario.getDataCadastro()));
            stmt.executeUpdate();
            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    usuario.setIdUsuario(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == ERRO_DUPLICADO) {
                throw new AppException("Já existe um usuário cadastrado com este e-mail.", e);
            }
            throw new AppException("Não foi possível cadastrar o usuário.", e);
        }
    }

    /** Retorna o usuário com o e-mail informado, ou null se não existir. */
    public Usuario buscarPorEmail(String email) throws AppException {
        String sql = "SELECT id_usuario, nome, email, senha_hash, perfil, data_cadastro "
                + "FROM usuarios WHERE email = ?";
        try (Connection con = Conexao.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setNome(rs.getString("nome"));
                    u.setEmail(rs.getString("email"));
                    u.setSenhaHash(rs.getString("senha_hash"));
                    u.setPerfil(Usuario.Perfil.valueOf(rs.getString("perfil")));
                    u.setDataCadastro(rs.getDate("data_cadastro").toLocalDate());
                    return u;
                }
            }
        } catch (SQLException e) {
            throw new AppException("Não foi possível consultar o usuário.", e);
        }
        return null;
    }
}
