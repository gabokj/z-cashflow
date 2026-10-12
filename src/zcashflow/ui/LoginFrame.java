package zcashflow.ui;

import zcashflow.service.UsuarioService;
import zcashflow.util.AppException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

/**
 * Tela de autenticação (wireframe "Figura 1" do Projeto de Interfaces).
 */
public class LoginFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final JTextField campoEmail = Componentes.campoTexto();
    private final JPasswordField campoSenha = new JPasswordField();
    private final JLabel labelErro = new JLabel(" ");
    private final UsuarioService usuarioService = new UsuarioService();

    public LoginFrame() {
        super("Z-CashFlow — Login");
        montarTela();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 620);
        setMinimumSize(new Dimension(760, 520));
        setLocationRelativeTo(null);
    }

    private void montarTela() {
        JPanel raiz = new JPanel(new GridLayout(1, 2));

        // Lado esquerdo: identidade visual
        JPanel ladoEsquerdo = new JPanel(new GridBagLayout());
        ladoEsquerdo.setBackground(UIConstants.BG);
        JPanel marca = new JPanel();
        marca.setOpaque(false);
        marca.setLayout(new BoxLayout(marca, BoxLayout.Y_AXIS));
        JLabel nomeApp = new JLabel("Z-CashFlow");
        nomeApp.setFont(new Font("Segoe UI", Font.BOLD, 30));
        nomeApp.setForeground(UIConstants.PRIMARY);
        nomeApp.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel slogan = new JLabel("controle financeiro pessoal");
        slogan.setFont(UIConstants.FONT_BODY);
        slogan.setForeground(UIConstants.TEXT_MUTED);
        slogan.setAlignmentX(Component.CENTER_ALIGNMENT);
        marca.add(nomeApp);
        marca.add(Box.createVerticalStrut(8));
        marca.add(slogan);
        ladoEsquerdo.add(marca);

        // Lado direito: formulário
        JPanel ladoDireito = new JPanel(new GridBagLayout());
        ladoDireito.setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(380, 390));

        JLabel titulo = Componentes.titulo("Entrar");
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel rotuloEmail = Componentes.rotuloPara("E-MAIL", campoEmail);
        rotuloEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        campoEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        campoEmail.setMaximumSize(new Dimension(380, 38));

        JLabel rotuloSenha = Componentes.rotuloPara("SENHA", campoSenha);
        rotuloSenha.setAlignmentX(Component.LEFT_ALIGNMENT);
        campoSenha.setFont(UIConstants.FONT_BODY);
        campoSenha.setBorder(campoEmail.getBorder());
        campoSenha.setAlignmentX(Component.LEFT_ALIGNMENT);
        campoSenha.setMaximumSize(new Dimension(380, 38));

        labelErro.setForeground(UIConstants.DANGER);
        labelErro.setFont(UIConstants.FONT_BODY);
        labelErro.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton botaoEntrar = Componentes.botaoPrimario("ENTRAR");
        botaoEntrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        botaoEntrar.setMaximumSize(new Dimension(380, 42));
        botaoEntrar.addActionListener(this::tentarLogin);

        JLabel semConta = Componentes.subtitulo("Ainda não tem conta?");
        semConta.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton botaoCriarConta = Componentes.botaoSecundario("Criar conta");
        botaoCriarConta.setAlignmentX(Component.LEFT_ALIGNMENT);
        botaoCriarConta.setMaximumSize(new Dimension(380, 42));
        botaoCriarConta.addActionListener(e -> abrirCadastro());

        form.add(titulo);
        form.add(Box.createVerticalStrut(24));
        form.add(rotuloEmail);
        form.add(Box.createVerticalStrut(6));
        form.add(campoEmail);
        form.add(Box.createVerticalStrut(16));
        form.add(rotuloSenha);
        form.add(Box.createVerticalStrut(6));
        form.add(campoSenha);
        form.add(Box.createVerticalStrut(6));
        form.add(labelErro);
        form.add(Box.createVerticalStrut(10));
        form.add(botaoEntrar);
        form.add(Box.createVerticalStrut(24));
        form.add(semConta);
        form.add(Box.createVerticalStrut(8));
        form.add(botaoCriarConta);

        getRootPane().setDefaultButton(botaoEntrar); // Enter aciona o login

        ladoDireito.add(form);
        raiz.add(ladoEsquerdo);
        raiz.add(ladoDireito);
        setContentPane(raiz);
    }

    private void tentarLogin(ActionEvent e) {
        try {
            usuarioService.autenticar(campoEmail.getText(), new String(campoSenha.getPassword()));
        } catch (AppException ex) {
            campoSenha.setText("");
            exibirErro(ex.getMessage());
            return;
        }
        new MainFrame().setVisible(true);
        dispose();
    }

    private void abrirCadastro() {
        CadastroUsuarioDialog dialogo = new CadastroUsuarioDialog(this);
        dialogo.setVisible(true);
        String emailCriado = dialogo.getEmailCriado();
        if (emailCriado != null) {
            campoEmail.setText(emailCriado);
            campoSenha.setText("");
            labelErro.setText(" ");
            campoSenha.requestFocusInWindow();
        }
    }

    private void exibirErro(String mensagem) {
        labelErro.setText("<html><body style='width:340px'>" + mensagem + "</body></html>");
    }
}
