package scheduler;

import model.Word;

import java.util.List;

@FunctionalInterface
public interface NotificationCallback {
    /**
     * Solicita a exibição de uma palavra. {@code aoFechar} deve ser chamado quando a notificação
     * for fechada (automaticamente ou pelo usuário), para que a próxima palavra da rodada apareça.
     */
    void notificar(List<Word> palavras, int indiceAtual, Runnable aoFechar);
}
