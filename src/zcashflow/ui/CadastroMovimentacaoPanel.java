package zcashflow.ui;

import zcashflow.model.Categoria;
import zcashflow.model.Categoria.TipoFluxo;
import zcashflow.model.Usuario;
import zcashflow.service.DadosService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Tela de cadastro de movimentação (RF001), reproduzindo a Figura 3/8 do
 * Projeto de Interfaces: tipo (entrada/saída), valor, data, categoria e
 * descrição, com painel lateral de orientações.
 */
public class CadastroMovimentacaoPanel extends JPanel {

    private static final DateTimeFormatter FORMATO_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final MainFrame principal;
    private final DadosService dados = DadosService.getInstance();

    private final JRadioButton radioEntrada = new JRadioButton("Entrada");
    private final JRadioButton radioSaida = new JRadioButton("Saída");
    private final JTextField campoValor = Componentes.campoTexto();
    private final JTextField campoData = Componentes.campoTexto();
    private final JComboBox<Categoria> comboCategoria = new JComboBox<>();
    private final JTextField campoDescricao = Componentes.campoTexto();
    private final JLabel labelMensagem = new JLabel(" ");

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
        JLabel titulo = Componentes.titulo("Nova movimentação");
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitulo = Componentes.subtitulo("RF001 — cadastro de entradas e saídas");
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecalho.add(titulo);
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
        radioEntrada.setOpaque(false);
        radioSaida.setOpaque(false);
        radioEntrada.setFont(UIConstants.FONT_BODY_BOLD);
        radioSaida.setFont(UIConstants.FONT_BODY_BOLD);
        radioEntrada.setForeground(UIConstants.SUCCESS);
        radioSaida.setForeground(UIConstants.DANGER);
        radioEntrada.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        radioSaida.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        radioEntrada.addActionListener(e -> atualizarCategoriasDisponiveis());
        radioSaida.addActionListener(e -> atualizarCategoriasDisponiveis());

        JPanel painelTipo = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        painelTipo.setOpaque(false);
        painelTipo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelTipo.setMaximumSize(new Dimension(Short.MAX_VALUE, 32));
        painelTipo.add(radioEntrada);
        painelTipo.add(radioSaida);

        JPanel linhaValorData = new JPanel(new GridLayout(1, 2, 16, 0));
        linhaValorData.setOpaque(false);
        linhaValorData.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaValorData.setMaximumSize(new Dimension(640, 60));
        linhaValorData.add(campoComRotulo("VALOR (R$)", campoValor));
        campoData.setText(LocalDate.now().format(FORMATO_BR));
        linhaValorData.add(campoComRotulo("DATA (dd/mm/aaaa)", campoData));

        comboCategoria.setFont(UIConstants.FONT_BODY);

        JButton salvar = Componentes.botaoPrimario("Salvar");
        salvar.addActionListener(e -> salvarMovimentacao());
        JButton cancelar = Componentes.botaoSecundario("Cancelar");
        cancelar.addActionListener(e -> {
            limparFormulario();
            principal.navegarPara(MainFrame.CARD_DASHBOARD);
        });

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        botoes.setOpaque(false);
        botoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        botoes.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));
        botoes.add(salvar);
        botoes.add(cancelar);

        labelMensagem.setFont(UIConstants.FONT_BODY_BOLD);
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
                "A categoria escolhida deve ser compatível com o tipo selecionado (RF002).",
                "Datas são sempre exibidas no formato brasileiro dd/mm/aaaa (RF003).",
                "O saldo é recalculado automaticamente após salvar (RF004)."}) {
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
        p.add(Componentes.rotulo(rotulo), BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        // Evita que o BoxLayout (eixo Y) estique este campo verticalmente:
        // BorderLayout reivindica altura máxima infinita por padrão.
        int alturaCampo = campo.getPreferredSize().height;
        p.setMaximumSize(new Dimension(Short.MAX_VALUE, 20 + 6 + Math.max(alturaCampo, 36)));
        return p;
    }

    private void atualizarCategoriasDisponiveis() {
        TipoFluxo tipo = radioEntrada.isSelected() ? TipoFluxo.ENTRADA : TipoFluxo.SAIDA;
        comboCategoria.removeAllItems();
        List<Categoria> categorias = dados.listarCategoriasPorTipo(tipo);
        for (Categoria c : categorias) {
            comboCategoria.addItem(c);
        }
    }

    private void salvarMovimentacao() {
        Usuario usuario = dados.getUsuarioLogado();
        if (usuario == null) {
            return;
        }

        String valorTexto = campoValor.getText().trim().replace(",", ".");
        double valor;
        try {
            valor = Double.parseDouble(valorTexto);
            if (valor <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            exibirErro("Informe um valor numérico maior que zero.");
            return;
        }

        LocalDate data;
        try {
            data = LocalDate.parse(campoData.getText().trim(), FORMATO_BR);
        } catch (DateTimeParseException ex) {
            exibirErro("Data inválida. Use o formato dd/mm/aaaa.");
            return;
        }

        Categoria categoria = (Categoria) comboCategoria.getSelectedItem();
        if (categoria == null) {
            exibirErro("Selecione uma categoria.");
            return;
        }

        String descricao = campoDescricao.getText().trim();
        if (descricao.isEmpty()) {
            exibirErro("Informe uma descrição.");
            return;
        }

        dados.registrarMovimentacao(usuario, categoria, valor, data, descricao);

        labelMensagem.setForeground(UIConstants.SUCCESS);
        labelMensagem.setText("✓ Movimentação cadastrada com sucesso!");
        limparFormulario();
        principal.navegarPara(MainFrame.CARD_DASHBOARD);
    }

    private void exibirErro(String mensagem) {
        labelMensagem.setForeground(UIConstants.DANGER);
        labelMensagem.setText("⚠ " + mensagem);
    }

    private void limparFormulario() {
        campoValor.setText("");
        campoDescricao.setText("");
        campoData.setText(LocalDate.now().format(FORMATO_BR));
        radioEntrada.setSelected(true);
        atualizarCategoriasDisponiveis();
    }

    /** Chamado sempre que a tela é exibida, para garantir categorias atualizadas. */
    public void atualizar() {
        labelMensagem.setText(" ");
        atualizarCategoriasDisponiveis();
    }
}
