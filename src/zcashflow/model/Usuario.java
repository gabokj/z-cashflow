package zcashflow.model;

import java.time.LocalDate;

/**
 * Usuário do sistema (tabela "usuarios"). A senha nunca é guardada em texto
 * puro: o objeto carrega apenas o hash PBKDF2.
 */
public class Usuario {

    public enum Perfil {
        USUARIO("Usuário"), ADMIN("Administrador");

        private final String rotulo;

        Perfil(String rotulo) {
            this.rotulo = rotulo;
        }

        public String getRotulo() {
            return rotulo;
        }
    }

    private int idUsuario;
    private String nome;
    private String email;
    private String senhaHash;
    private Perfil perfil = Perfil.USUARIO;
    private LocalDate dataCadastro;

    public Usuario() {
    }

    public Usuario(String nome, String email, String senhaHash, Perfil perfil) {
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.perfil = perfil;
        this.dataCadastro = LocalDate.now();
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public boolean isAdmin() {
        return perfil == Perfil.ADMIN;
    }

    @Override
    public String toString() {
        return nome + " (" + email + ")";
    }
}
