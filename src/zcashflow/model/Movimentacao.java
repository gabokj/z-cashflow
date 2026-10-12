package zcashflow.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Movimentação financeira (tabela "movimentacoes"): uma entrada ou saída de
 * dinheiro de um usuário, classificada por categoria. O valor é sempre positivo;
 * o sinal (+/-) vem do tipo de fluxo da categoria.
 */
public class Movimentacao {

    private int idMovimentacao;
    private Usuario usuario;
    private Categoria categoria;
    private BigDecimal valor;
    private LocalDate dataOperacao;
    private String descricao;

    public Movimentacao() {
    }

    public Movimentacao(Usuario usuario, Categoria categoria, BigDecimal valor, LocalDate dataOperacao, String descricao) {
        this.usuario = usuario;
        this.categoria = categoria;
        this.valor = valor;
        this.dataOperacao = dataOperacao;
        this.descricao = descricao;
    }

    public int getIdMovimentacao() {
        return idMovimentacao;
    }

    public void setIdMovimentacao(int idMovimentacao) {
        this.idMovimentacao = idMovimentacao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(LocalDate dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isEntrada() {
        return categoria.getTipoFluxo() == Categoria.TipoFluxo.ENTRADA;
    }

    /** Valor com sinal: positivo para entradas, negativo para saídas. */
    public BigDecimal getValorComSinal() {
        return isEntrada() ? valor : valor.negate();
    }
}
