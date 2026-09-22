package ui;

import model.Settings;
import persistence.SettingsRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.Duration;

public class SettingsDialog extends JDialog {

    private final SettingsRepository settingsRepository;
    private final Settings settings;
    private final JSpinner campoIntervalo;

    private boolean alterado = false;

    public SettingsDialog(Frame owner, SettingsRepository settingsRepository) {
        super(owner, "Configurações", true);
        this.settingsRepository = settingsRepository;
        this.settings = settingsRepository.carregar();

        JLabel titulo = new JLabel("Configurações de notificação");
        titulo.setFont(UiTheme.FONT_TITLE.deriveFont(18f));
        titulo.setForeground(UiTheme.TEXT_PRIMARY);
        titulo.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel rotuloIntervalo = new JLabel("Mostrar uma notificação a cada quantos minutos?");
        rotuloIntervalo.setFont(UiTheme.FONT_BOLD);
        rotuloIntervalo.setForeground(UiTheme.TEXT_SECONDARY);

        long minutosAtuais = Math.max(1, settings.getIntervaloNotificacoes().toMinutes());
        campoIntervalo = new JSpinner(new SpinnerNumberModel((int) minutosAtuais, 1, 1440, 1));
        campoIntervalo.setFont(UiTheme.FONT_BASE);
        ((JSpinner.DefaultEditor) campoIntervalo.getEditor()).getTextField().setColumns(4);

        JLabel sufixoMinutos = new JLabel("minuto(s)");
        sufixoMinutos.setFont(UiTheme.FONT_BASE);
        sufixoMinutos.setForeground(UiTheme.TEXT_SECONDARY);

        JPanel linhaIntervalo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        linhaIntervalo.setOpaque(false);
        linhaIntervalo.add(campoIntervalo);
        linhaIntervalo.add(sufixoMinutos);

        JPanel painelCampos = new JPanel();
        painelCampos.setOpaque(false);
        painelCampos.setLayout(new BoxLayout(painelCampos, BoxLayout.Y_AXIS));
        rotuloIntervalo.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaIntervalo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelCampos.add(rotuloIntervalo);
        painelCampos.add(Box.createVerticalStrut(8));
        painelCampos.add(linhaIntervalo);

        JButton botaoSalvar = UiTheme.criarBotaoPrimario("Salvar");
        JButton botaoCancelar = UiTheme.criarBotaoNeutro("Cancelar");

        botaoSalvar.addActionListener(e -> salvar());
        botaoCancelar.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(botaoSalvar);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painelBotoes.setOpaque(false);
        painelBotoes.setBorder(new EmptyBorder(20, 0, 0, 0));
        painelBotoes.add(botaoCancelar);
        painelBotoes.add(botaoSalvar);

        JPanel conteudo = new JPanel(new BorderLayout());
        conteudo.setBackground(UiTheme.SURFACE);
        conteudo.setBorder(new EmptyBorder(20, 24, 20, 24));
        conteudo.add(titulo, BorderLayout.NORTH);
        conteudo.add(painelCampos, BorderLayout.CENTER);
        conteudo.add(painelBotoes, BorderLayout.SOUTH);

        setContentPane(conteudo);
        setResizable(false);
        pack();
        setLocationRelativeTo(owner);
    }

    private void salvar() {
        int minutos = (int) campoIntervalo.getValue();
        settings.setIntervaloNotificacoes(Duration.ofMinutes(minutos));
        settingsRepository.salvar(settings);
        alterado = true;
        dispose();
    }

    public boolean foiAlterado() {
        return alterado;
    }
}
