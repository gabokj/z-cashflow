package zcashflow.ui;

import zcashflow.model.Movimentacao;
import zcashflow.model.Usuario;
import zcashflow.service.Sessao;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Janela principal: barra lateral fixa + área de conteúdo com CardLayout,
 * conforme a estrutura de navegação do Projeto de Interfaces (Etapa 2).
 */
public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    public static final String CARD_DASHBOARD = "dashboard";
    public static final String CARD_CADASTRO = "cadastro";
    public static final String CARD_EXTRATO = "extrato";
    public static final String CARD_CATEGORIAS = "categorias";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel painelConteudo = new JPanel(cardLayout);
    private final Map<String, JButton> botoesNav = new LinkedHashMap<>();

    private DashboardPanel dashboardPanel;
    private CadastroMovimentacaoPanel cadastroPanel;
    private ExtratoPanel extratoPanel;
    private CategoriasPanel categoriasPanel;

    public MainFrame() {
        super("Z-CashFlow");
        montarTela();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 780);
        setMinimumSize(new Dimension(980, 600));
        setLocationRelativeTo(null);
        navegarPara(CARD_DASHBOARD);
    }

    private void montarTela() {
        setLayout(new BorderLayout());
        add(construirBarraSuperior(), BorderLayout.NORTH);
        add(construirSidebar(), BorderLayout.WEST);

        painelConteudo.setBackground(UIConstants.BG);
        dashboardPanel = new DashboardPanel(this);
        cadastroPanel = new CadastroMovimentacaoPanel(this);
        extratoPanel = new ExtratoPanel(this);
        categoriasPanel = new CategoriasPanel();

        painelConteudo.add(envolverComScroll(dashboardPanel), CARD_DASHBOARD);
        painelConteudo.add(envolverComScroll(cadastroPanel), CARD_CADASTRO);
        painelConteudo.add(envolverComScroll(extratoPanel), CARD_EXTRATO);
        painelConteudo.add(envolverComScroll(categoriasPanel), CARD_CATEGORIAS);
        add(painelConteudo, BorderLayout.CENTER);
    }

    private JScrollPane envolverComScroll(JComponent componente) {
        JScrollPane scroll = new JScrollPane(componente);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(UIConstants.BG);
        return scroll;
    }

    private JComponent construirBarraSuperior() {
        Usuario u = Sessao.getInstance().getUsuario();
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(UIConstants.PRIMARY_DARK);
        topo.setPreferredSize(new Dimension(100, 36));
        topo.setBorder(new EmptyBorder(0, 16, 0, 16));

        JLabel titulo = new JLabel("Z-CashFlow", SwingConstants.LEFT);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(UIConstants.FONT_BODY_BOLD);
        JLabel usuario = new JLabel(u == null ? "" : u.getNome() + "  •  " + u.getPerfil().getRotulo());
        usuario.setForeground(Color.WHITE);
        usuario.setFont(UIConstants.FONT_BODY);
        usuario.setPreferredSize(new Dimension(usuario.getPreferredSize().width + 16, 36));
        topo.add(titulo, BorderLayout.WEST);
        topo.add(usuario, BorderLayout.EAST);
        return topo;
    }

    private JComponent construirSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(Color.WHITE);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(16, 0, 16, 0));
        sidebar.setPreferredSize(new Dimension(220, 100));

        JLabel marca = new JLabel("Z-CashFlow");
        marca.setFont(UIConstants.FONT_BRAND);
        marca.setForeground(UIConstants.PRIMARY);
        marca.setAlignmentX(Component.LEFT_ALIGNMENT);
        marca.setBorder(new EmptyBorder(0, 20, 16, 0));
        sidebar.add(marca);
        sidebar.add(new JSeparator());
        sidebar.add(Box.createVerticalStrut(8));

        sidebar.add(criarItemNav("\u25A3  Dashboard", CARD_DASHBOARD));
        sidebar.add(criarItemNav("+  Nova movimentação", CARD_CADASTRO));
        sidebar.add(criarItemNav("\u2194  Extrato", CARD_EXTRATO));
        sidebar.add(criarItemNav("\u25C9  Categorias", CARD_CATEGORIAS));
        sidebar.add(Box.createVerticalGlue());

        JButton sair = new JButton("Sair");
        sair.setAlignmentX(Component.LEFT_ALIGNMENT);
        sair.setFocusPainted(true);
        sair.setBorder(new EmptyBorder(10, 20, 10, 20));
        sair.setForeground(UIConstants.DANGER);
        sair.setFont(UIConstants.FONT_BODY_BOLD);
        sair.setBorderPainted(false);
        sair.setContentAreaFilled(false);
        sair.setHorizontalAlignment(SwingConstants.LEFT);
        sair.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sair.addActionListener(e -> {
            Sessao.getInstance().encerrar();
            new LoginFrame().setVisible(true);
            dispose();
        });
        sidebar.add(sair);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(sidebar, BorderLayout.CENTER);
        wrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIConstants.BORDER));
        return wrapper;
    }

    private JButton criarItemNav(String texto, String destino) {
        JButton b = new JButton(texto);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(220, 44));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFont(UIConstants.FONT_BODY_BOLD);
        b.setFocusPainted(true);
        b.setBorder(new EmptyBorder(10, 20, 10, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorderPainted(false);
        b.addActionListener(e -> navegarPara(destino));
        botoesNav.put(destino, b);
        atualizarEstiloBotao(b, false);
        return b;
    }

    private void atualizarEstiloBotao(JButton b, boolean selecionado) {
        if (selecionado) {
            b.setBackground(UIConstants.PRIMARY_LIGHT);
            b.setForeground(UIConstants.PRIMARY);
            b.setContentAreaFilled(true);
            b.setOpaque(true);
        } else {
            b.setBackground(Color.WHITE);
            b.setForeground(UIConstants.TEXT_MUTED);
            b.setContentAreaFilled(false);
            b.setOpaque(false);
        }
    }

    /** Navega para uma tela, recarregando seus dados do banco. */
    public void navegarPara(String destino) {
        switch (destino) {
            case CARD_DASHBOARD -> dashboardPanel.atualizar();
            case CARD_CADASTRO -> cadastroPanel.prepararNovo();
            case CARD_EXTRATO -> extratoPanel.atualizar();
            case CARD_CATEGORIAS -> categoriasPanel.atualizar();
            default -> { }
        }
        mostrar(destino, destino);
    }

    /** Abre o formulário de movimentação preenchido para edição. */
    public void editarMovimentacao(Movimentacao mov) {
        cadastroPanel.prepararEdicao(mov);
        mostrar(CARD_CADASTRO, CARD_EXTRATO);
    }

    private void mostrar(String card, String itemMenuAtivo) {
        botoesNav.forEach((nome, botao) -> atualizarEstiloBotao(botao, nome.equals(itemMenuAtivo)));
        cardLayout.show(painelConteudo, card);
    }
}
