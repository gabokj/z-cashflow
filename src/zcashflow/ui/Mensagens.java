package zcashflow.ui;

import javax.swing.JOptionPane;
import java.awt.Component;

/** Caixas de diálogo padronizadas. */
public final class Mensagens {

    private Mensagens() {
    }

    public static void erro(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    public static void info(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Z-CashFlow", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirmar(Component pai, String mensagem, String titulo) {
        Object[] opcoes = {"Sim", "Não"};
        int r = JOptionPane.showOptionDialog(pai, mensagem, titulo, JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[1]);
        return r == 0;
    }
}
