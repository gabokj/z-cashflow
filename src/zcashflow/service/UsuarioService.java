package zcashflow.service;

import zcashflow.dao.UsuarioDAO;
import zcashflow.model.Usuario;
import zcashflow.util.AppException;
import zcashflow.util.SenhaUtil;

import java.util.regex.Pattern;

/**
 * Regras de autenticação e de cadastro de usuários.
 */
public class UsuarioService {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final int TAMANHO_MINIMO_SENHA = 6;

    private final UsuarioDAO dao = new UsuarioDAO();

    /** Valida as credenciais e abre a sessão. A mensagem de erro não revela qual dado está errado. */
    public Usuario autenticar(String email, String senha) throws AppException {
        if (email == null || email.isBlank() || senha == null || senha.isEmpty()) {
            throw new AppException("Preencha e-mail e senha.");
        }
        Usuario usuario = dao.buscarPorEmail(email.trim().toLowerCase());
        if (usuario == null || !SenhaUtil.verificar(senha, usuario.getSenhaHash())) {
            throw new AppException("E-mail ou senha inválidos.");
        }
        Sessao.getInstance().iniciar(usuario);
        return usuario;
    }

    /** Cria uma conta com perfil comum (USUARIO); administradores são definidos diretamente no banco. */
    public Usuario registrar(String nome, String email, String senha, String confirmacao) throws AppException {
        if (nome == null || nome.trim().length() < 3) {
            throw new AppException("Informe o nome completo (mínimo de 3 caracteres).");
        }
        if (nome.trim().length() > 100) {
            throw new AppException("O nome deve ter no máximo 100 caracteres.");
        }
        if (email == null || !EMAIL.matcher(email.trim()).matches() || email.trim().length() > 100) {
            throw new AppException("Informe um e-mail válido.");
        }
        if (senha == null || senha.length() < TAMANHO_MINIMO_SENHA) {
            throw new AppException("A senha deve ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres.");
        }
        if (!senha.equals(confirmacao)) {
            throw new AppException("A confirmação de senha não confere.");
        }

        Usuario usuario = new Usuario(nome.trim(), email.trim().toLowerCase(),
                SenhaUtil.gerarHash(senha), Usuario.Perfil.USUARIO);
        dao.inserir(usuario);
        return usuario;
    }
}
