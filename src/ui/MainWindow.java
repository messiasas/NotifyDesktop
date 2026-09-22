package ui;

import model.Word;
import persistence.SettingsRepository;
import persistence.WordRepository;
import scheduler.NotificationScheduler;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class MainWindow extends JFrame {

    private static final String CARD_TABELA = "tabela";
    private static final String CARD_SEM_PALAVRAS = "semPalavras";

    private final WordRepository wordRepository;
    private final SettingsRepository settingsRepository;
    private final NotificationScheduler scheduler;
    private final WordTableModel tableModel;
    private final JTable tabela;
    private final JLabel statusLabel;
    private final JPanel painelConteudo;
    private final CardLayout cardLayout;
    private JButton botaoNotificacoes;

    public MainWindow(WordRepository wordRepository, SettingsRepository settingsRepository, NotificationScheduler scheduler) {
        super("Notify");
        this.wordRepository = wordRepository;
        this.settingsRepository = settingsRepository;
        this.scheduler = scheduler;

        List<Word> palavras = wordRepository.carregar();
        tableModel = new WordTableModel(palavras);

        tabela = new JTable(tableModel);
        configurarTabela();

        tableModel.addTableModelListener(e -> {
            atualizarEstadoConteudo();
            atualizarStatus();
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UiTheme.SURFACE);

        cardLayout = new CardLayout();
        painelConteudo = UiTheme.criarCartao(cardLayout);
        painelConteudo.add(scroll, CARD_TABELA);
        painelConteudo.add(criarEstadoVazio("Nenhuma palavra cadastrada",
                "Clique em \"+ Adicionar\" para começar a montar sua lista de estudos."), CARD_SEM_PALAVRAS);

        statusLabel = new JLabel();
        statusLabel.setFont(UiTheme.FONT_SUBTITLE);
        statusLabel.setForeground(UiTheme.TEXT_SECONDARY);

        JPanel raiz = new JPanel(new BorderLayout(0, 16));
        raiz.setBackground(UiTheme.BACKGROUND);
        raiz.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        raiz.add(criarCabecalho(), BorderLayout.NORTH);
        raiz.add(painelConteudo, BorderLayout.CENTER);
        raiz.add(criarRodape(), BorderLayout.SOUTH);

        setContentPane(raiz);

        atualizarStatus();
        atualizarEstadoConteudo();

        setMinimumSize(new Dimension(760, 460));
        setSize(940, 560);
        setLocationRelativeTo(null);

        // Fechar pelo X só esconde a janela — o app continua rodando na bandeja
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });
    }

    private JPanel criarCabecalho() {
        JLabel titulo = new JLabel("Notify");
        titulo.setFont(UiTheme.FONT_TITLE);
        titulo.setForeground(UiTheme.TEXT_PRIMARY);

        JLabel subtitulo = new JLabel("Enter new words");
        subtitulo.setFont(UiTheme.FONT_SUBTITLE);
        subtitulo.setForeground(UiTheme.TEXT_SECONDARY);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(subtitulo);

        botaoNotificacoes = UiTheme.criarBotaoDestaque(textoIntervaloNotificacoes());
        botaoNotificacoes.setToolTipText("Clique para alterar o intervalo entre as notificações");
        botaoNotificacoes.addActionListener(e -> abrirConfiguracoes());

        JPanel cabecalho = new JPanel(new BorderLayout(24, 0));
        cabecalho.setOpaque(false);
        cabecalho.add(textos, BorderLayout.CENTER);
        cabecalho.add(botaoNotificacoes, BorderLayout.EAST);
        return cabecalho;
    }

    private String textoIntervaloNotificacoes() {
        long minutos = settingsRepository.carregar().getIntervaloNotificacoes().toMinutes();
        return "🔔 Notificações a cada " + minutos + " min · Alterar";
    }

    private void abrirConfiguracoes() {
        SettingsDialog dialog = new SettingsDialog(this, settingsRepository);
        dialog.setVisible(true);
        if (dialog.foiAlterado()) {
            scheduler.aplicarConfiguracoesAtualizadas();
            botaoNotificacoes.setText(textoIntervaloNotificacoes());
        }
    }

    private JPanel criarEstadoVazio(String titulo, String descricao) {
        JLabel tituloLabel = new JLabel(titulo, SwingConstants.CENTER);
        tituloLabel.setFont(UiTheme.FONT_BOLD.deriveFont(16f));
        tituloLabel.setForeground(UiTheme.TEXT_PRIMARY);
        tituloLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descricaoLabel = new JLabel(descricao, SwingConstants.CENTER);
        descricaoLabel.setFont(UiTheme.FONT_SUBTITLE);
        descricaoLabel.setForeground(UiTheme.TEXT_SECONDARY);
        descricaoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel painel = new JPanel();
        painel.setOpaque(false);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.add(Box.createVerticalGlue());
        painel.add(tituloLabel);
        painel.add(Box.createVerticalStrut(6));
        painel.add(descricaoLabel);
        painel.add(Box.createVerticalGlue());
        return painel;
    }

    private JPanel criarRodape() {
        JButton botaoAdicionar = UiTheme.criarBotaoPrimario("+ Adicionar");
        JButton botaoEditar = UiTheme.criarBotaoNeutro("Editar");
        JButton botaoRemover = UiTheme.criarBotaoNeutro("Remover");
        JButton botaoLimpar = UiTheme.criarBotaoPerigo("Limpar tudo");

        botaoAdicionar.addActionListener(e -> adicionar());
        botaoEditar.addActionListener(e -> editar());
        botaoRemover.addActionListener(e -> remover());
        botaoLimpar.addActionListener(e -> limpar());

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painelBotoes.setOpaque(false);
        painelBotoes.add(botaoEditar);
        painelBotoes.add(botaoRemover);
        painelBotoes.add(botaoLimpar);
        painelBotoes.add(botaoAdicionar);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);
        rodape.add(statusLabel, BorderLayout.WEST);
        rodape.add(painelBotoes, BorderLayout.EAST);
        return rodape;
    }

    private void configurarTabela() {
        tabela.setRowHeight(32);
        tabela.setFont(UiTheme.FONT_BASE);
        tabela.setForeground(UiTheme.TEXT_PRIMARY);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setSelectionBackground(UiTheme.SELECTION);
        tabela.setSelectionForeground(UiTheme.TEXT_PRIMARY);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setFillsViewportHeight(true);
        tabela.setRowMargin(0);

        JTableHeader cabecalho = tabela.getTableHeader();
        cabecalho.setFont(UiTheme.FONT_TABLE_HEADER);
        cabecalho.setForeground(UiTheme.TEXT_SECONDARY);
        cabecalho.setBackground(UiTheme.SURFACE);
        cabecalho.setPreferredSize(new Dimension(0, 38));
        cabecalho.setReorderingAllowed(false);
        cabecalho.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UiTheme.BORDER));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                             boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? UiTheme.SURFACE : UiTheme.ROW_ALTERNATE);
                }
                setToolTipText(value == null ? null : value.toString());
                return c;
            }
        };
        for (int coluna = 0; coluna < tabela.getColumnCount(); coluna++) {
            tabela.getColumnModel().getColumn(coluna).setCellRenderer(renderer);
        }
        tabela.getColumnModel().getColumn(0).setPreferredWidth(160);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(160);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(280);

        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabela.getSelectedRow() != -1) {
                    editar();
                }
            }
        });
        tabela.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    remover();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    editar();
                }
            }
        });
    }

    private void atualizarEstadoConteudo() {
        if (tableModel.getRowCount() == 0) {
            cardLayout.show(painelConteudo, CARD_SEM_PALAVRAS);
        } else {
            cardLayout.show(painelConteudo, CARD_TABELA);
        }
    }

    private void atualizarStatus() {
        int total = tableModel.getRowCount();
        statusLabel.setText(total == 1 ? "1 palavra cadastrada" : total + " palavras cadastradas");
    }

    private void adicionar() {
        WordFormDialog dialog = new WordFormDialog(this, null);
        dialog.setVisible(true);

        Word novaPalavra = dialog.getResultado();
        if (novaPalavra != null) {
            tableModel.adicionar(novaPalavra);
            salvar();
        }
    }

    private void limpar() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "A lista já está vazia.", "Nada a limpar", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Remover TODAS as " + tableModel.getRowCount() + " palavras da lista? Essa ação não pode ser desfeita.",
                "Confirmar limpeza",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacao == JOptionPane.YES_OPTION) {
            tableModel.limparTudo();
            salvar();
        }
    }

    private void editar() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma palavra para editar.", "Nenhuma seleção", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int linha = tabela.convertRowIndexToModel(linhaSelecionada);

        Word palavraAtual = tableModel.getWordAt(linha);
        WordFormDialog dialog = new WordFormDialog(this, palavraAtual);
        dialog.setVisible(true);

        Word palavraEditada = dialog.getResultado();
        if (palavraEditada != null) {
            tableModel.atualizar(linha, palavraEditada);
            salvar();
        }
    }

    private void remover() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma palavra para remover.", "Nenhuma seleção", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int linha = tabela.convertRowIndexToModel(linhaSelecionada);

        int confirmacao = JOptionPane.showConfirmDialog(this, "Remover esta palavra?", "Confirmar remoção", JOptionPane.YES_NO_OPTION);
        if (confirmacao == JOptionPane.YES_OPTION) {
            tableModel.remover(linha);
            salvar();
        }
    }

    private void salvar() {
        wordRepository.salvar(tableModel.getPalavras());
    }
}
