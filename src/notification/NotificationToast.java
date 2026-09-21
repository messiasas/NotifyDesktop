package notification;

import model.Word;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class NotificationToast extends JWindow {

    private static final int LARGURA = 360;
    private static final int ALTURA = 120;
    private static final int MARGEM = 20;
    private static final int TEMPO_EXIBICAO_MS = 3500;
    private static final int PASSO_FADE_MS = 30;
    private static final float FADE_INCREMENTO = 0.08f;

    private final List<Word> palavras;
    private int indiceAtual;

    private final JLabel labelTermo;
    private final JLabel labelTraducao;
    private boolean traducaoRevelada = false;
    private Timer timerFechamento;

    public NotificationToast(List<Word> palavras, int indiceInicial) {
        this.palavras = palavras;
        this.indiceAtual = indiceInicial;

        setLayout(new BorderLayout());
        setSize(LARGURA, ALTURA);
        setAlwaysOnTop(true);
        setOpacity(0f);

        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(new Color(40, 40, 40));

        JLabel setaEsquerda = criarSeta("\u2039", this::irParaAnterior); // ‹
        JLabel setaDireita = criarSeta("\u203A", this::irParaProxima);   // ›

        JPanel painelConteudo = new JPanel(new BorderLayout());
        painelConteudo.setOpaque(false);
        painelConteudo.setBorder(BorderFactory.createEmptyBorder(16, 8, 16, 8));

        labelTermo = new JLabel();
        labelTermo.setFont(new Font("SansSerif", Font.BOLD, 20));
        labelTermo.setForeground(Color.WHITE);

        labelTraducao = new JLabel();
        labelTraducao.setFont(new Font("SansSerif", Font.PLAIN, 14));
        labelTraducao.setForeground(new Color(180, 180, 180));
        labelTraducao.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        painelConteudo.add(labelTermo, BorderLayout.NORTH);
        painelConteudo.add(labelTraducao, BorderLayout.CENTER);

        MouseAdapter cliqueCorpo = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!traducaoRevelada) {
                    revelarTraducao();
                } else {
                    fecharComFade();
                }
            }
        };
        painelConteudo.addMouseListener(cliqueCorpo);
        labelTermo.addMouseListener(cliqueCorpo);
        labelTraducao.addMouseListener(cliqueCorpo);

        painelPrincipal.add(setaEsquerda, BorderLayout.WEST);
        painelPrincipal.add(painelConteudo, BorderLayout.CENTER);
        painelPrincipal.add(setaDireita, BorderLayout.EAST);

        // Botão de fechar: posicionado em camada separada, sobreposto no canto superior direito
        JLabel botaoFechar = new JLabel("\u00D7", SwingConstants.CENTER); // ×
        botaoFechar.setFont(new Font("SansSerif", Font.BOLD, 16));
        botaoFechar.setForeground(new Color(150, 150, 150));
        botaoFechar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botaoFechar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fecharComFade();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                botaoFechar.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                botaoFechar.setForeground(new Color(150, 150, 150));
            }
        });

        JLayeredPane camada = new JLayeredPane();
        painelPrincipal.setBounds(0, 0, LARGURA, ALTURA);
        botaoFechar.setBounds(LARGURA - 22, 2, 18, 18); // canto superior direito, de verdade

        camada.add(painelPrincipal, JLayeredPane.DEFAULT_LAYER);
        camada.add(botaoFechar, JLayeredPane.PALETTE_LAYER); // fica por cima de tudo

        setContentPane(camada);

        atualizarLabels();
        posicionarNoCanto();
    }

    private JLabel criarSeta(String simbolo, Runnable aoClicar) {
        JLabel seta = new JLabel(simbolo, SwingConstants.CENTER);
        seta.setFont(new Font("SansSerif", Font.BOLD, 20));
        seta.setForeground(new Color(150, 150, 150));
        seta.setOpaque(true);
        seta.setBackground(new Color(40, 40, 40));
        seta.setVerticalAlignment(SwingConstants.TOP);
        seta.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        seta.setPreferredSize(new Dimension(28, ALTURA));
        seta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        seta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                aoClicar.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                seta.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                seta.setForeground(new Color(150, 150, 150));
            }
        });

        return seta;
    }

    private void irParaAnterior() {
        indiceAtual = (indiceAtual - 1 + palavras.size()) % palavras.size();
        atualizarLabels();
        reiniciarFechamentoAutomatico();
    }

    private void irParaProxima() {
        indiceAtual = (indiceAtual + 1) % palavras.size();
        atualizarLabels();
        reiniciarFechamentoAutomatico();
    }

    private void atualizarLabels() {
        Word palavra = palavras.get(indiceAtual);
        labelTermo.setText(palavra.getTermo());
        labelTraducao.setText("Clique para revelar a tradução");
        labelTraducao.setForeground(new Color(180, 180, 180));
        traducaoRevelada = false;
    }

    private void revelarTraducao() {
        Word palavra = palavras.get(indiceAtual);
        labelTraducao.setText(palavra.getTraducao());
        labelTraducao.setForeground(Color.WHITE);
        traducaoRevelada = true;
    }

    private void posicionarNoCanto() {
        Dimension tela = Toolkit.getDefaultToolkit().getScreenSize();
        int x = tela.width - LARGURA - MARGEM;
        int y = tela.height - ALTURA - MARGEM;
        setLocation(x, y);
    }

    public void exibir() {
        setVisible(true);
        fadeIn();
    }

    private void fadeIn() {
        Timer timer = new Timer(PASSO_FADE_MS, null);
        timer.addActionListener(e -> {
            float nova = getOpacity() + FADE_INCREMENTO;
            if (nova >= 1f) {
                setOpacity(1f);
                timer.stop();
                reiniciarFechamentoAutomatico();
            } else {
                setOpacity(nova);
            }
        });
        timer.start();
    }

    private void reiniciarFechamentoAutomatico() {
        if (timerFechamento != null) {
            timerFechamento.stop();
        }
        timerFechamento = new Timer(TEMPO_EXIBICAO_MS, e -> fecharComFade());
        timerFechamento.setRepeats(false);
        timerFechamento.start();
    }

    private void fecharComFade() {
        if (timerFechamento != null) {
            timerFechamento.stop();
        }
        Timer timer = new Timer(PASSO_FADE_MS, null);
        timer.addActionListener(e -> {
            float nova = getOpacity() - FADE_INCREMENTO;
            if (nova <= 0f) {
                setOpacity(0f);
                timer.stop();
                dispose();
            } else {
                setOpacity(nova);
            }
        });
        timer.start();
    }
}