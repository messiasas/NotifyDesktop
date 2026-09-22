package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Paleta de cores, fontes e componentes reutilizados pelas telas do Notify,
 * para manter uma aparência consistente e mais amigável em toda a aplicação.
 */
public final class UiTheme {

    public static final Color BACKGROUND = new Color(0xF4F6FB);
    public static final Color SURFACE = new Color(0xFFFFFF);
    public static final Color BORDER = new Color(0xE2E5EE);
    public static final Color ROW_ALTERNATE = new Color(0xF7F9FC);
    public static final Color SELECTION = new Color(0xE4EAFF);

    public static final Color ACCENT = new Color(0x4A6CF7);
    public static final Color ACCENT_HOVER = new Color(0x3D5BE0);
    public static final Color ACCENT_SOFT = new Color(0xE7ECFF);
    public static final Color ACCENT_SOFT_HOVER = new Color(0xD7DFFF);
    public static final Color DANGER = new Color(0xE5484D);
    public static final Color DANGER_HOVER = new Color(0xC93E42);
    public static final Color NEUTRAL = new Color(0xEDEFF5);
    public static final Color NEUTRAL_HOVER = new Color(0xE0E3EC);

    public static final Color TEXT_PRIMARY = new Color(0x1F2430);
    public static final Color TEXT_SECONDARY = new Color(0x6B7280);
    public static final Color TEXT_ON_DARK = Color.WHITE;

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BASE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 12);

    private UiTheme() {
    }

    /** Botão plano, com cantos arredondados e destaque ao passar o mouse. */
    public static JButton criarBotao(String texto, Color corBase, Color corHover, Color corTexto) {
        JButton botao = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ButtonModel modelo = getModel();
                Color fundo = modelo.isRollover() || modelo.isPressed() ? corHover : corBase;
                g2.setColor(fundo);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                // sem borda; o próprio fundo arredondado já delimita o botão
            }
        };
        botao.setFont(FONT_BUTTON);
        botao.setForeground(corTexto);
        botao.setBackground(corBase);
        botao.setFocusPainted(false);
        botao.setContentAreaFilled(false);
        botao.setOpaque(false);
        botao.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setMargin(new Insets(0, 0, 0, 0));
        Dimension preferido = botao.getPreferredSize();
        botao.setPreferredSize(new Dimension(Math.max(preferido.width, 120), preferido.height));
        return botao;
    }

    public static JButton criarBotaoPrimario(String texto) {
        return criarBotao(texto, ACCENT, ACCENT_HOVER, TEXT_ON_DARK);
    }

    public static JButton criarBotaoPerigo(String texto) {
        return criarBotao(texto, DANGER, DANGER_HOVER, TEXT_ON_DARK);
    }

    public static JButton criarBotaoNeutro(String texto) {
        return criarBotao(texto, NEUTRAL, NEUTRAL_HOVER, TEXT_PRIMARY);
    }

    /**
     * Botão de destaque suave, usado para chamar atenção para uma configuração importante.
     * Sem largura fixa: o texto muda em tempo de execução (ex.: intervalo de notificação),
     * então o tamanho precisa se recalcular sozinho a cada setText().
     */
    public static JButton criarBotaoDestaque(String texto) {
        JButton botao = criarBotao(texto, ACCENT_SOFT, ACCENT_SOFT_HOVER, ACCENT);
        botao.setFont(FONT_BUTTON.deriveFont(14f));
        botao.setBorder(BorderFactory.createEmptyBorder(12, 28, 12, 28));
        botao.setPreferredSize(null);
        return botao;
    }

    /** Painel branco com cantos levemente arredondados, usado como "cartão". */
    public static JPanel criarCartao(LayoutManager layout) {
        JPanel painel = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SURFACE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 14, 14));
                g2.setColor(BORDER);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 14, 14));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        painel.setOpaque(false);
        return painel;
    }
}
