package zcashflow.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Representa uma movimentação financeira (entrada ou saída de dinheiro).
 * Corresponde à tabela "movimentacoes" do banco de dados.
 */
public class Movimentacao {

    private static final DateTimeFormatter FORMATO_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private int idMovimentacao;
    private Usuario usuario;
    private Categoria categoria;
    private double valor;
    private LocalDate dataOperacao;
    private String descricao;

    public Movimentacao() {
    }

    public Movimentacao(Usuario usuario, Categoria categoria, double valor, LocalDate dataOperacao, String descricao) {
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

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
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

    /**
     * RF003 - Extrato Automatizado: formata a linha do extrato no padrão
     * DD/MM/YYYY, facilitando a leitura e auditoria pelo usuário.
     */
    @Override
    public String toString() {
        String sinal = categoria.getTipoFluxo() == Categoria.TipoFluxo.ENTRADA ? "+" : "-";
        return String.format("[%s] %-25s %s R$ %10.2f  (%s)",
                dataOperacao.format(FORMATO_BR),
                categoria.getNomeCategoria(),
                sinal,
                valor,
                descricao == null ? "" : descricao);
    }
}
