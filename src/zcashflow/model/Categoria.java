package zcashflow.model;

/**
 * Representa uma categoria de movimentação financeira.
 * Corresponde à tabela "categorias" do banco de dados.
 */
public class Categoria {

    public enum TipoFluxo {
        ENTRADA, SAIDA
    }

    private int idCategoria;
    private String nomeCategoria;
    private TipoFluxo tipoFluxo;

    public Categoria() {
    }

    public Categoria(String nomeCategoria, TipoFluxo tipoFluxo) {
        this.nomeCategoria = nomeCategoria;
        this.tipoFluxo = tipoFluxo;
    }

    public Categoria(int idCategoria, String nomeCategoria, TipoFluxo tipoFluxo) {
        this.idCategoria = idCategoria;
        this.nomeCategoria = nomeCategoria;
        this.tipoFluxo = tipoFluxo;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNomeCategoria() {
        return nomeCategoria;
    }

    public void setNomeCategoria(String nomeCategoria) {
        this.nomeCategoria = nomeCategoria;
    }

    public TipoFluxo getTipoFluxo() {
        return tipoFluxo;
    }

    public void setTipoFluxo(TipoFluxo tipoFluxo) {
        this.tipoFluxo = tipoFluxo;
    }

    @Override
    public String toString() {
        return nomeCategoria + " (" + tipoFluxo + ")";
    }
}
