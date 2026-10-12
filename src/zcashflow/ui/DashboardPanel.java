package zcashflow.ui;

import zcashflow.model.Categoria.TipoFluxo;
import zcashflow.model.Movimentacao;
import zcashflow.model.Usuario;
import zcashflow.service.DadosService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Painel principal (Dashboard) — reproduz a Figura 2/7 do Projeto de
 * Interfaces: saldo atual (RF004), entradas/saídas do mês e últimas
 * movimentações (RF003).
 */
public class DashboardPanel extends JPanel {

    private static final DateTimeFormatter FORMATO_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final MainFrame principal;
    private final DadosService dados = DadosService.getInstance();

    private final JLabel labelSaudacao = new JLabel();
    private final JLabel labelSaldo = new JLabel();
    private final JLabel labelEntradas = new JLabel();
    private final JLabel labelSaidas = new JLabel();
    private final DefaultTableModel modeloTabela = new DefaultTableModel(
            new Object[]{"Data", "Categoria", "Descrição", "Valor"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public DashboardPanel(MainFrame principal) {
        this.principal = principal;
        setBackground(UIConstants.BG);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(28, 32, 28, 32));
        montar();
    }

    private void montar() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        labelSaudacao.setFont(UIConstants.FONT_H1);
        labelSaudacao.setForeground(UIConstants.TEXT);
        JLabel subtitulo = Componentes.subtitulo("Resumo do mês atual");
        textos.add(labelSaudacao);
        textos.add(subtitulo);
        cabecalho.add(textos, BorderLayout.WEST);

        JButton botaoNovaMov = Componentes.botaoPrimario("+ Nova movimentação");
        botaoNovaMov.addActionListener(e -> principal.navegarPara(MainFrame.CARD_CADASTRO));
        JPanel wrapBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        wrapBotao.setOpaque(false);
        wrapBotao.add(botaoNovaMov);
        cabecalho.add(wrapBotao, BorderLayout.EAST);

        JPanel cartoes = new JPanel(new GridLayout(1, 3, 16, 0));
        cartoes.setOpaque(false);
        cartoes.setBorder(new EmptyBorder(20, 0, 20, 0));
        cartoes.add(criarCartaoResumo("SALDO ATUAL", labelSaldo, UIConstants.PRIMARY, "Atualizado agora"));
        cartoes.add(criarCartaoResumo("ENTRADAS (mês)", labelEntradas, UIConstants.SUCCESS, null));
        cartoes.add(criarCartaoResumo("SAÍDAS (mês)", labelSaidas, UIConstants.DANGER, null));

        JPanel painelTabela = Componentes.cartao();
        painelTabela.setLayout(new BorderLayout());
        JPanel cabecalhoTabela = new JPanel(new BorderLayout());
        cabecalhoTabela.setOpaque(false);
        JLabel tituloTabela = new JLabel("Últimas movimentações");
        tituloTabela.setFont(UIConstants.FONT_H2);
        tituloTabela.setForeground(UIConstants.TEXT);
        cabecalhoTabela.add(tituloTabela, BorderLayout.WEST);

        JButton verExtrato = Componentes.botaoSecundario("Ver extrato completo");
        verExtrato.addActionListener(e -> principal.navegarPara(MainFrame.CARD_EXTRATO));
        JPanel wrapExtrato = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        wrapExtrato.setOpaque(false);
        wrapExtrato.add(verExtrato);
        cabecalhoTabela.add(wrapExtrato, BorderLayout.EAST);

        JTable tabela = new JTable(modeloTabela);
        tabela.setRowHeight(28);
        tabela.setFont(UIConstants.FONT_BODY);
        tabela.getTableHeader().setFont(UIConstants.FONT_LABEL);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setFillsViewportHeight(true);

        painelTabela.add(cabecalhoTabela, BorderLayout.NORTH);
        painelTabela.add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(cartoes, BorderLayout.NORTH);
        centro.add(painelTabela, BorderLayout.CENTER);

        add(cabecalho, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
    }

    private JPanel criarCartaoResumo(String rotulo, JLabel labelValor, Color cor, String rodape) {
        JPanel cartao = Componentes.cartao();
        cartao.setLayout(new BoxLayout(cartao, BoxLayout.Y_AXIS));
        JLabel lRotulo = Componentes.rotulo(rotulo);
        lRotulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        labelValor.setFont(new Font("Segoe UI", Font.BOLD, 24));
        labelValor.setForeground(cor);
        labelValor.setAlignmentX(Component.LEFT_ALIGNMENT);
        cartao.add(lRotulo);
        cartao.add(Box.createVerticalStrut(8));
        cartao.add(labelValor);
        if (rodape != null) {
            JLabel lRodape = Componentes.subtitulo(rodape + "  •  RF004");
            lRodape.setAlignmentX(Component.LEFT_ALIGNMENT);
            cartao.add(Box.createVerticalStrut(6));
            cartao.add(lRodape);
        }
        return cartao;
    }

    /** Recarrega os dados exibidos a partir do DadosService. */
    public void atualizar() {
        Usuario usuario = dados.getUsuarioLogado();
        if (usuario == null) {
            return;
        }
        labelSaudacao.setText("Olá, " + primeiroNome(usuario.getNome()));

        double saldo = dados.calcularSaldo(usuario.getIdUsuario());
        labelSaldo.setText(formatarMoeda(saldo));

        LocalDate hoje = LocalDate.now();
        double entradas = dados.totalPorTipoNoMes(usuario.getIdUsuario(), TipoFluxo.ENTRADA, hoje.getYear(), hoje.getMonthValue());
        double saidas = dados.totalPorTipoNoMes(usuario.getIdUsuario(), TipoFluxo.SAIDA, hoje.getYear(), hoje.getMonthValue());
        labelEntradas.setText(formatarMoeda(entradas));
        labelSaidas.setText(formatarMoeda(saidas));

        modeloTabela.setRowCount(0);
        List<Movimentacao> movs = dados.listarMovimentacoes(usuario.getIdUsuario());
        int limite = Math.min(8, movs.size());
        for (int i = 0; i < limite; i++) {
            Movimentacao m = movs.get(i);
            String sinal = m.getCategoria().getTipoFluxo() == TipoFluxo.ENTRADA ? "+ " : "- ";
            modeloTabela.addRow(new Object[]{
                    m.getDataOperacao().format(FORMATO_BR),
                    m.getCategoria().getNomeCategoria(),
                    m.getDescricao(),
                    sinal + formatarMoeda(m.getValor())
            });
        }
    }

    private String primeiroNome(String nomeCompleto) {
        return nomeCompleto.split(" ")[0];
    }

    private String formatarMoeda(double valor) {
        return String.format(Locale.of("pt", "BR"), "R$ %,.2f", valor);
    }
}
