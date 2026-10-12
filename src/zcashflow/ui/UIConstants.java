package zcashflow.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * Paleta de cores e tipografia definidas no Projeto de Interfaces (Etapa 2),
 * centralizadas aqui para manter consistência visual entre todas as telas.
 * Todas as combinações de texto/fundo atendem ao contraste mínimo WCAG AA (4,5:1).
 */
public final class UIConstants {

    private UIConstants() {
    }

    public static final Color PRIMARY = new Color(0x1E, 0x4E, 0x8C);
    public static final Color PRIMARY_DARK = new Color(0x16, 0x3A, 0x69);
    public static final Color PRIMARY_LIGHT = new Color(0xE8, 0xEF, 0xF9);

    public static final Color SUCCESS = new Color(0x16, 0x7A, 0x4C);
    public static final Color DANGER = new Color(0xC1, 0x27, 0x2D);

    public static final Color BG = new Color(0xF4, 0xF6, 0xF9);
    public static final Color SURFACE = Color.WHITE;
    public static final Color BORDER = new Color(0xE1, 0xE5, 0xEA);

    public static final Color TEXT = new Color(0x1A, 0x1F, 0x29);
    public static final Color TEXT_MUTED = new Color(0x5B, 0x64, 0x72);

    public static final Font FONT_H1 = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_H2 = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BRAND = new Font("Segoe UI", Font.BOLD, 18);
}
