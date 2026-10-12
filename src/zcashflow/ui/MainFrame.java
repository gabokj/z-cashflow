package zcashflow.ui;

import zcashflow.service.DadosService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Janela principal do sistema: barra lateral fixa + área de conteúdo com
 * CardLayout, reproduzindo a estrutura de navegação definida no Projeto de
 * Interfaces (Etapa 2): Dashboard, Movimentações (Nova / Extrato) e
 * Categorias sempre acessíveis pela lateral esquerda.
 */
public class MainFrame extends JFrame {

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
        setSize(1200, 760);
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
        categoriasPanel = new CategoriasPanel(this);

        painelConteudo.add(envolverComScroll(dashboardPanel), CARD_DASHBOARD);
        painelConteudo.add(envolverComScroll(cadastroPanel), CARD_CADASTRO);
        painelConteudo.add(envolverComScroll(extratoPanel), CARD_EXTRATO);
        painelConteudo.add(envolverComScroll(categoriasPanel), CARD_CATEGORIAS);

        add(painelConteudo, BorderLayout.CENTER);
    }

    private JScrollPane envolverComScroll(JComponent componente) {
        JScrollPane scroll = new JScrollPane(componente);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(UIConstants.BG);
        return scroll;
    }

    private JComponent construirBarraSuperior() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(UIConstants.PRIMARY_DARK);
        topo.setPreferredSize(new Dimension(100, 36));
        JLabel titulo = new JLabel("Z-CashFlow", SwingConstants.CENTER);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(UIConstants.FONT_BODY_BOLD);
        topo.add(titulo, BorderLayout.CENTER);
        return topo;
    }

    private JComponent construirSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(Color.WHITE);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(16, 0, 16, 0));
        sidebar.setPreferredSize(new Dimension(220, 100));

        JLabel marca = new JLabel("  Z-CashFlow");
        marca.setFont(UIConstants.FONT_BRAND);
        marca.setForeground(UIConstants.PRIMARY);
        marca.setAlignmentX(Component.LEFT_ALIGNMENT);
        marca.setBorder(new EmptyBorder(0, 10, 16, 0));
        sidebar.add(marca);
        sidebar.add(new JSeparator());
        sidebar.add(Box.createVerticalStrut(8));

        sidebar.add(criarItemNav("\u25A3  Dashboard", CARD_DASHBOARD));
        sidebar.add(criarItemNav("\u2795  Nova movimentação", CARD_CADASTRO));
        sidebar.add(criarItemNav("\u2194  Extrato", CARD_EXTRATO));
        sidebar.add(criarItemNav("\u25C9  Categorias", CARD_CATEGORIAS));

        sidebar.add(Box.createVerticalGlue());

        JButton sair = new JButton("Sair");
        sair.setAlignmentX(Component.LEFT_ALIGNMENT);
        sair.setFocusPainted(false);
        sair.setBorder(new EmptyBorder(10, 20, 10, 20));
        sair.setForeground(UIConstants.DANGER);
        sair.setBackground(Color.WHITE);
        sair.setBorderPainted(false);
        sair.setContentAreaFilled(false);
        sair.setHorizontalAlignment(SwingConstants.LEFT);
        sair.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sair.addActionListener(e -> {
            DadosService.getInstance().logout();
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
            this.dispose();
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
        b.setFocusPainted(false);
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

    /** Navega entre telas e atualiza os dados da tela de destino. */
    public void navegarPara(String destino) {
        botoesNav.forEach((nome, botao) -> atualizarEstiloBotao(botao, nome.equals(destino)));

        switch (destino) {
            case CARD_DASHBOARD -> dashboardPanel.atualizar();
            case CARD_CADASTRO -> cadastroPanel.atualizar();
            case CARD_EXTRATO -> extratoPanel.atualizar();
            case CARD_CATEGORIAS -> categoriasPanel.atualizar();
            default -> { }
        }
        cardLayout.show(painelConteudo, destino);
    }
}
