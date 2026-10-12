package zcashflow.ui;

import zcashflow.model.Categoria;
import zcashflow.model.Categoria.TipoFluxo;
import zcashflow.service.CategoriaService;
import zcashflow.service.Sessao;
import zcashflow.util.AppException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestão de categorias (Figura 5 da Etapa 2). Todos os usuários consultam;
 * somente administradores criam, editam e excluem.
 */
public class CategoriasPanel extends PainelRolavel {

    private static final long serialVersionUID = 1L;

    private final CategoriaService categoriaService = new CategoriaService();
    private final DefaultTableModel modeloTabela = Componentes.modeloSomenteLeitura("Nome", "Tipo");
    private final JTable tabela = Componentes.tabela(modeloTabela);
    private final JButton botaoNova = Componentes.botaoPrimario("+ Nova categoria");
    private final JButton botaoEditar = Componentes.botaoSecundario("Editar");
    private final JButton botaoExcluir = Componentes.botaoPerigo("Excluir");
    private final JLabel avisoPermissao = Componentes.subtitulo(" ");

    private List<Categoria> listaAtual = new ArrayList<>();

    public CategoriasPanel() {
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
        JLabel subtitulo = Componentes.subtitulo("Categorias de entrada e saída usadas nos lançamentos");
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        avisoPermissao.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecalho.add(titulo);
        cabecalho.add(subtitulo);
        cabecalho.add(avisoPermissao);

        botaoNova.addActionListener(e -> abrirDialogo(null));
        botaoEditar.addActionListener(e -> abrirDialogo(selecionada()));
        botaoExcluir.addActionListener(e -> excluirSelecionada());
        tabela.getSelectionModel().addListSelectionListener(e -> atualizarBotoes());

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        acoes.setOpaque(false);
        acoes.setBorder(new EmptyBorder(14, 0, 10, 0));
        acoes.add(botaoNova);
        acoes.add(botaoEditar);
        acoes.add(botaoExcluir);

        JPanel cartaoTabela = Componentes.cartao();
        cartaoTabela.setLayout(new BorderLayout());
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(null);
        cartaoTabela.add(scroll, BorderLayout.CENTER);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.add(cabecalho, BorderLayout.NORTH);
        topo.add(acoes, BorderLayout.SOUTH);

        add(topo, BorderLayout.NORTH);
        add(cartaoTabela, BorderLayout.CENTER);
    }

    private boolean ehAdmin() {
        return Sessao.getInstance().getUsuario() != null && Sessao.getInstance().getUsuario().isAdmin();
    }

    private Categoria selecionada() {
        int linha = tabela.getSelectedRow();
        return linha >= 0 && linha < listaAtual.size() ? listaAtual.get(linha) : null;
    }

    private void atualizarBotoes() {
        boolean admin = ehAdmin();
        boolean temSelecao = selecionada() != null;
        botaoNova.setEnabled(admin);
        botaoEditar.setEnabled(admin && temSelecao);
        botaoExcluir.setEnabled(admin && temSelecao);
        avisoPermissao.setText(admin ? " " : "Somente administradores podem criar, editar ou excluir categorias.");
    }

    private void abrirDialogo(Categoria existente) {
        boolean edicao = existente != null;
        JTextField campoNome = Componentes.campoTexto();
        JComboBox<TipoFluxo> comboTipo = new JComboBox<>(TipoFluxo.values());
        comboTipo.setFont(UIConstants.FONT_BODY);
        if (edicao) {
            campoNome.setText(existente.getNomeCategoria());
            comboTipo.setSelectedItem(existente.getTipoFluxo());
        }

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.add(Componentes.rotuloPara("NOME DA CATEGORIA", campoNome));
        painel.add(Box.createVerticalStrut(4));
        painel.add(campoNome);
        painel.add(Box.createVerticalStrut(12));
        painel.add(Componentes.rotuloPara("TIPO", comboTipo));
        painel.add(Box.createVerticalStrut(4));
        painel.add(comboTipo);
        painel.setBorder(new EmptyBorder(8, 8, 8, 8));
        painel.setPreferredSize(new Dimension(320, 130));

        // Repete o diálogo enquanto houver erro de validação, preservando o que foi digitado.
        while (true) {
            int opcao = JOptionPane.showConfirmDialog(this, painel,
                    edicao ? "Editar categoria" : "Nova categoria",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (opcao != JOptionPane.OK_OPTION) {
                return;
            }
            try {
                TipoFluxo tipo = (TipoFluxo) comboTipo.getSelectedItem();
                if (edicao) {
                    categoriaService.atualizar(existente.getIdCategoria(), campoNome.getText(), tipo);
                } else {
                    categoriaService.criar(campoNome.getText(), tipo);
                }
                atualizar();
                return;
            } catch (AppException e) {
                Mensagens.erro(this, e.getMessage());
            }
        }
    }

    private void excluirSelecionada() {
        Categoria c = selecionada();
        if (c == null) {
            return;
        }
        if (!Mensagens.confirmar(this, "Excluir a categoria \"" + c.getNomeCategoria() + "\"?", "Confirmar exclusão")) {
            return;
        }
        try {
            categoriaService.excluir(c.getIdCategoria());
        } catch (AppException e) {
            Mensagens.erro(this, e.getMessage());
        }
        atualizar();
    }

    /** Chamado sempre que a tela é exibida. */
    public void atualizar() {
        try {
            listaAtual = categoriaService.listar();
        } catch (AppException e) {
            Mensagens.erro(this, e.getMessage());
            return;
        }
        modeloTabela.setRowCount(0);
        for (Categoria c : listaAtual) {
            modeloTabela.addRow(new Object[]{c.getNomeCategoria(), c.getTipoFluxo().getRotulo()});
        }
        atualizarBotoes();
    }
}
