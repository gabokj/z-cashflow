package zcashflow.ui;

import zcashflow.model.Categoria;
import zcashflow.model.Categoria.TipoFluxo;
import zcashflow.service.DadosService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tela de gestão de categorias (Figura 5 do Projeto de Interfaces),
 * usada principalmente pelo perfil Administrador para manter a lista
 * de categorias de entrada e saída.
 */
public class CategoriasPanel extends JPanel {

    private final DadosService dados = DadosService.getInstance();
    private final DefaultTableModel modeloTabela = new DefaultTableModel(
            new Object[]{"Nome", "Tipo"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public CategoriasPanel(MainFrame principal) {
        setBackground(UIConstants.BG);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(28, 32, 28, 32));
        montar();
    }

    private void montar() {
        JPanel cabecalho = new JPanel();
        cabecalho.setOpaque(false);
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        JLabel titulo = Componentes.titulo("Categorias");
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitulo = Componentes.subtitulo("gestão das categorias de entrada e saída");
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecalho.add(titulo);
        cabecalho.add(subtitulo);

        JButton novaCategoria = Componentes.botaoPrimario("+ Nova categoria");
        novaCategoria.addActionListener(e -> abrirDialogoNovaCategoria());
        JPanel linhaBotao = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaBotao.setOpaque(false);
        linhaBotao.setBorder(new EmptyBorder(16, 0, 10, 0));
        linhaBotao.add(novaCategoria);

        JPanel painelTabela = Componentes.cartao();
        painelTabela.setLayout(new BorderLayout());
        JTable tabela = new JTable(modeloTabela);
        tabela.setRowHeight(30);
        tabela.setFont(UIConstants.FONT_BODY);
        tabela.getTableHeader().setFont(UIConstants.FONT_LABEL);
        tabela.setShowGrid(false);
        tabela.setFillsViewportHeight(true);
        painelTabela.add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.add(cabecalho, BorderLayout.NORTH);
        topo.add(linhaBotao, BorderLayout.SOUTH);

        add(topo, BorderLayout.NORTH);
        add(painelTabela, BorderLayout.CENTER);
    }

    private void abrirDialogoNovaCategoria() {
        JTextField campoNome = Componentes.campoTexto();
        JComboBox<TipoFluxo> comboTipo = new JComboBox<>(TipoFluxo.values());
        comboTipo.setFont(UIConstants.FONT_BODY);

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.add(Componentes.rotulo("NOME DA CATEGORIA"));
        painel.add(campoNome);
        painel.add(Box.createVerticalStrut(12));
        painel.add(Componentes.rotulo("TIPO"));
        painel.add(comboTipo);
        painel.setBorder(new EmptyBorder(8, 8, 8, 8));
        painel.setPreferredSize(new Dimension(320, 120));

        int opcao = JOptionPane.showConfirmDialog(this, painel, "Nova categoria",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opcao == JOptionPane.OK_OPTION) {
            String nome = campoNome.getText().trim();
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe um nome para a categoria.",
                        "Campo obrigatório", JOptionPane.WARNING_MESSAGE);
                return;
            }
            dados.cadastrarCategoria(nome, (TipoFluxo) comboTipo.getSelectedItem());
            atualizar();
        }
    }

    /** Chamado sempre que a tela é exibida. */
    public void atualizar() {
        modeloTabela.setRowCount(0);
        List<Categoria> categorias = dados.listarCategorias();
        for (Categoria c : categorias) {
            String tipo = c.getTipoFluxo() == TipoFluxo.ENTRADA ? "Entrada" : "Saída";
            modeloTabela.addRow(new Object[]{c.getNomeCategoria(), tipo});
        }
    }
}
