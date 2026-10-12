package zcashflow.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Fábrica de componentes visuais padronizados (botões, cartões, campos),
 * para manter a mesma linguagem visual em todas as telas do sistema.
 */
public final class Componentes {

    private Componentes() {
    }

    public static JButton botaoPrimario(String texto) {
        JButton b = new JButton(texto);
        estilizarBotao(b, UIConstants.PRIMARY, Color.WHITE, UIConstants.PRIMARY);
        return b;
    }

    public static JButton botaoSecundario(String texto) {
        JButton b = new JButton(texto);
        estilizarBotao(b, Color.WHITE, UIConstants.TEXT, UIConstants.BORDER);
        return b;
    }

    public static JButton botaoPerigo(String texto) {
        JButton b = new JButton(texto);
        estilizarBotao(b, Color.WHITE, UIConstants.DANGER, UIConstants.DANGER);
        return b;
    }

    private static void estilizarBotao(JButton b, Color fundo, Color texto, Color borda) {
        b.setBackground(fundo);
        b.setForeground(texto);
        b.setFont(UIConstants.FONT_BODY_BOLD);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new CompoundBorder(new LineBorder(borda, 1, true), new EmptyBorder(10, 18, 10, 18)));
        b.setOpaque(true);
    }

    public static JPanel cartao() {
        JPanel p = new JPanel();
        p.setBackground(UIConstants.SURFACE);
        p.setBorder(new CompoundBorder(new LineBorder(UIConstants.BORDER, 1, true), new EmptyBorder(18, 20, 18, 20)));
        return p;
    }

    public static JLabel rotulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(UIConstants.FONT_LABEL);
        l.setForeground(UIConstants.TEXT_MUTED);
        return l;
    }

    public static JTextField campoTexto() {
        JTextField tf = new JTextField();
        tf.setFont(UIConstants.FONT_BODY);
        tf.setBorder(new CompoundBorder(new LineBorder(UIConstants.BORDER, 1, true), new EmptyBorder(8, 10, 8, 10)));
        return tf;
    }

    public static Border bordaCartao() {
        return new CompoundBorder(new LineBorder(UIConstants.BORDER, 1, true), new EmptyBorder(16, 18, 16, 18));
    }

    public static JLabel titulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(UIConstants.FONT_H1);
        l.setForeground(UIConstants.TEXT);
        return l;
    }

    public static JLabel subtitulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(UIConstants.FONT_BODY);
        l.setForeground(UIConstants.TEXT_MUTED);
        return l;
    }
}
