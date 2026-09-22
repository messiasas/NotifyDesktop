package ui;

import model.Word;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class WordFormDialog extends JDialog {

    private final JTextField campoTermo = new JTextField(20);
    private final JTextField campoTraducao = new JTextField(20);
    private final JTextField campoExemplo = new JTextField(20);

    private Word resultado; // null se o usuário cancelar

    public WordFormDialog(Frame owner, Word palavraExistente) {
        super(owner, palavraExistente == null ? "Adicionar palavra" : "Editar palavra", true);

        if (palavraExistente != null) {
            campoTermo.setText(palavraExistente.getTermo());
            campoTraducao.setText(palavraExistente.getTraducao());
            campoExemplo.setText(palavraExistente.getExemploFrase());
        }

        for (JTextField campo : new JTextField[]{campoTermo, campoTraducao, campoExemplo}) {
            campo.setFont(UiTheme.FONT_BASE);
            campo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UiTheme.BORDER, 1, true),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        }

        JLabel titulo = new JLabel(palavraExistente == null ? "Nova palavra" : "Editar palavra");
        titulo.setFont(UiTheme.FONT_TITLE.deriveFont(18f));
        titulo.setForeground(UiTheme.TEXT_PRIMARY);
        titulo.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel painelCampos = new JPanel(new GridBagLayout());
        painelCampos.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0;
        gbc.weightx = 1;

        gbc.gridy = 0;
        painelCampos.add(rotulo("Termo"), gbc);
        gbc.gridy = 1;
        painelCampos.add(campoTermo, gbc);
        gbc.gridy = 2;
        painelCampos.add(rotulo("Tradução"), gbc);
        gbc.gridy = 3;
        painelCampos.add(campoTraducao, gbc);
        gbc.gridy = 4;
        painelCampos.add(rotulo("Exemplo (opcional)"), gbc);
        gbc.gridy = 5;
        painelCampos.add(campoExemplo, gbc);

        JButton botaoSalvar = UiTheme.criarBotaoPrimario("Salvar");
        JButton botaoCancelar = UiTheme.criarBotaoNeutro("Cancelar");

        botaoSalvar.addActionListener(e -> salvar(palavraExistente));
        botaoCancelar.addActionListener(e -> {
            resultado = null;
            dispose();
        });
        getRootPane().setDefaultButton(botaoSalvar);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painelBotoes.setOpaque(false);
        painelBotoes.setBorder(new EmptyBorder(16, 0, 0, 0));
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

    private JLabel rotulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(UiTheme.FONT_BOLD);
        label.setForeground(UiTheme.TEXT_SECONDARY);
        return label;
    }

    private void salvar(Word palavraExistente) {
        String termo = campoTermo.getText().trim();
        String traducao = campoTraducao.getText().trim();
        String exemplo = campoExemplo.getText().trim();

        if (termo.isEmpty() || traducao.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Termo e tradução são obrigatórios.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (palavraExistente == null) {
            resultado = new Word(termo, traducao, exemplo);
        } else {
            palavraExistente.setTermo(termo);
            palavraExistente.setTraducao(traducao);
            palavraExistente.setExemploFrase(exemplo);
            resultado = palavraExistente;
        }

        dispose();
    }

    public Word getResultado() {
        return resultado;
    }
}
