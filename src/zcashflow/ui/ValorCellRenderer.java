package zcashflow.ui;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

/**
 * Renderizador da coluna "Valor": verde para entradas ("+ R$ ...") e vermelho para
 * saídas ("- R$ ..."). O sinal permanece no texto, então a informação não depende
 * apenas da cor (acessibilidade para daltonismo).
 */
public class ValorCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean selecionado,
                                                   boolean foco, int linha, int coluna) {
        JLabel l = (JLabel) super.getTableCellRendererComponent(tabela, valor, selecionado, foco, linha, coluna);
        String texto = valor == null ? "" : valor.toString();
        Color cor = UIConstants.TEXT;
        if (texto.startsWith("+")) {
            cor = UIConstants.SUCCESS;
        } else if (texto.startsWith("-")) {
            cor = UIConstants.DANGER;
        }
        l.setForeground(cor);
        l.setFont(UIConstants.FONT_BODY_BOLD.deriveFont(Font.BOLD));
        l.setHorizontalAlignment(SwingConstants.RIGHT);
        return l;
    }
}
