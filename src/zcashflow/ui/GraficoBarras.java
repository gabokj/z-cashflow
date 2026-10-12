package zcashflow.ui;

import zcashflow.model.ResumoMensal;
import zcashflow.util.Formatador;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Gráfico de barras agrupadas (entradas x saídas por mês) desenhado com Java2D,
 * sem bibliotecas externas. As barras têm legenda textual e rótulos de valor,
 * então a leitura não depende só da cor.
 */
public class GraficoBarras extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter MES = DateTimeFormatter.ofPattern("MMM/yy", Formatador.PT_BR);

    private List<ResumoMensal> dados = new ArrayList<>();

    public GraficoBarras() {
        setOpaque(false);
        setPreferredSize(new Dimension(400, 230));
        getAccessibleContext().setAccessibleName("Gráfico de entradas e saídas dos últimos meses");
    }

    public void setDados(List<ResumoMensal> dados) {
        this.dados = new ArrayList<>(dados);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int largura = getWidth();
        int altura = getHeight();
        int esquerda = 62;
        int direita = 10;
        int topo = 30;
        int base = 30;
        int areaW = largura - esquerda - direita;
        int areaH = altura - topo - base;

        desenharLegenda(g2, esquerda, 6);

        BigDecimal maximo = BigDecimal.ZERO;
        for (ResumoMensal r : dados) {
            maximo = maximo.max(r.entradas()).max(r.saidas());
        }
        if (dados.isEmpty() || maximo.signum() == 0) {
            g2.setFont(UIConstants.FONT_BODY);
            g2.setColor(UIConstants.TEXT_MUTED);
            String msg = "Sem movimentações no período";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, (largura - fm.stringWidth(msg)) / 2, topo + areaH / 2);
            g2.dispose();
            return;
        }

        double max = escalaAgradavel(maximo.doubleValue());

        // Linhas de grade e escala vertical
        g2.setFont(UIConstants.FONT_SMALL);
        FontMetrics fm = g2.getFontMetrics();
        int divisoes = 4;
        for (int i = 0; i <= divisoes; i++) {
            int y = topo + areaH - (int) Math.round(areaH * i / (double) divisoes);
            g2.setColor(UIConstants.BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(esquerda, y, esquerda + areaW, y);
            String rotulo = Formatador.inteiro(BigDecimal.valueOf(max * i / divisoes));
            g2.setColor(UIConstants.TEXT_MUTED);
            g2.drawString(rotulo, esquerda - 8 - fm.stringWidth(rotulo), y + fm.getAscent() / 2 - 1);
        }

        // Barras
        int n = dados.size();
        double larguraGrupo = areaW / (double) n;
        int larguraBarra = (int) Math.max(8, Math.min(34, larguraGrupo * 0.32));
        for (int i = 0; i < n; i++) {
            ResumoMensal r = dados.get(i);
            double centro = esquerda + larguraGrupo * i + larguraGrupo / 2;

            desenharBarra(g2, (int) (centro - larguraBarra - 2), larguraBarra, topo, areaH, max,
                    r.entradas(), UIConstants.SUCCESS);
            desenharBarra(g2, (int) (centro + 2), larguraBarra, topo, areaH, max,
                    r.saidas(), UIConstants.DANGER);

            String mes = r.mes().format(MES);
            g2.setFont(UIConstants.FONT_SMALL);
            g2.setColor(UIConstants.TEXT);
            g2.drawString(mes, (int) (centro - fm.stringWidth(mes) / 2.0), topo + areaH + 18);
        }
        g2.dispose();
    }

    private void desenharBarra(Graphics2D g2, int x, int largura, int topo, int areaH, double max,
                               BigDecimal valor, Color cor) {
        if (valor.signum() == 0) {
            return;
        }
        int h = (int) Math.max(2, Math.round(areaH * valor.doubleValue() / max));
        int y = topo + areaH - h;
        g2.setColor(cor);
        g2.fillRoundRect(x, y, largura, h, 4, 4);

        g2.setFont(UIConstants.FONT_SMALL);
        FontMetrics fm = g2.getFontMetrics();
        String rotulo = Formatador.inteiro(valor);
        g2.setColor(UIConstants.TEXT);
        g2.drawString(rotulo, x + (largura - fm.stringWidth(rotulo)) / 2, y - 3);
    }

    private void desenharLegenda(Graphics2D g2, int x, int y) {
        g2.setFont(UIConstants.FONT_SMALL);
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(UIConstants.SUCCESS);
        g2.fillRoundRect(x, y + 2, 10, 10, 3, 3);
        g2.setColor(UIConstants.TEXT);
        g2.drawString("Entradas", x + 15, y + 11);
        int x2 = x + 15 + fm.stringWidth("Entradas") + 18;
        g2.setColor(UIConstants.DANGER);
        g2.fillRoundRect(x2, y + 2, 10, 10, 3, 3);
        g2.setColor(UIConstants.TEXT);
        g2.drawString("Saídas", x2 + 15, y + 11);
        g2.setColor(UIConstants.TEXT_MUTED);
        g2.drawString("valores em R$", x2 + 15 + fm.stringWidth("Saídas") + 18, y + 11);
    }

    /** Arredonda o máximo para cima, para uma escala vertical "redonda" (1, 2, 5 x 10^n). */
    private double escalaAgradavel(double valor) {
        double potencia = Math.pow(10, Math.floor(Math.log10(valor)));
        double fracao = valor / potencia;
        double nice = fracao <= 1 ? 1 : fracao <= 2 ? 2 : fracao <= 5 ? 5 : 10;
        return nice * potencia;
    }
}
