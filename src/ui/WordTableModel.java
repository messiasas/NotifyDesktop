package ui;

import model.Word;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class WordTableModel extends AbstractTableModel {

    private static final String[] COLUNAS = {"Termo", "Tradução", "Exemplo"};

    private final List<Word> palavras;

    public WordTableModel(List<Word> palavras) {
        this.palavras = palavras;
    }

    @Override
    public int getRowCount() {
        return palavras.size();
    }

    @Override
    public int getColumnCount() {
        return COLUNAS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUNAS[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Word palavra = palavras.get(rowIndex);
        switch (columnIndex) {
            case 0: return palavra.getTermo();
            case 1: return palavra.getTraducao();
            case 2: return palavra.getExemploFrase();
            default: return null;
        }
    }

    public void limparTudo() {
        int quantidade = palavras.size();
        if (quantidade > 0) {
            palavras.clear();
            fireTableRowsDeleted(0, quantidade - 1);
        }
    }

    public Word getWordAt(int rowIndex) {
        return palavras.get(rowIndex);
    }

    public void adicionar(Word palavra) {
        palavras.add(palavra);
        int linha = palavras.size() - 1;
        fireTableRowsInserted(linha, linha);
    }

    public void atualizar(int rowIndex, Word palavra) {
        palavras.set(rowIndex, palavra);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void remover(int rowIndex) {
        palavras.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public List<Word> getPalavras() {
        return palavras;
    }
}