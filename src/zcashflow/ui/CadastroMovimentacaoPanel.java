package zcashflow.ui;

import zcashflow.model.Categoria;
import zcashflow.model.Categoria.TipoFluxo;
import zcashflow.model.Movimentacao;
import zcashflow.service.CategoriaService;
import zcashflow.service.MovimentacaoService;
import zcashflow.util.AppException;
import zcashflow.util.Formatador;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;

/**
 * Formulário de movimentação (RF001/RF002): serve tanto para cadastrar uma
 * nova entrada/saída quanto para editar uma existente (Figuras 3 e 8 da Etapa 2).
 */
public class CadastroMovimentacaoPanel extends PainelRolavel {

    private static final long serialVersionUID = 1L;

    private final MainFrame principal;
    private final CategoriaService categoriaService = new CategoriaService();
    private final MovimentacaoService movimentacaoService = new MovimentacaoService();

    private final JLabel labelTitulo = Componentes.titulo("Nova movimentação");
    private final JRadioButton radioEntrada = new JRadioButton("Entrada");
    private final JRadioButton radioSaida = new JRadioButton("Saída");
    private final JTextField campoValor = Componentes.campoTexto();
    private final JTextField campoData = Componentes.campoTexto();
    private final JComboBox<Categoria> comboCategoria = new JComboBox<>();
    private final JTextField campoDescricao = Componentes.campoTexto();
    private final JButton botaoSalvar = Componentes.botaoPrimario("Salvar");
    private final JLabel labelMensagem = new JLabel(" ");

    /** null = cadastrando uma nova movimentação; senão, editando esta. */
    private Movimentacao emEdicao;
    private boolean carregandoCombo;

    public CadastroMovimentacaoPanel(MainFrame principal) {
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
        labelTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitulo = Componentes.subtitulo("Registre uma entrada (receita) ou uma saída (despesa)");
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecalho.add(labelTitulo);
        cabecalho.add(subtitulo);

        JPanel corpo = new JPanel(new BorderLayout(20, 0));
        corpo.setOpaque(false);
        corpo.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel formulario = Componentes.cartao();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));

        JLabel rotuloTipo = Componentes.rotulo("TIPO DE MOVIMENTAÇÃO");
        rotuloTipo.setAlignmentX(Component.LEFT_ALIGNMENT);

        ButtonGroup grupoTipo = new ButtonGroup();
        grupoTipo.add(radioEntrada);
        grupoTipo.add(radioSaida);
        radioEntrada.setSelected(true);
        for (JRadioButton r : new JRadioButton[]{radioEntrada, radioSaida}) {
            r.setOpaque(false);
            r.setFont(UIConstants.FONT_BODY_BOLD);
            r.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            r.addActionListener(e -> carregarCategorias(null));
        }
        radioEntrada.setForeground(UIConstants.SUCCESS);
        radioSaida.setForeground(UIConstants.DANGER);

        JPanel painelTipo = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        painelTipo.setOpaque(false);
        painelTipo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelTipo.setMaximumSize(new Dimension(Short.MAX_VALUE, 32));
        painelTipo.add(radioEntrada);
        painelTipo.add(radioSaida);

        JPanel linhaValorData = new JPanel(new GridLayout(1, 2, 16, 0));
        linhaValorData.setOpaque(false);
        linhaValorData.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaValorData.setMaximumSize(new Dimension(Short.MAX_VALUE, 66));
        linhaValorData.add(campoComRotulo("VALOR (R$) — ex.: 1250,50", campoValor));
        linhaValorData.add(campoComRotulo("DATA (dd/mm/aaaa)", campoData));

        comboCategoria.setFont(UIConstants.FONT_BODY);

        botaoSalvar.addActionListener(e -> salvar());
        JButton cancelar = Componentes.botaoSecundario("Cancelar");
        cancelar.addActionListener(e -> voltar());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        botoes.setOpaque(false);
        botoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        botoes.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));
        botoes.add(botaoSalvar);
        botoes.add(cancelar);

        labelMensagem.setFont(UIConstants.FONT_BODY_BOLD);
        labelMensagem.setForeground(UIConstants.DANGER);
        labelMensagem.setAlignmentX(Component.LEFT_ALIGNMENT);

        formulario.add(rotuloTipo);
        formulario.add(Box.createVerticalStrut(8));
        formulario.add(painelTipo);
        formulario.add(Box.createVerticalStrut(18));
        formulario.add(linhaValorData);
        formulario.add(Box.createVerticalStrut(18));
        formulario.add(campoComRotulo("CATEGORIA", comboCategoria));
        formulario.add(Box.createVerticalStrut(18));
        formulario.add(campoComRotulo("DESCRIÇÃO", campoDescricao));
        formulario.add(Box.createVerticalStrut(24));
        formulario.add(botoes);
        formulario.add(Box.createVerticalStrut(12));
        formulario.add(labelMensagem);

        JPanel dicas = Componentes.cartao();
        dicas.setLayout(new BoxLayout(dicas, BoxLayout.Y_AXIS));
        JLabel tituloDicas = new JLabel("Boas práticas");
        tituloDicas.setFont(UIConstants.FONT_H2);
        tituloDicas.setAlignmentX(Component.LEFT_ALIGNMENT);
        dicas.add(tituloDicas);
        dicas.add(Box.createVerticalStrut(10));
        for (String texto : new String[]{
                "A categoria precisa ser compatível com o tipo escolhido (entrada ou saída).",
                "Informe sempre o valor positivo; o sistema aplica o sinal conforme o tipo.",
                "Datas usam o formato brasileiro dd/mm/aaaa.",
                "O saldo é recalculado automaticamente após salvar."}) {
            JLabel l = new JLabel("<html><body style='width: 200px'>• " + texto + "</body></html>");
            l.setFont(UIConstants.FONT_BODY);
            l.setForeground(UIConstants.TEXT_MUTED);
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            l.setBorder(new EmptyBorder(0, 0, 10, 0));
            dicas.add(l);
        }
        JPanel wrapDicas = new JPanel(new BorderLayout());
        wrapDicas.setOpaque(false);
        wrapDicas.add(dicas, BorderLayout.NORTH);

        corpo.add(formulario, BorderLayout.CENTER);
        corpo.add(wrapDicas, BorderLayout.EAST);
        add(cabecalho, BorderLayout.NORTH);
        add(corpo, BorderLayout.CENTER);
    }

    private JPanel campoComRotulo(String rotulo, JComponent campo) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(Componentes.rotuloPara(rotulo, campo), BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        // BorderLayout reivindica altura máxima infinita; limitamos para o BoxLayout não esticar o campo.
        p.setMaximumSize(new Dimension(Short.MAX_VALUE, 26 + Math.max(campo.getPreferredSize().height, 36)));
        return p;
    }

    /** Recarrega as categorias do tipo selecionado; mantém {@code selecionada} se informada. */
    private void carregarCategorias(Categoria selecionada) {
        TipoFluxo tipo = radioEntrada.isSelected() ? TipoFluxo.ENTRADA : TipoFluxo.SAIDA;
        carregandoCombo = true;
        try {
            comboCategoria.removeAllItems();
            List<Categoria> categorias = categoriaService.listarPorTipo(tipo);
            for (Categoria c : categorias) {
                comboCategoria.addItem(c);
            }
            if (selecionada != null) {
                comboCategoria.setSelectedItem(selecionada);
            }
        } catch (AppException e) {
            Mensagens.erro(this, e.getMessage());
        } finally {
            carregandoCombo = false;
        }
    }

    /** Abre o formulário vazio para uma nova movimentação. */
    public void prepararNovo() {
        emEdicao = null;
        labelTitulo.setText("Nova movimentação");
        botaoSalvar.setText("Salvar");
        limparCampos();
        carregarCategorias(null);
        campoValor.requestFocusInWindow();
    }

    /** Abre o formulário preenchido com os dados da movimentação a editar. */
    public void prepararEdicao(Movimentacao mov) {
        emEdicao = mov;
        labelTitulo.setText("Editar movimentação");
        botaoSalvar.setText("Salvar alterações");
        labelMensagem.setText(" ");
        radioEntrada.setSelected(mov.isEntrada());
        radioSaida.setSelected(!mov.isEntrada());
        carregarCategorias(mov.getCategoria());
        campoValor.setText(Formatador.valorEditavel(mov.getValor()));
        campoData.setText(Formatador.data(mov.getDataOperacao()));
        campoDescricao.setText(mov.getDescricao());
    }

    private void salvar() {
        if (carregandoCombo) {
            return;
        }
        try {
            var valor = Formatador.lerValor(campoValor.getText());
            LocalDate data = Formatador.lerData(campoData.getText());
            Categoria categoria = (Categoria) comboCategoria.getSelectedItem();

            if (emEdicao == null) {
                movimentacaoService.registrar(categoria, valor, data, campoDescricao.getText());
            } else {
                movimentacaoService.atualizar(emEdicao.getIdMovimentacao(), categoria, valor, data,
                        campoDescricao.getText());
            }
        } catch (AppException e) {
            labelMensagem.setText("⚠ " + e.getMessage());
            return;
        }
        boolean editando = emEdicao != null;
        limparCampos();
        emEdicao = null;
        principal.navegarPara(editando ? MainFrame.CARD_EXTRATO : MainFrame.CARD_DASHBOARD);
    }

    private void voltar() {
        boolean editando = emEdicao != null;
        limparCampos();
        emEdicao = null;
        principal.navegarPara(editando ? MainFrame.CARD_EXTRATO : MainFrame.CARD_DASHBOARD);
    }

    private void limparCampos() {
        labelMensagem.setText(" ");
        campoValor.setText("");
        campoDescricao.setText("");
        campoData.setText(Formatador.data(LocalDate.now()));
        radioEntrada.setSelected(true);
    }
}
