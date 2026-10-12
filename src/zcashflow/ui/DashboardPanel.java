package zcashflow.ui;

import zcashflow.model.Movimentacao;
import zcashflow.model.ResumoMensal;
import zcashflow.model.Usuario;
import zcashflow.service.MovimentacaoService;
import zcashflow.service.Sessao;
import zcashflow.util.AppException;
import zcashflow.util.Formatador;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;

/**
 * Dashboard: saldo atual (RF004), entradas/saídas do mês, gráfico dos últimos
 * seis meses e últimas movimentações (RF003) — Figuras 2 e 7 da Etapa 2.
 */
public class DashboardPanel extends PainelRolavel {

    private static final long serialVersionUID = 1L;
    private static final int MESES_GRAFICO = 6;
    private static final int ULTIMAS_MOVIMENTACOES = 6;

    private final MainFrame principal;
    private final MovimentacaoService movimentacaoService = new MovimentacaoService();

    private final JLabel labelSaudacao = new JLabel();
    private final JLabel labelPeriodo = Componentes.subtitulo(" ");
    private final JLabel labelSaldo = new JLabel();
    private final JLabel labelEntradas = new JLabel();
    private final JLabel labelSaidas = new JLabel();
    private final GraficoBarras grafico = new GraficoBarras();
    private final DefaultTableModel modeloTabela =
            Componentes.modeloSomenteLeitura("Data", "Categoria", "Descrição", "Valor");

    public DashboardPanel(MainFrame principal) {
        this.principal = principal;
        setBackground(UIConstants.BG);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(28, 32, 28, 32));
        montar();
    }

    private void montar() {
        // Cabeçalho
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        labelSaudacao.setFont(UIConstants.FONT_H1);
        labelSaudacao.setForeground(UIConstants.TEXT);
        textos.add(labelSaudacao);
        textos.add(labelPeriodo);
        cabecalho.add(textos, BorderLayout.WEST);

        JButton botaoNova = Componentes.botaoPrimario("+ Nova movimentação");
        botaoNova.addActionListener(e -> principal.navegarPara(MainFrame.CARD_CADASTRO));
        JPanel wrapBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        wrapBotao.setOpaque(false);
        wrapBotao.add(botaoNova);
        cabecalho.add(wrapBotao, BorderLayout.EAST);

        // Cartões de resumo
        JPanel cartoes = new JPanel(new GridLayout(1, 3, 16, 0));
        cartoes.setOpaque(false);
        cartoes.setBorder(new EmptyBorder(20, 0, 16, 0));
        cartoes.add(criarCartaoResumo("SALDO ATUAL", labelSaldo, UIConstants.PRIMARY, "Atualizado agora"));
        cartoes.add(criarCartaoResumo("ENTRADAS (mês)", labelEntradas, UIConstants.SUCCESS, "Total recebido no mês"));
        cartoes.add(criarCartaoResumo("SAÍDAS (mês)", labelSaidas, UIConstants.DANGER, "Total gasto no mês"));

        // Gráfico
        JPanel cartaoGrafico = Componentes.cartao();
        cartaoGrafico.setLayout(new BorderLayout(0, 8));
        JLabel tituloGrafico = new JLabel("Entradas x Saídas — últimos " + MESES_GRAFICO + " meses");
        tituloGrafico.setFont(UIConstants.FONT_H2);
        tituloGrafico.setForeground(UIConstants.TEXT);
        cartaoGrafico.add(tituloGrafico, BorderLayout.NORTH);
        cartaoGrafico.add(grafico, BorderLayout.CENTER);

        // Tabela de últimas movimentações
        JPanel cartaoTabela = Componentes.cartao();
        cartaoTabela.setLayout(new BorderLayout(0, 8));
        JPanel cabecalhoTabela = new JPanel(new BorderLayout());
        cabecalhoTabela.setOpaque(false);
        JLabel tituloTabela = new JLabel("Últimas movimentações");
        tituloTabela.setFont(UIConstants.FONT_H2);
        tituloTabela.setForeground(UIConstants.TEXT);
        cabecalhoTabela.add(tituloTabela, BorderLayout.WEST);
        JButton verExtrato = Componentes.botaoSecundario("Ver extrato completo");
        verExtrato.addActionListener(e -> principal.navegarPara(MainFrame.CARD_EXTRATO));
        cabecalhoTabela.add(verExtrato, BorderLayout.EAST);

        JTable tabela = Componentes.tabela(modeloTabela);
        tabela.getColumnModel().getColumn(3).setCellRenderer(new ValorCellRenderer());
        tabela.getColumnModel().getColumn(0).setPreferredWidth(90);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(300);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(null);
        scroll.setPreferredSize(new java.awt.Dimension(300, 30 * ULTIMAS_MOVIMENTACOES + 30));
        cartaoTabela.add(cabecalhoTabela, BorderLayout.NORTH);
        cartaoTabela.add(scroll, BorderLayout.CENTER);

        JPanel corpo = new JPanel();
        corpo.setOpaque(false);
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        cartoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        cartaoGrafico.setAlignmentX(Component.LEFT_ALIGNMENT);
        cartaoTabela.setAlignmentX(Component.LEFT_ALIGNMENT);
        corpo.add(cartoes);
        corpo.add(cartaoGrafico);
        corpo.add(Box.createVerticalStrut(16));
        corpo.add(cartaoTabela);

        add(cabecalho, BorderLayout.NORTH);
        add(corpo, BorderLayout.CENTER);
    }

    private JPanel criarCartaoResumo(String rotulo, JLabel labelValor, Color cor, String rodape) {
        JPanel cartao = Componentes.cartao();
        cartao.setLayout(new BoxLayout(cartao, BoxLayout.Y_AXIS));
        JLabel lRotulo = Componentes.rotulo(rotulo);
        lRotulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        labelValor.setFont(new Font("Segoe UI", Font.BOLD, 24));
        labelValor.setForeground(cor);
        labelValor.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lRodape = Componentes.subtitulo(rodape);
        lRodape.setAlignmentX(Component.LEFT_ALIGNMENT);
        cartao.add(lRotulo);
        cartao.add(Box.createVerticalStrut(8));
        cartao.add(labelValor);
        cartao.add(Box.createVerticalStrut(6));
        cartao.add(lRodape);
        return cartao;
    }

    /** Recarrega todos os dados exibidos a partir do banco. */
    public void atualizar() {
        Usuario usuario = Sessao.getInstance().getUsuario();
        if (usuario == null) {
            return;
        }
        labelSaudacao.setText("Olá, " + usuario.getNome().split(" ")[0]);
        YearMonth atual = YearMonth.now();
        String mes = atual.getMonth().getDisplayName(TextStyle.FULL, Formatador.PT_BR);
        labelPeriodo.setText("Resumo de " + mes + " de " + atual.getYear());

        try {
            BigDecimal saldo = movimentacaoService.saldo();
            List<ResumoMensal> resumo = movimentacaoService.resumoMensal(MESES_GRAFICO);
            ResumoMensal mesAtual = resumo.get(resumo.size() - 1);
            List<Movimentacao> recentes = movimentacaoService.recentes(ULTIMAS_MOVIMENTACOES);

            labelSaldo.setText(Formatador.moeda(saldo));
            labelEntradas.setText(Formatador.moeda(mesAtual.entradas()));
            labelSaidas.setText(Formatador.moeda(mesAtual.saidas()));
            grafico.setDados(resumo);

            modeloTabela.setRowCount(0);
            for (Movimentacao m : recentes) {
                modeloTabela.addRow(new Object[]{
                        Formatador.data(m.getDataOperacao()),
                        m.getCategoria().getNomeCategoria(),
                        m.getDescricao(),
                        (m.isEntrada() ? "+ " : "- ") + Formatador.moeda(m.getValor())
                });
            }
        } catch (AppException e) {
            Mensagens.erro(this, e.getMessage());
        }
    }
}
