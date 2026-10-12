package zcashflow.ui;

import zcashflow.model.Categoria;
import zcashflow.model.Categoria.TipoFluxo;
import zcashflow.model.Movimentacao;
import zcashflow.model.Usuario;
import zcashflow.service.DadosService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Tela de extrato (RF003), reproduzindo a Figura 4 do Projeto de
 * Interfaces: lista tabular com filtros por tipo e categoria, datas
 * sempre no formato brasileiro dd/mm/aaaa.
 */
public class ExtratoPanel extends JPanel {

    private static final DateTimeFormatter FORMATO_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final MainFrame principal;
    private final DadosService dados = DadosService.getInstance();

    private final JComboBox<String> comboTipo = new JComboBox<>(new String[]{"Todos", "Entrada", "Saída"});
    private final JComboBox<Object> comboCategoria = new JComboBox<>();
    private final DefaultTableModel modeloTabela = new DefaultTableModel(
            new Object[]{"Data", "Categoria", "Descrição", "Valor"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JLabel labelTotal = new JLabel();
    private boolean atualizandoCombos = false;

    public ExtratoPanel(MainFrame principal) {
        this.principal = principal;
        setBackground(UIConstants.BG);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(28, 32, 28, 32));
        montar();
    }

    private void montar() {
        JPanel cabecalho = new JPanel();
        cabecalho.setOpaque(false);
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        JLabel titulo = Componentes.titulo("Extrato de movimentações");
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitulo = Componentes.subtitulo("RF003 — extrato automatizado (formato dd/mm/aaaa)");
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecalho.add(titulo);
        cabecalho.add(subtitulo);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filtros.setOpaque(false);
        filtros.setBorder(new EmptyBorder(18, 0, 10, 0));
        comboTipo.setFont(UIConstants.FONT_BODY);
        comboCategoria.setFont(UIConstants.FONT_BODY);
        comboTipo.addActionListener(e -> {
            if (atualizandoCombos) {
                return;
            }
            atualizarComboCategorias();
            aplicarFiltros();
        });
        comboCategoria.addActionListener(e -> {
            if (atualizandoCombos) {
                return;
            }
            aplicarFiltros();
        });

        JButton limpar = Componentes.botaoSecundario("Limpar filtros");
        limpar.addActionListener(e -> {
            comboTipo.setSelectedIndex(0);
            atualizarComboCategorias();
            aplicarFiltros();
        });

        filtros.add(rotuloEcampo("Tipo", comboTipo));
        filtros.add(rotuloEcampo("Categoria", comboCategoria));
        filtros.add(limpar);

        JPanel painelTabela = Componentes.cartao();
        painelTabela.setLayout(new BorderLayout());

        JTable tabela = new JTable(modeloTabela);
        tabela.setRowHeight(30);
        tabela.setFont(UIConstants.FONT_BODY);
        tabela.getTableHeader().setFont(UIConstants.FONT_LABEL);
        tabela.setShowGrid(false);
        tabela.setFillsViewportHeight(true);

        labelTotal.setFont(UIConstants.FONT_H2);
        labelTotal.setForeground(UIConstants.TEXT);
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.setOpaque(false);
        rodape.add(labelTotal);

        painelTabela.add(new JScrollPane(tabela), BorderLayout.CENTER);
        painelTabela.add(rodape, BorderLayout.SOUTH);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.add(cabecalho, BorderLayout.NORTH);
        topo.add(filtros, BorderLayout.SOUTH);

        add(topo, BorderLayout.NORTH);
        add(painelTabela, BorderLayout.CENTER);
    }

    private JPanel rotuloEcampo(String rotulo, JComponent campo) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel l = Componentes.rotulo(rotulo);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
        p.add(Box.createVerticalStrut(4));
        p.add(campo);
        return p;
    }

    private void atualizarComboCategorias() {
        atualizandoCombos = true;
        try {
            Object selecionadoAnterior = comboCategoria.getSelectedItem();
            comboCategoria.removeAllItems();
            comboCategoria.addItem("Todas");

            TipoFluxo tipo = tipoFiltroSelecionado();
            List<Categoria> categorias = (tipo == null)
                    ? dados.listarCategorias()
                    : dados.listarCategoriasPorTipo(tipo);
            for (Categoria c : categorias) {
                comboCategoria.addItem(c);
            }
            if (selecionadoAnterior instanceof Categoria && categorias.contains(selecionadoAnterior)) {
                comboCategoria.setSelectedItem(selecionadoAnterior);
            } else {
                comboCategoria.setSelectedIndex(0);
            }
        } finally {
            atualizandoCombos = false;
        }
    }

    private TipoFluxo tipoFiltroSelecionado() {
        String selecionado = (String) comboTipo.getSelectedItem();
        if ("Entrada".equals(selecionado)) {
            return TipoFluxo.ENTRADA;
        }
        if ("Saída".equals(selecionado)) {
            return TipoFluxo.SAIDA;
        }
        return null;
    }

    private void aplicarFiltros() {
        Usuario usuario = dados.getUsuarioLogado();
        if (usuario == null) {
            return;
        }

        TipoFluxo tipoFiltro = tipoFiltroSelecionado();
        Object categoriaSelecionada = comboCategoria.getSelectedItem();
        Categoria categoriaFiltro = (categoriaSelecionada instanceof Categoria) ? (Categoria) categoriaSelecionada : null;

        List<Movimentacao> movs = dados.listarMovimentacoes(usuario.getIdUsuario());

        modeloTabela.setRowCount(0);
        double totalEntradas = 0;
        double totalSaidas = 0;
        for (Movimentacao m : movs) {
            if (tipoFiltro != null && m.getCategoria().getTipoFluxo() != tipoFiltro) {
                continue;
            }
            if (categoriaFiltro != null && m.getCategoria().getIdCategoria() != categoriaFiltro.getIdCategoria()) {
                continue;
            }
            boolean entrada = m.getCategoria().getTipoFluxo() == TipoFluxo.ENTRADA;
            String sinal = entrada ? "+ " : "- ";
            modeloTabela.addRow(new Object[]{
                    m.getDataOperacao().format(FORMATO_BR),
                    m.getCategoria().getNomeCategoria(),
                    m.getDescricao(),
                    sinal + formatarMoeda(m.getValor())
            });
            if (entrada) {
                totalEntradas += m.getValor();
            } else {
                totalSaidas += m.getValor();
            }
        }

        labelTotal.setText(String.format("Entradas: %s    Saídas: %s    Saldo do período: %s",
                formatarMoeda(totalEntradas), formatarMoeda(totalSaidas), formatarMoeda(totalEntradas - totalSaidas)));
    }

    private String formatarMoeda(double valor) {
        return String.format(Locale.of("pt", "BR"), "R$ %,.2f", valor);
    }

    /** Chamado sempre que a tela é exibida. */
    public void atualizar() {
        atualizarComboCategorias();
        aplicarFiltros();
    }
}
