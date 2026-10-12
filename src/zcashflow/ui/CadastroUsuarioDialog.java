package zcashflow.ui;

import zcashflow.service.UsuarioService;
import zcashflow.util.AppException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

/**
 * Diálogo de criação de conta (botão "Criar conta" da tela de login).
 */
public class CadastroUsuarioDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final JTextField campoNome = Componentes.campoTexto();
    private final JTextField campoEmail = Componentes.campoTexto();
    private final JPasswordField campoSenha = new JPasswordField();
    private final JPasswordField campoConfirmacao = new JPasswordField();
    private final JLabel labelErro = new JLabel(" ");
    private final UsuarioService usuarioService = new UsuarioService();
    private String emailCriado;

    public CadastroUsuarioDialog(JFrame pai) {
        super(pai, "Criar conta", true);
        montar();
        pack();
        setResizable(false);
        setLocationRelativeTo(pai);
    }

    /** E-mail da conta criada, ou null se o usuário cancelou. */
    public String getEmailCriado() {
        return emailCriado;
    }

    private void montar() {
        JPanel conteudo = new JPanel();
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBackground(UIConstants.SURFACE);
        conteudo.setBorder(new EmptyBorder(24, 28, 20, 28));

        JLabel titulo = Componentes.titulo("Criar conta");
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        conteudo.add(titulo);
        conteudo.add(Box.createVerticalStrut(16));

        for (JPasswordField pf : new JPasswordField[]{campoSenha, campoConfirmacao}) {
            pf.setFont(UIConstants.FONT_BODY);
            pf.setBorder(campoNome.getBorder());
        }

        adicionarCampo(conteudo, "NOME COMPLETO", campoNome);
        adicionarCampo(conteudo, "E-MAIL", campoEmail);
        adicionarCampo(conteudo, "SENHA (mínimo de 6 caracteres)", campoSenha);
        adicionarCampo(conteudo, "CONFIRMAR SENHA", campoConfirmacao);

        labelErro.setForeground(UIConstants.DANGER);
        labelErro.setFont(UIConstants.FONT_BODY);
        labelErro.setAlignmentX(Component.LEFT_ALIGNMENT);
        conteudo.add(labelErro);
        conteudo.add(Box.createVerticalStrut(8));

        JButton criar = Componentes.botaoPrimario("Criar conta");
        criar.addActionListener(e -> criarConta());
        JButton cancelar = Componentes.botaoSecundario("Cancelar");
        cancelar.addActionListener(e -> dispose());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        botoes.setOpaque(false);
        botoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        botoes.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));
        botoes.add(criar);
        botoes.add(cancelar);
        conteudo.add(botoes);

        setContentPane(conteudo);
        getRootPane().setDefaultButton(criar);
    }

    private void adicionarCampo(JPanel painel, String rotulo, JComponent campo) {
        JLabel l = Componentes.rotuloPara(rotulo, campo);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setPreferredSize(new Dimension(340, 36));
        campo.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        painel.add(l);
        painel.add(Box.createVerticalStrut(4));
        painel.add(campo);
        painel.add(Box.createVerticalStrut(12));
    }

    private void criarConta() {
        try {
            usuarioService.registrar(campoNome.getText(), campoEmail.getText(),
                    new String(campoSenha.getPassword()), new String(campoConfirmacao.getPassword()));
        } catch (AppException ex) {
            labelErro.setText("<html><body style='width:320px'>⚠ " + ex.getMessage() + "</body></html>");
            return;
        }
        emailCriado = campoEmail.getText().trim().toLowerCase();
        Mensagens.info(this, "Conta criada com sucesso! Faça login com seu e-mail e senha.");
        dispose();
    }
}
