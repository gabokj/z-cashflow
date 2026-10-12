package zcashflow.ui;

import zcashflow.model.Usuario;
import zcashflow.service.DadosService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Tela de autenticação (wireframe "Figura 1" do Projeto de Interfaces).
 */
public class LoginFrame extends JFrame {

    private final JTextField campoEmail = Componentes.campoTexto();
    private final JPasswordField campoSenha = new JPasswordField();
    private final JLabel labelErro = new JLabel(" ");
    private final DadosService dados = DadosService.getInstance();

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

        // --- Lado esquerdo: marca ---
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

        // --- Lado direito: formulário ---
        JPanel ladoDireito = new JPanel(new GridBagLayout());
        ladoDireito.setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(0, 0, 0, 0));
        form.setMaximumSize(new Dimension(380, 420));
        form.setPreferredSize(new Dimension(380, 300));

        JLabel titulo = Componentes.titulo("Entrar");
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel rotuloEmail = Componentes.rotulo("E-MAIL");
        rotuloEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        campoEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        campoEmail.setMaximumSize(new Dimension(380, 38));
        campoEmail.setText("gabriel@zmail.com");

        JLabel rotuloSenha = Componentes.rotulo("SENHA");
        rotuloSenha.setAlignmentX(Component.LEFT_ALIGNMENT);
        campoSenha.setFont(UIConstants.FONT_BODY);
        campoSenha.setBorder(campoEmail.getBorder());
        campoSenha.setAlignmentX(Component.LEFT_ALIGNMENT);
        campoSenha.setMaximumSize(new Dimension(380, 38));
        campoSenha.setText("olaprofessor");

        labelErro.setForeground(UIConstants.DANGER);
        labelErro.setFont(UIConstants.FONT_BODY);
        labelErro.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton botaoEntrar = Componentes.botaoPrimario("ENTRAR");
        botaoEntrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        botaoEntrar.setMaximumSize(new Dimension(380, 42));
        botaoEntrar.addActionListener(this::tentarLogin);

        JLabel dica = new JLabel("<html>Use <b>gabriel@zmail.com</b> / <b>olaprofessor</b> " +
                "(dados de demonstração em memória)</html>");
        dica.setFont(UIConstants.FONT_BODY);
        dica.setForeground(UIConstants.TEXT_MUTED);
        dica.setAlignmentX(Component.LEFT_ALIGNMENT);

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
        form.add(Box.createVerticalStrut(20));
        form.add(dica);

        getRootPane().setDefaultButton(botaoEntrar);

        ladoDireito.add(form);

        raiz.add(ladoEsquerdo);
        raiz.add(ladoDireito);
        setContentPane(raiz);
    }

    private void tentarLogin(ActionEvent e) {
        String email = campoEmail.getText().trim();
        String senha = new String(campoSenha.getPassword());

        if (email.isEmpty() || senha.isEmpty()) {
            labelErro.setText("Preencha e-mail e senha.");
            return;
        }

        Usuario usuario = dados.autenticar(email, senha);
        if (usuario == null) {
            labelErro.setText("E-mail ou senha inválidos.");
            return;
        }

        labelErro.setText(" ");
        MainFrame principal = new MainFrame();
        principal.setVisible(true);
        this.dispose();
    }
}
