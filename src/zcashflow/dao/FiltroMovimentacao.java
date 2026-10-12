package zcashflow.dao;

import zcashflow.model.Categoria.TipoFluxo;

import java.time.LocalDate;

/**
 * Critérios opcionais para consultar movimentações (extrato). Campos nulos
 * significam "sem filtro". O id do usuário é obrigatório: cada usuário só
 * enxerga as próprias movimentações.
 */
public class FiltroMovimentacao {

    private final int idUsuario;
    private TipoFluxo tipo;
    private Integer idCategoria;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Integer limite;

    public FiltroMovimentacao(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public TipoFluxo getTipo() {
        return tipo;
    }

    public FiltroMovimentacao tipo(TipoFluxo tipo) {
        this.tipo = tipo;
        return this;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public FiltroMovimentacao idCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
        return this;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public FiltroMovimentacao dataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
        return this;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public FiltroMovimentacao dataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
        return this;
    }

    public Integer getLimite() {
        return limite;
    }

    public FiltroMovimentacao limite(Integer limite) {
        this.limite = limite;
        return this;
    }
}
