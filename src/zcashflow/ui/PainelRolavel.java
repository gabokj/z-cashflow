package zcashflow.ui;

import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * Painel de tela que acompanha a largura da janela dentro de um JScrollPane
 * (sem barra de rolagem horizontal) e rola na vertical só quando necessário.
 * Quando a janela é mais alta que o conteúdo, o painel preenche a altura toda.
 */
public class PainelRolavel extends JPanel implements Scrollable {

    private static final long serialVersionUID = 1L;

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 16;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return orientation == javax.swing.SwingConstants.VERTICAL ? visibleRect.height : visibleRect.width;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return getParent() instanceof JViewport && getParent().getHeight() > getPreferredSize().height;
    }
}
