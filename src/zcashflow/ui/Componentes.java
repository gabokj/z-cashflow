package zcashflow.ui;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.Cursor;

/**
 * Fábrica de componentes visuais padronizados (botões, cartões, campos, tabelas),
 * para manter a mesma linguagem visual em todas as telas.
 */
public final class Componentes {

    private Componentes() {
    }

    public static JButton botaoPrimario(String texto) {
        JButton b = new JButton(texto);
        estilizarBotao(b, UIConstants.PRIMARY, java.awt.Color.WHITE, UIConstants.PRIMARY);
        return b;
    }

    public static JButton botaoSecundario(String texto) {
        JButton b = new JButton(texto);
        estilizarBotao(b, java.awt.Color.WHITE, UIConstants.TEXT, UIConstants.BORDER);
        return b;
    }

    public static JButton botaoPerigo(String texto) {
        JButton b = new JButton(texto);
        estilizarBotao(b, java.awt.Color.WHITE, UIConstants.DANGER, UIConstants.DANGER);
        return b;
    }

    private static void estilizarBotao(JButton b, java.awt.Color fundo, java.awt.Color texto, java.awt.Color borda) {
        b.setBackground(fundo);
        b.setForeground(texto);
        b.setFont(UIConstants.FONT_BODY_BOLD);
        b.setFocusPainted(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new CompoundBorder(new LineBorder(borda, 1, true), new EmptyBorder(10, 18, 10, 18)));
        b.setOpaque(true);
        b.getAccessibleContext().setAccessibleName(b.getText());
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

    /** Rótulo associado ao campo (leitores de tela e atalhos usam essa ligação). */
    public static JLabel rotuloPara(String texto, JComponent campo) {
        JLabel l = rotulo(texto);
        l.setLabelFor(campo);
        campo.getAccessibleContext().setAccessibleName(texto);
        return l;
    }

    public static JTextField campoTexto() {
        JTextField tf = new JTextField();
        tf.setFont(UIConstants.FONT_BODY);
        tf.setBorder(new CompoundBorder(new LineBorder(UIConstants.BORDER, 1, true), new EmptyBorder(8, 10, 8, 10)));
        return tf;
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

    /** Tabela somente leitura com o estilo padrão do sistema. */
    public static JTable tabela(DefaultTableModel modelo) {
        JTable t = new JTable(modelo);
        t.setRowHeight(30);
        t.setFont(UIConstants.FONT_BODY);
        t.getTableHeader().setFont(UIConstants.FONT_LABEL);
        t.getTableHeader().setReorderingAllowed(false);
        t.setShowGrid(false);
        t.setIntercellSpacing(new java.awt.Dimension(0, 0));
        t.setFillsViewportHeight(true);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setSelectionBackground(UIConstants.PRIMARY_LIGHT);
        t.setSelectionForeground(UIConstants.TEXT);
        return t;
    }

    public static DefaultTableModel modeloSomenteLeitura(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
}
