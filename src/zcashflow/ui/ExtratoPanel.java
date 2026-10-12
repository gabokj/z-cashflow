package zcashflow.ui;

import zcashflow.dao.FiltroMovimentacao;
import zcashflow.model.Categoria;
import zcashflow.model.Categoria.TipoFluxo;
import zcashflow.model.Movimentacao;
import zcashflow.service.CategoriaService;
import zcashflow.service.MovimentacaoService;
import zcashflow.util.AppException;
import zcashflow.util.Formatador;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Extrato (RF003): consulta com filtros por tipo, categoria e período, com
 * edição, exclusão e exportação para CSV (Figura 4 da Etapa 2).
 */
public class ExtratoPanel extends PainelRolavel {

    private static final long serialVersionUID = 1L;

    private final MainFrame principal;
    private final CategoriaService categoriaService = new CategoriaService();
    private final MovimentacaoService movimentacaoService = new MovimentacaoService();

    private final JComboBox<Object> comboTipo = new JComboBox<>(new Object[]{"Todos", TipoFluxo.ENTRADA, TipoFluxo.SAIDA});
    private final JComboBox<Object> comboCategoria = new JComboBox<>();
    private final javax.swing.JTextField campoDe = Componentes.campoTexto();
    private final javax.swing.JTextField campoAte = Componentes.campoTexto();
    private final DefaultTableModel modeloTabela =
            Componentes.modeloSomenteLeitura("Data", "Tipo", "Categoria", "Descrição", "Valor");
    private final JTable tabela = Componentes.tabela(modeloTabela);
    private final JLabel labelTotal = new JLabel(" ");
    private final JButton botaoEditar = Componentes.botaoSecundario("Editar");
    private final JButton botaoExcluir = Componentes.botaoPerigo("Excluir");

    /** Movimentações exibidas, na mesma ordem das linhas da tabela. */
    private List<Movimentacao> listaAtual = new ArrayList<>();
    private boolean atualizandoCombos;

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
        JLabel subtitulo = Componentes.subtitulo("Consulte, filtre, edite e exporte seus lançamentos");
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecalho.add(titulo);
        cabecalho.add(subtitulo);

        // Filtros
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filtros.setOpaque(false);
        filtros.setBorder(new EmptyBorder(16, 0, 10, 0));
        comboTipo.setFont(UIConstants.FONT_BODY);
        comboCategoria.setFont(UIConstants.FONT_BODY);
        campoDe.setPreferredSize(new Dimension(110, 34));
        campoAte.setPreferredSize(new Dimension(110, 34));

        comboTipo.addActionListener(e -> {
            if (!atualizandoCombos) {
                carregarCategorias();
                aplicarFiltros();
            }
        });
        comboCategoria.addActionListener(e -> {
            if (!atualizandoCombos) {
                aplicarFiltros();
            }
        });

        JButton filtrar = Componentes.botaoPrimario("Filtrar");
        filtrar.addActionListener(e -> aplicarFiltros());
        campoDe.addActionListener(e -> aplicarFiltros());
        campoAte.addActionListener(e -> aplicarFiltros());
        JButton limpar = Componentes.botaoSecundario("Limpar filtros");
        limpar.addActionListener(e -> limparFiltros());

        filtros.add(rotuloEcampo("TIPO", comboTipo));
        filtros.add(rotuloEcampo("CATEGORIA", comboCategoria));
        filtros.add(rotuloEcampo("DE (dd/mm/aaaa)", campoDe));
        filtros.add(rotuloEcampo("ATÉ (dd/mm/aaaa)", campoAte));
        filtros.add(filtrar);
        filtros.add(limpar);

        // Tabela
        tabela.getColumnModel().getColumn(4).setCellRenderer(new ValorCellRenderer());
        tabela.getColumnModel().getColumn(0).setPreferredWidth(90);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(70);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(280);
        tabela.getSelectionModel().addListSelectionListener(e -> atualizarBotoes());
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarSelecionada();
                }
            }
        });

        botaoEditar.addActionListener(e -> editarSelecionada());
        botaoExcluir.addActionListener(e -> excluirSelecionada());
        JButton exportar = Componentes.botaoSecundario("Exportar CSV");
        exportar.addActionListener(e -> exportarCsv());

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        acoes.setOpaque(false);
        acoes.add(botaoEditar);
        acoes.add(botaoExcluir);
        acoes.add(exportar);

        labelTotal.setFont(UIConstants.FONT_BODY_BOLD);
        labelTotal.setForeground(UIConstants.TEXT);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);
        rodape.setBorder(new EmptyBorder(10, 0, 0, 0));
        labelTotal.setBorder(new EmptyBorder(10, 10, 0, 0));
        rodape.add(acoes, BorderLayout.NORTH);
        rodape.add(labelTotal, BorderLayout.SOUTH);

        JPanel cartaoTabela = Componentes.cartao();
        cartaoTabela.setLayout(new BorderLayout());
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(null);
        cartaoTabela.add(scroll, BorderLayout.CENTER);
        cartaoTabela.add(rodape, BorderLayout.SOUTH);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.add(cabecalho, BorderLayout.NORTH);
        topo.add(filtros, BorderLayout.SOUTH);

        add(topo, BorderLayout.NORTH);
        add(cartaoTabela, BorderLayout.CENTER);
        atualizarBotoes();
    }

    private JPanel rotuloEcampo(String rotulo, JComponent campo) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel l = Componentes.rotuloPara(rotulo, campo);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
        p.add(Box.createVerticalStrut(4));
        p.add(campo);
        return p;
    }

    /** Chamado sempre que a tela é exibida. */
    public void atualizar() {
        carregarCategorias();
        aplicarFiltros();
    }

    private TipoFluxo tipoSelecionado() {
        Object s = comboTipo.getSelectedItem();
        return s instanceof TipoFluxo ? (TipoFluxo) s : null;
    }

    private void carregarCategorias() {
        atualizandoCombos = true;
        try {
            Object anterior = comboCategoria.getSelectedItem();
            comboCategoria.removeAllItems();
            comboCategoria.addItem("Todas");
            TipoFluxo tipo = tipoSelecionado();
            List<Categoria> categorias = tipo == null ? categoriaService.listar() : categoriaService.listarPorTipo(tipo);
            for (Categoria c : categorias) {
                comboCategoria.addItem(c);
            }
            if (anterior instanceof Categoria && categorias.contains(anterior)) {
                comboCategoria.setSelectedItem(anterior);
            } else {
                comboCategoria.setSelectedIndex(0);
            }
        } catch (AppException e) {
            Mensagens.erro(this, e.getMessage());
        } finally {
            atualizandoCombos = false;
        }
    }

    private void limparFiltros() {
        atualizandoCombos = true;
        comboTipo.setSelectedIndex(0);
        campoDe.setText("");
        campoAte.setText("");
        atualizandoCombos = false;
        carregarCategorias();
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        try {
            FiltroMovimentacao filtro = movimentacaoService.novoFiltro()
                    .tipo(tipoSelecionado())
                    .dataInicio(Formatador.lerDataOpcional(campoDe.getText()))
                    .dataFim(Formatador.lerDataOpcional(campoAte.getText()));
            Object cat = comboCategoria.getSelectedItem();
            if (cat instanceof Categoria) {
                filtro.idCategoria(((Categoria) cat).getIdCategoria());
            }
            listaAtual = movimentacaoService.listar(filtro);
        } catch (AppException e) {
            Mensagens.erro(this, e.getMessage());
            return;
        }

        modeloTabela.setRowCount(0);
        BigDecimal entradas = BigDecimal.ZERO;
        BigDecimal saidas = BigDecimal.ZERO;
        for (Movimentacao m : listaAtual) {
            modeloTabela.addRow(new Object[]{
                    Formatador.data(m.getDataOperacao()),
                    m.getCategoria().getTipoFluxo().getRotulo(),
                    m.getCategoria().getNomeCategoria(),
                    m.getDescricao(),
                    (m.isEntrada() ? "+ " : "- ") + Formatador.moeda(m.getValor())
            });
            if (m.isEntrada()) {
                entradas = entradas.add(m.getValor());
            } else {
                saidas = saidas.add(m.getValor());
            }
        }
        labelTotal.setText("Entradas: " + Formatador.moeda(entradas) + "     Saídas: " + Formatador.moeda(saidas)
                + "     Saldo: " + Formatador.moeda(entradas.subtract(saidas))
                + "     (" + listaAtual.size() + " lançamento(s))");
        atualizarBotoes();
    }

    private Movimentacao selecionada() {
        int linha = tabela.getSelectedRow();
        return linha >= 0 && linha < listaAtual.size() ? listaAtual.get(linha) : null;
    }

    private void atualizarBotoes() {
        boolean temSelecao = selecionada() != null;
        botaoEditar.setEnabled(temSelecao);
        botaoExcluir.setEnabled(temSelecao);
    }

    private void editarSelecionada() {
        Movimentacao m = selecionada();
        if (m != null) {
            principal.editarMovimentacao(m);
        }
    }

    private void excluirSelecionada() {
        Movimentacao m = selecionada();
        if (m == null) {
            return;
        }
        String resumo = Formatador.data(m.getDataOperacao()) + " — " + m.getDescricao()
                + " (" + Formatador.moeda(m.getValor()) + ")";
        if (!Mensagens.confirmar(this, "Excluir este lançamento?\n\n" + resumo + "\n\nEsta ação não pode ser desfeita.",
                "Confirmar exclusão")) {
            return;
        }
        try {
            movimentacaoService.excluir(m.getIdMovimentacao());
        } catch (AppException e) {
            Mensagens.erro(this, e.getMessage());
        }
        aplicarFiltros();
    }

    /** Exporta o que está na tabela (respeitando os filtros) em CSV com separador ";" para o Excel. */
    private void exportarCsv() {
        if (listaAtual.isEmpty()) {
            Mensagens.info(this, "Não há lançamentos para exportar com os filtros atuais.");
            return;
        }
        JFileChooser escolha = new JFileChooser();
        escolha.setDialogTitle("Exportar extrato");
        escolha.setFileFilter(new FileNameExtensionFilter("Arquivo CSV (*.csv)", "csv"));
        escolha.setSelectedFile(new File("extrato_zcashflow_" + LocalDate.now() + ".csv"));
        if (escolha.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File arquivo = escolha.getSelectedFile();
        if (!arquivo.getName().toLowerCase().endsWith(".csv")) {
            arquivo = new File(arquivo.getParentFile(), arquivo.getName() + ".csv");
        }
        if (arquivo.exists() && !Mensagens.confirmar(this, "O arquivo já existe. Deseja substituí-lo?", "Substituir arquivo")) {
            return;
        }
        try (Writer w = Files.newBufferedWriter(arquivo.toPath(), StandardCharsets.UTF_8)) {
            w.write('\uFEFF'); // BOM: o Excel reconhece os acentos
            w.write("Data;Tipo;Categoria;Descrição;Valor\r\n");
            for (Movimentacao m : listaAtual) {
                w.write(String.join(";",
                        Formatador.data(m.getDataOperacao()),
                        m.getCategoria().getTipoFluxo().getRotulo(),
                        csv(m.getCategoria().getNomeCategoria()),
                        csv(m.getDescricao()),
                        Formatador.valorEditavel(m.getValorComSinal())));
                w.write("\r\n");
            }
        } catch (IOException e) {
            Mensagens.erro(this, "Não foi possível gravar o arquivo: " + e.getMessage());
            return;
        }
        Mensagens.info(this, "Extrato exportado com sucesso:\n" + arquivo.getAbsolutePath());
    }

    private String csv(String texto) {
        return "\"" + texto.replace("\"", "\"\"") + "\"";
    }
}
