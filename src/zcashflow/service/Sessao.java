package zcashflow.service;

import zcashflow.model.Usuario;
import zcashflow.util.AppException;

/**
 * Guarda o usuário autenticado durante a execução do programa.
 */
public final class Sessao {

    private static final Sessao INSTANCIA = new Sessao();

    private Usuario usuario;

    private Sessao() {
    }

    public static Sessao getInstance() {
        return INSTANCIA;
    }

    public void iniciar(Usuario usuario) {
        this.usuario = usuario;
    }

    public void encerrar() {
        this.usuario = null;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    /** Retorna o usuário logado ou falha se não houver sessão ativa. */
    public Usuario exigirUsuario() throws AppException {
        if (usuario == null) {
            throw new AppException("Sessão expirada. Faça login novamente.");
        }
        return usuario;
    }

    public void exigirAdmin() throws AppException {
        if (!exigirUsuario().isAdmin()) {
            throw new AppException("Apenas administradores podem executar esta operação.");
        }
    }
}
