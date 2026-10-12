package zcashflow.model;

import java.util.Objects;

/**
 * Categoria de movimentação (tabela "categorias"). O tipo de fluxo da categoria
 * (ENTRADA ou SAIDA) define se o valor soma ou subtrai do saldo.
 */
public class Categoria {

    public enum TipoFluxo {
        ENTRADA("Entrada"), SAIDA("Saída");

        private final String rotulo;

        TipoFluxo(String rotulo) {
            this.rotulo = rotulo;
        }

        public String getRotulo() {
            return rotulo;
        }

        @Override
        public String toString() {
            return rotulo;
        }
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

    /** Duas categorias são a mesma se tiverem o mesmo id (necessário para combos recarregados do banco). */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Categoria)) {
            return false;
        }
        Categoria outra = (Categoria) o;
        return idCategoria != 0 && idCategoria == outra.idCategoria;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCategoria);
    }

    /** Usado diretamente nas listas suspensas da interface. */
    @Override
    public String toString() {
        return nomeCategoria;
    }
}
